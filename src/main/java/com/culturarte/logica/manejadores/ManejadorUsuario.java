package com.culturarte.logica.manejadores;
import com.culturarte.logica.clases.Proponente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import com.culturarte.logica.clases.Usuario;
import java.util.List;
import jakarta.persistence.TypedQuery;


public class ManejadorUsuario {
    private static ManejadorUsuario instancia = null;
    private EntityManagerFactory emf;
    
    private ManejadorUsuario() {
        emf = Persistence.createEntityManagerFactory("DBculturarte"); ;
    }

    public static ManejadorUsuario getInstance() {
        if (instancia == null)
            instancia = new ManejadorUsuario();
        return instancia;
    }

    public void agregarUsuario(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Usuario buscarUsuario(String nick) { 
        EntityManager em = emf.createEntityManager();
        try {
            Usuario u = em.find(Usuario.class, nick);
            if (u != null) {
                // Fuerza la carga de la colección
                u.getUsuariosSeguidos().size();
            }
            return u;
        } finally {
            em.close();
        }
    }
    
    public Proponente getProponenteConPropuestas(String nick) {
        EntityManager em = emf.createEntityManager();
        try {
            Proponente p = em.find(Proponente.class, nick);

            // 🔑 inicializa antes de cerrar el EntityManager
            p.getPropuestas().forEach(propuesta -> 
                propuesta.getColaboraciones().size()
            );

            return p; 
        } finally {
            em.close();
        }
    }

    public List<Usuario> listarUsuarios() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery("SELECT u FROM Usuario u", Usuario.class);
            List<Usuario> usuarios = query.getResultList();

            // Forzar carga de colecciones si son lazy
            usuarios.forEach(u -> u.getUsuariosSeguidos().size());

            return usuarios;
        } finally {
            em.close();
        }
    }
    
    public void actualizarUsuario(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(usuario);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    
}



   
