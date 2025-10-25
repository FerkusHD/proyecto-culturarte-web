package com.culturarte.logica.clases;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Categoria {

    @Id
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "padre_nombre")
    private Categoria padre;

    @OneToMany(mappedBy = "padre", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Categoria> subCategorias = new ArrayList<>();

    public Categoria() {}

    public Categoria(String nombre, Categoria padre) {
        this.nombre = nombre;
        this.padre = padre;
    }
    
    public String getNombreCompleto() {
        if(this.getPadre() != null) {
            return this.padre.getNombreCompleto() + ", " + this.getNombre();
        } else {
            return this.getNombre();
        }
        
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Categoria getPadre() { return padre; }
    public void setPadre(Categoria padre) { this.padre = padre; }

    public List<Categoria> getSubCategorias() { return subCategorias; }
    public void addSubCategoria(Categoria sub) { subCategorias.add(sub); }
}


