package com.culturarte.logica.manejadores;
import com.culturarte.logica.clases.Colaboracion;
import com.culturarte.logica.clases.Proponente;
import com.culturarte.logica.clases.Propuesta;
import jakarta.persistence.*;
import com.culturarte.logica.clases.Usuario;
import java.util.List;
import java.util.ArrayList;
import com.culturarte.logica.datatypes.DTProponente;
import com.culturarte.logica.datatypes.DTPropuesta;
import java.time.LocalDateTime;
import java.time.LocalDate;


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

    @Transactional
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = em.createQuery("SELECT u FROM Usuario u", Usuario.class)
                                   .getResultList();
        // inicializar lazy si querés:
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

        p.setEliminado(true);
        p.setFechaEliminacion(LocalDate.now());

        em.merge(p);
    }
 /*
 @Transactional
public void eliminarProponente(String nick) {
    Proponente p = em.find(Proponente.class, nick);

    if (p == null) {
        throw new IllegalArgumentException("No existe proponente: " + nick);
    }

    // ============================
    // 1. Relaciones de seguidores
    // ============================

    // A) Usuarios que lo siguen
    for (Usuario seguidor : p.getUsuariosSeguidores()) {
        seguidor.getUsuariosSeguidos().remove(p);
        em.merge(seguidor);
    }

    // B) Usuarios que él sigue
    for (Usuario seguido : p.getUsuariosSeguidos()) {
        seguido.getUsuariosSeguidores().remove(p);
        em.merge(seguido);
    }

    p.getUsuariosSeguidores().clear();
    p.getUsuariosSeguidos().clear();


    /*
        // ============================
    // 2. Quitar favoritos de sus propuestas
    // ============================
    for (Propuesta prop : p.getPropuestas()) {
        for (Usuario u : prop.getUsuariosFavoritos()) {
            u.getPropuestasFavoritas().remove(prop);
            em.merge(u);
        }
        prop.getUsuariosFavoritos().clear();
    }

    // ============================
    // 3. Eliminar propuestas
    // ============================
    for (Propuesta prop : p.getPropuestas()) {
        em.remove(prop);
    }
    p.getPropuestas().clear();

    // ============================
    // 4. Eliminar proponente
    // ============================
    em.remove(p);

*/


    @Transactional
    public ArrayList<DTProponente> listarProponentesEliminados() {
        List<Proponente> eliminados = em.createQuery(
                "SELECT p FROM Proponente p WHERE p.eliminado = true",
                Proponente.class
        ).getResultList();

        ArrayList<DTProponente> dtProponentes = new ArrayList<>();

        for (Proponente p : eliminados) {
            p.getPropuestas().forEach(prop -> prop.getColaboraciones().size());

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
            dtp.setPropuestas(new ArrayList());
            for (Propuesta prop : p.getPropuestas()) {
                DTPropuesta dtProp = new DTPropuesta(
                        prop.getTitulo(),
                        prop.getDescripcion(),
                        prop.getLugar(),
                        prop.getFechaPrevista(),
                        prop.getPrecioEntrada(),
                        prop.getMontoNecesario()
                );
                dtp.addPropuesta(dtProp);
            }

            dtp.setFechaEliminacion(p.getFechaEliminacion());

            dtProponentes.add(dtp);
        }

        return dtProponentes;
    }



}



   
