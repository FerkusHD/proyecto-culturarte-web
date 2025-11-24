package com.culturarte.logica.manejadores;

import com.culturarte.logica.clases.Propuesta;
import com.culturarte.logica.clases.Colaborador;
import com.culturarte.logica.clases.Colaboracion;
import com.culturarte.logica.clases.Usuario;
import jakarta.persistence.*;
import org.hibernate.Hibernate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ManejadorPropuesta {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void agregarPropuesta(Propuesta propuesta) {
        em.persist(propuesta);
    }

    @Transactional
    public Propuesta getPropuesta(String titulo) {
        Propuesta p = em.find(Propuesta.class, titulo);
        if (p != null) {
            Hibernate.initialize(p.getComentarios());
            Hibernate.initialize(p.getColaboraciones());
            Hibernate.initialize(p.getHistorialEstados());
        }
        return p;
    }

    @Transactional
    public List<Propuesta> getPropuestas() {
        TypedQuery<Propuesta> query = em.createQuery("SELECT p FROM Propuesta p", Propuesta.class);
        List<Propuesta> propuestas = query.getResultList();
        propuestas.forEach(this::forzarCargaLazy);
        return propuestas;
    }

    @Transactional
    public void sacarPropuesta(Propuesta propuesta) {
        if (propuesta == null || propuesta.getTitulo() == null)
            return;

        Propuesta p = em.find(Propuesta.class, propuesta.getTitulo());
        if (p != null) {
            em.remove(p);
        }
    }

    @Transactional
    public void actualizarPropuesta(Propuesta p) {
        em.merge(p);
    }

    private void forzarCargaLazy(Propuesta propuesta) {
        if (propuesta == null)
            return;

        if (propuesta.getColaboraciones() != null)
            propuesta.getColaboraciones().size();

        if (propuesta.getHistorialEstados() != null)
            propuesta.getHistorialEstados().size();

        if (propuesta.getProponente() != null)
            propuesta.getProponente().getNickname();

        if (propuesta.getCategoria() != null)
            propuesta.getCategoria().getNombre();
    }

    public List<Propuesta> buscarPropuestas(String texto) {
        String jpql = "SELECT p FROM Propuesta p";

        if (texto != null && !texto.trim().isEmpty()) {
            jpql += " WHERE LOWER(p.titulo) LIKE :texto OR LOWER(p.descripcion) LIKE :texto OR LOWER(p.lugar) LIKE :texto";
        }

        TypedQuery<Propuesta> query = em.createQuery(jpql, Propuesta.class);

        if (texto != null && !texto.trim().isEmpty()) {
            query.setParameter("texto", "%" + texto.toLowerCase() + "%");
        }

        List<Propuesta> resultados = query.getResultList();
        resultados.forEach(this::forzarCargaLazy);
        return resultados;
    }

    /**
     * Obtiene las 10 mejores propuestas recomendadas para un colaborador
     * basándose en la fórmula: Puntaje(Pi) = colaboradores(Pi) +
     * puntajeFinanciacion(Pi) + favoritosCon(Pi)
     * 
     * @param nicknameColaborador Nickname del colaborador para quien se generan las
     *                            recomendaciones
     * @return Lista de hasta 10 propuestas recomendadas ordenadas por puntaje
     *         descendente
     */
    @Transactional
    public List<Propuesta> obtenerRecomendaciones(String nicknameColaborador) {
        // Obtener el colaborador
        Colaborador colaborador = em.find(Colaborador.class, nicknameColaborador);
        if (colaborador == null) {
            return new ArrayList<>();
        }

        // Set para almacenar propuestas candidatas (evitar duplicados)
        Set<String> propuestasCandidatas = new HashSet<>();

        // Set de propuestas en las que ya colaboró (para excluirlas)
        Set<String> propuestasYaColaboradas = colaborador.getColaboraciones().stream()
                .map(c -> c.getPropuesta().getTitulo())
                .collect(Collectors.toSet());

        // 1. Agregar propuestas de usuarios que sigue el colaborador
        for (Usuario usuarioSeguido : colaborador.getUsuariosSeguidos()) {
            if (usuarioSeguido instanceof Colaborador) {
                Colaborador colabSeguido = (Colaborador) usuarioSeguido;
                for (Colaboracion colab : colabSeguido.getColaboraciones()) {
                    String tituloPropuesta = colab.getPropuesta().getTitulo();
                    if (!propuestasYaColaboradas.contains(tituloPropuesta)) {
                        propuestasCandidatas.add(tituloPropuesta);
                    }
                }
            }
        }

        // 2. Agregar propuestas de usuarios que colaboraron en las mismas propuestas
        for (Colaboracion miColaboracion : colaborador.getColaboraciones()) {
            Propuesta propuesta = miColaboracion.getPropuesta();

            // Por cada colaboración en esa propuesta
            for (Colaboracion otraColaboracion : propuesta.getColaboraciones()) {
                Colaborador otroColaborador = otraColaboracion.getColaborador();

                // Obtener las propuestas de ese otro colaborador
                if (otroColaborador != null && !otroColaborador.getNickname().equals(nicknameColaborador)) {
                    for (Colaboracion colabOtro : otroColaborador.getColaboraciones()) {
                        String tituloPropuesta = colabOtro.getPropuesta().getTitulo();
                        if (!propuestasYaColaboradas.contains(tituloPropuesta)) {
                            propuestasCandidatas.add(tituloPropuesta);
                        }
                    }
                }
            }
        }

        // 3. Calcular puntajes para cada propuesta candidata
        Map<String, Integer> puntajes = new HashMap<>();

        for (String tituloPropuesta : propuestasCandidatas) {
            Propuesta propuesta = getPropuesta(tituloPropuesta);
            if (propuesta != null) {
                int puntaje = calcularPuntajePropuesta(propuesta);
                puntajes.put(tituloPropuesta, puntaje);
            }
        }

        // 4. Ordenar por puntaje descendente y tomar las 10 mejores
        List<String> topTitulos = puntajes.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 5. Obtener las propuestas completas
        List<Propuesta> recomendaciones = new ArrayList<>();
        for (String titulo : topTitulos) {
            Propuesta p = getPropuesta(titulo);
            if (p != null) {
                forzarCargaLazy(p);
                recomendaciones.add(p);
            }
        }

        return recomendaciones;
    }

    /**
     * Calcula el puntaje de una propuesta según la fórmula:
     * Puntaje(Pi) = colaboradores(Pi) + puntajeFinanciacion(Pi) + favoritosCon(Pi)
     */
    private int calcularPuntajePropuesta(Propuesta propuesta) {
        // 1. Cantidad de colaboradores
        int cantidadColaboradores = propuesta.getColaboraciones() != null ? propuesta.getColaboraciones().size() : 0;

        // 2. Puntaje de financiación
        int puntajeFinanciacion = calcularPuntajeFinanciacion(propuesta);

        // 3. Cantidad de usuarios que tienen la propuesta como favorita
        int favoritosCon = contarFavoritos(propuesta);

        return cantidadColaboradores + puntajeFinanciacion + favoritosCon;
    }

    /**
     * Calcula el puntaje de financiación según el porcentaje obtenido:
     * - 0% ≤ financiación ≤ 25% => puntaje = 1
     * - 25% < financiación ≤ 50% => puntaje = 2
     * - 50% < financiación ≤ 75% => puntaje = 3
     * - 75% < financiación ≤ 100% => puntaje = 4
     */
    private int calcularPuntajeFinanciacion(Propuesta propuesta) {
        float montoNecesario = propuesta.getMontoNecesario();
        if (montoNecesario <= 0) {
            return 1; // Si no hay monto necesario, puntaje mínimo
        }

        float montoRecaudado = propuesta.getMontoRecaudado();
        float porcentaje = (montoRecaudado / montoNecesario) * 100;

        if (porcentaje <= 25) {
            return 1;
        } else if (porcentaje <= 50) {
            return 2;
        } else if (porcentaje <= 75) {
            return 3;
        } else {
            return 4;
        }
    }

    /**
     * Cuenta cuántos usuarios tienen la propuesta como favorita
     */
    private int contarFavoritos(Propuesta propuesta) {
        String jpql = "SELECT COUNT(u) FROM Usuario u JOIN u.propuestasSeguidas p WHERE p.titulo = :titulo";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("titulo", propuesta.getTitulo());
        Long count = query.getSingleResult();
        return count != null ? count.intValue() : 0;
    }

}
