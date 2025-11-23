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

    /**
     * Obtiene las colaboraciones sin pago de un colaborador
     */
    public java.util.List<Colaboracion> getColaboracionesSinPago(String nickColaborador) {
        TypedQuery<Colaboracion> query = em.createQuery(
                "SELECT c FROM Colaboracion c WHERE c.colaborador.nickname = :nick AND c.pago IS NULL",
                Colaboracion.class);
        query.setParameter("nick", nickColaborador);
        return query.getResultList();
    }

    /**
     * Actualiza una colaboración
     */
    @Transactional
    public void actualizarColaboracion(Colaboracion colab) {
        em.merge(colab);
    }
}
