package com.culturarte.logica.clases;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Entity
public class Proponente extends Usuario {
    private String direccion;
    @Column(length = 3000)
    private String biografia;
    private String linkWeb;
    @OneToMany(mappedBy="proponente", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Propuesta> propuestas;
    private boolean eliminado=false;
    private LocalDate fechaEliminacion=null;
    
    public Proponente() {}

    public Proponente(String nickname, String password, String nombre, String apellido, String email, LocalDate fechaNacimiento, String imagen, String direccion, String linkWeb, String biografia) {
        super(nickname, password, nombre, apellido, email, fechaNacimiento, imagen);
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

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }
    public boolean getEliminado() {
        return eliminado;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setFechaEliminacion(LocalDate fechaEliminacion){
        this.fechaEliminacion = fechaEliminacion;
    }

    public LocalDate getFechaEliminacion() {
        return fechaEliminacion;
    }
}
