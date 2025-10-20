package com.culturarte.logica.manejadores;
import com.culturarte.logica.clases.Proponente;
import jakarta.persistence.*;
import com.culturarte.logica.clases.Usuario;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ManejadorUsuario {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void agregarUsuario(Usuario usuario) {
        em.persist(usuario);
    }

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
            // Inicializar la colección si es lazy
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
            p.getPropuestas().forEach(propuesta ->
                    propuesta.getColaboraciones().size()
            );
        }
        return p;
    }

    public List<Usuario> listarUsuarios() {
        TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u", Usuario.class);
        List<Usuario> usuarios = query.getResultList();
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


}



   
