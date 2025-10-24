package com.culturarte.logica.manejadores;

import com.culturarte.logica.clases.Categoria;
import jakarta.persistence.*;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ManejadorCategoria {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void alta(Categoria categoria) {
        em.persist(categoria);
    }

    @Transactional
    public void agregarSubcategoria(String nombre, String nombrePadre) {
        Categoria padre = em.find(Categoria.class, nombrePadre);
        if (padre != null) {
            Categoria nueva = new Categoria(nombre, padre);
            padre.addSubCategoria(nueva);
            em.persist(nueva);
        }
    }
    
    @Transactional
    public Categoria buscar(String nombre) {
        return em.find(Categoria.class, nombre);
    }
    
    @Transactional
    public List<Categoria> getCategoriasRaizConSubcategorias() {
        List<Categoria> categorias = em.createQuery("SELECT c FROM Categoria c", Categoria.class)
            .getResultList();
        categorias.forEach(c -> c.getSubCategorias().size()); // inicializa lazy
        return categorias;
    }

    @Transactional
    private void cargarSubcategorias(Categoria cat) {
        cat.getSubCategorias().size(); // fuerza carga
        for (Categoria sub : cat.getSubCategorias()) {
            cargarSubcategorias(sub);
        }
    }
}



