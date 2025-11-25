package com.culturarte.logica.manejadores;

import com.culturarte.logica.clases.Proponente;

import jakarta.persistence.*;
import com.culturarte.logica.clases.Usuario;
import java.util.List;
import java.util.ArrayList;
import com.culturarte.logica.clases.Proponente;
import com.culturarte.logica.clases.Colaboracion;
import com.culturarte.logica.clases.Colaborador;
import com.culturarte.logica.clases.Propuesta;
import com.culturarte.logica.datatypes.DTProponente;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ManejadorUsuario {

    @PersistenceContext
    private EntityManager em;

    private final com.culturarte.logica.persistencia.ProponenteEliminadoPersistencia persistencia;

    public ManejadorUsuario(com.culturarte.logica.persistencia.ProponenteEliminadoPersistencia persistencia) {
        this.persistencia = persistencia;
    }

    @Transactional
    public void agregarUsuario(Usuario usuario) {
        em.persist(usuario);
    }

    @Transactional
    public Usuario buscarUsuario(String nick) {
        Usuario u = em.find(Usuario.class, nick);
        if (u != null) {
            u.getUsuariosSeguidos().size();
        }
        return u;
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        try {
            Usuario u = em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                    .setParameter("email", email)
                    .getSingleResult();
            if (u != null) {
                u.getUsuariosSeguidos().size();
            }
            return u;
        } catch (NoResultException e) {
            return null;
        }
    }

    public Proponente getProponenteConPropuestas(String nick) {
        Proponente p = em.find(Proponente.class, nick);
        if (p != null) {
            p.getPropuestas().forEach(propuesta -> propuesta.getColaboraciones().size());
        }
        return p;
    }

    @Transactional
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = em.createQuery("SELECT u FROM Usuario u", Usuario.class)
                .getResultList();
        usuarios.forEach(u -> u.getUsuariosSeguidos().size());
        return usuarios;
    }

    @Transactional
    public void actualizarUsuario(Usuario usuario) {
        em.merge(usuario);
    }

    public List<Usuario> buscarUsuarios(String nombre) {
        String jpql = "SELECT u FROM Usuario u";
        TypedQuery<Usuario> query;

        if (nombre != null && !nombre.trim().isEmpty()) {
            jpql += " WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) " +
                    "OR LOWER(u.nickname) LIKE LOWER(CONCAT('%', :nombre, '%'))";
            query = em.createQuery(jpql, Usuario.class);
            query.setParameter("nombre", nombre);
        } else {
            query = em.createQuery(jpql, Usuario.class);
        }

        List<Usuario> usuarios = query.getResultList();

        usuarios.forEach(u -> u.getUsuariosSeguidos().size());

        return usuarios;
    }

    @Transactional
    public void eliminarProponente(String nick) {
        Proponente p = em.find(Proponente.class, nick);

        if (p == null) {
            throw new IllegalArgumentException("No existe proponente: " + nick);
        }

        if (p.getPropuestas() != null) {
            p.getPropuestas().forEach(propuesta -> {
                if (propuesta.getColaboraciones() != null) {
                    propuesta.getColaboraciones().size();
                }
            });
        }

        DTProponente dtp = new DTProponente();
        dtp.setNickname(p.getNickname());
        dtp.setNombre(p.getNombre());
        dtp.setApellido(p.getApellido());
        dtp.setEmail(p.getEmail());
        dtp.setFechaNacimiento(p.getFechaNacimiento());
        dtp.setImagen(p.getImagen());
        dtp.setDireccion(p.getDireccion());
        dtp.setLinkWeb(p.getLinkWeb());
        dtp.setBiografia(p.getBiografia());
        dtp.setPropuestas(new ArrayList<>());

        if (p.getPropuestas() != null) {
            for (Propuesta prop : p.getPropuestas()) {
                float montoRecaudado = prop.getMontoRecaudado();

                com.culturarte.logica.enums.TipoEstado estadoActual = null;
                try {
                    if (prop.getHistorialEstados() != null && !prop.getHistorialEstados().isEmpty()) {
                        estadoActual = prop.getEstadoActual().getEstado();
                    }
                } catch (Exception e) {
                }

                ArrayList<String> colaboradores = new ArrayList<>();
                if (prop.getColaboraciones() != null) {
                    for (Colaboracion colab : prop.getColaboraciones()) {
                        colaboradores.add(colab.getColaborador().getNickname());
                    }
                }

                com.culturarte.logica.datatypes.DTPropuesta dtProp = new com.culturarte.logica.datatypes.DTPropuesta(
                        prop.getTitulo(),
                        estadoActual,
                        colaboradores,
                        montoRecaudado,
                        prop.getMontoNecesario());

                dtp.addPropuesta(dtProp);
            }
        }

        try {
            persistencia.guardarProponenteEliminado(dtp);
        } catch (Exception e) {
            throw new RuntimeException("Error al persistir proponente eliminado en disco: " + e.getMessage(), e);
        }

        if (p.getPropuestas() != null) {
            List<Propuesta> propuestas = new ArrayList<>(p.getPropuestas());
            for (Propuesta prop : propuestas) {
                if (prop.getColaboraciones() != null) {
                    List<Colaboracion> colaboraciones = new ArrayList<>(
                            prop.getColaboraciones());
                    for (Colaboracion colab : colaboraciones) {
                        if (colab.getColaborador() != null) {
                            colab.getColaborador().getColaboraciones().remove(colab);
                            em.merge(colab.getColaborador());
                        }
                        em.remove(colab);
                    }
                    prop.getColaboraciones().clear();
                }

                em.remove(prop);
            }
            p.getPropuestas().clear();
        }

        if (p.getUsuariosSeguidos() != null) {
            for (Usuario seguido : new ArrayList<>(p.getUsuariosSeguidos())) {
                seguido.getUsuariosSeguidores().remove(p);
            }
            p.getUsuariosSeguidos().clear();
        }

        if (p.getUsuariosSeguidores() != null) {
            for (Usuario seguidor : new ArrayList<>(p.getUsuariosSeguidores())) {
                seguidor.getUsuariosSeguidos().remove(p);
            }
            p.getUsuariosSeguidores().clear();
        }

        if (p.getPropuestasSeguidas() != null) {
            p.getPropuestasSeguidas().clear();
        }

        em.flush();

        em.remove(p);
    }

    @Transactional
    public ArrayList<DTProponente> listarProponentesEliminados() {
        return persistencia.listarProponentesEliminados();
    }

}
