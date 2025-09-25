/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.culturarte.logica.datatypes;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 *
 * @author pablo
 */
public class DTProponente {
    private String nickname;
    private String nombre; 
    private String apellido;
    private String email;
    private LocalDate fechaNacimiento;
    private File imagen;
    private String direccion;
    private String biografia;
    private String linkWeb;
    private ArrayList<DTPropuesta> propuestas;
    
    public DTProponente() {}
    
    public DTProponente(String nickname, String nombre, String apellido, String email, LocalDate fechaNacimiento, File imagen, String direccion, String linkWeb, String biografia) {
        this.nickname = nickname;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
        this.imagen = imagen;
        this.direccion = direccion;
        this.linkWeb = linkWeb;
        this.biografia = biografia;
        this.propuestas = new ArrayList<>();
    }

    //Getters
    public String getNickname() {
        return nickname;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public String getApellido() { 
        return apellido;
    }
    
    public String getEmail(){
        return email;
    }
    
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    
    public File getImagen() {
        return imagen;
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
    
    public ArrayList<DTPropuesta> getPropuestas() {
        return propuestas;
    }

    //Setters
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
    
    public void setImagen(File imagen) {
        this.imagen = imagen;
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
    
    public void addPropuesta(DTPropuesta propuesta) {
        propuestas.add(propuesta);
    }
    
    @Override
    public String toString(){
        String fecha = fechaNacimiento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return nickname + " - " + nombre + " - " + apellido + " - " + email + " - (" + fecha + ") - " + direccion + " - " + biografia + " - " + linkWeb;
    }
}
