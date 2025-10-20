package com.culturarte.logica.datatypes;

import java.util.ArrayList;
import java.util.List;

public class DTCategoria {
    private String nombre;
    private List<DTCategoria> hijos = new ArrayList<>();

    public DTCategoria() {}

    public DTCategoria(String nombre) {
        this.nombre = nombre;
    }

    public DTCategoria(String nombre, List<DTCategoria> hijos) {
        this.nombre = nombre;
        this.hijos = hijos == null ? new ArrayList<>() : hijos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<DTCategoria> getHijos() {
        return hijos;
    }

    public void setHijos(List<DTCategoria> hijos) {
        this.hijos = hijos;
    }

    public void addHijo(DTCategoria hijo) {
        this.hijos.add(hijo);
    }
}

