package com.culturarte.logica.manejadores;

import com.culturarte.logica.clases.Categoria;
import jakarta.persistence.*;
import java.util.List;

public class ManejadorCategoria {

    private static ManejadorCategoria instancia = null;
    private EntityManagerFactory emf;

    private ManejadorCategoria() {
        emf = Persistence.createEntityManagerFactory("DBculturarte");
    }

    public static ManejadorCategoria getInstancia() {
        if (instancia == null) {
            instancia = new ManejadorCategoria();
        }
        return instancia;
    }

    public void alta(Categoria categoria) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(categoria);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void agregarSubcategoria(String nombre, String nombrePadre) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Categoria padre = em.find(Categoria.class, nombrePadre);
            if (padre != null) {
                Categoria nueva = new Categoria(nombre, padre);
                padre.addSubCategoria(nueva);
                em.persist(nueva);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public Categoria buscar(String nombre) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Categoria.class, nombre);
        } finally {
            em.close();
        }
    }

    public List<Categoria> getCategoriasRaizConSubcategorias() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Categoria> q = em.createQuery(
                "SELECT DISTINCT c FROM Categoria c LEFT JOIN FETCH c.subCategorias WHERE c.padre IS NULL",
                Categoria.class
            );
            List<Categoria> raiz = q.getResultList();
            // Si quieres carga recursiva:
            for (Categoria c : raiz) {
                cargarSubcategorias(c);
            }
            return raiz;
        } finally {
            em.close();
        }
    }

    private void cargarSubcategorias(Categoria cat) {
        cat.getSubCategorias().size(); // fuerza carga
        for (Categoria sub : cat.getSubCategorias()) {
            cargarSubcategorias(sub);
        }
    }
}



