package com.culturarte.logica.manejadores;

import com.culturarte.logica.clases.Colaboracion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class ManejadorColaboracion {
    
    private static ManejadorColaboracion instancia = null;
    private EntityManagerFactory emf;
    
    private ManejadorColaboracion() {
        emf = Persistence.createEntityManagerFactory("DBculturarte");
    }
    
    public static ManejadorColaboracion getInstancia() {
        if (instancia == null)
            instancia = new ManejadorColaboracion();
        return instancia;
    }
    
    // 🔑 Alta de colaboración
    public void agregarColaboracion(Colaboracion colab) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(colab); 
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
    public void eliminarColaboracion(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Colaboracion colab = em.find(Colaboracion.class, id);
            if (colab == null) {
                throw new IllegalArgumentException("No existe la colaboración con id: " + id);
            }

            em.remove(colab);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // 🔑 Buscar colaboración por id
    public Colaboracion getColaboracion(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Colaboracion.class, id);
        } finally {
            em.close();
        }
    }
}
