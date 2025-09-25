package com.culturarte.logica.manejadores;

import com.culturarte.logica.clases.Propuesta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class ManejadorPropuesta {

    private static ManejadorPropuesta instancia = null;
    private EntityManagerFactory emf;

    private ManejadorPropuesta() {
        emf = Persistence.createEntityManagerFactory("DBculturarte");
    }

    public static ManejadorPropuesta getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorPropuesta();
        }
        return instancia;
    }

    // Agregar propuesta
    public void agregarPropuesta(Propuesta propuesta) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(propuesta);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }


    public Propuesta getPropuesta(String titulo) {
        if (titulo == null) return null;

        EntityManager em = emf.createEntityManager();
        try {
            Propuesta p = em.find(Propuesta.class, titulo);
            if (p != null) {
                forzarCargaLazy(p);
            }
            return p;
        } finally {
            em.close();
        }
    }

    public List<Propuesta> getPropuestas() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Propuesta> query = em.createQuery("SELECT p FROM Propuesta p", Propuesta.class);
            List<Propuesta> propuestas = query.getResultList();
            propuestas.forEach(this::forzarCargaLazy);
            return propuestas;
        } finally {
            em.close();
        }
    }

    public void sacarPropuesta(Propuesta propuesta) {
        if (propuesta == null || propuesta.getTitulo() == null) return;

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Propuesta p = em.find(Propuesta.class, propuesta.getTitulo());
            if (p != null) {
                em.remove(p);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
        public void actualizarPropuesta(Propuesta p) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(p);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
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
    
}

