package com.culturarte.logica.manejadores;

import com.culturarte.logica.clases.Propuesta;
import jakarta.persistence.*;
import org.hibernate.Hibernate;
import java.util.List;
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
        if (propuesta == null || propuesta.getTitulo() == null) return;

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
        if (propuesta == null) return;
        
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


}

