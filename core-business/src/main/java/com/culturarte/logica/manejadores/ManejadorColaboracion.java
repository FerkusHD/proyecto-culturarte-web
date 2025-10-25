package com.culturarte.logica.manejadores;

import com.culturarte.logica.clases.Colaboracion;
import jakarta.persistence.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ManejadorColaboracion {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void agregarColaboracion(Colaboracion colab) {
        em.persist(colab);
    }

    @Transactional
    public void eliminarColaboracion(int id) {
        Colaboracion colab = em.find(Colaboracion.class, id);
        if (colab == null) {
            throw new IllegalArgumentException("No existe la colaboración con id: " + id);
        }
        em.remove(colab);
    }

    public Colaboracion getColaboracion(Long id) {
        return em.find(Colaboracion.class, id);
    }
}
