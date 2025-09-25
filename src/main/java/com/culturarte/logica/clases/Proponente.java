package com.culturarte.logica.clases;

import jakarta.persistence.*;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class Proponente extends Usuario {
    private String direccion;
    @Column(length = 3000)
    private String biografia;
    private String linkWeb;
    @OneToMany(mappedBy="proponente", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Propuesta> propuestas;
    
    public Proponente() {}

    public Proponente(String nickname, String nombre, String apellido, String email, LocalDate fechaNacimiento, File imagen, String direccion, String linkWeb, String biografia) {
        super(nickname, nombre, apellido, email, fechaNacimiento, imagen);
        this.direccion = direccion;
        this.linkWeb = linkWeb;
        this.biografia = biografia;
        this.propuestas = new ArrayList<>();
    }

    public String getDireccion() {

        return direccion;
    }

    public String getBiografia() {

        return biografia;
    }

    public String getLinkWeb() {

        return linkWeb;
    }

    public List<Propuesta> getPropuestas() {
        return propuestas;
    }

    public void setDireccion(String direccion) {

        this.direccion = direccion;
    }

    public void setBiografia(String biografia) {

        this.biografia = biografia;
    }

    public void setLinkWeb(String linkWeb) {

        this.linkWeb = linkWeb;
    }

    public void addPropuestas(Propuesta propuesta) {
        this.propuestas.add(propuesta);
    }
    
    public void removePropuesta(Propuesta propuesta) {
        this.propuestas.remove(propuesta);
    }

}
