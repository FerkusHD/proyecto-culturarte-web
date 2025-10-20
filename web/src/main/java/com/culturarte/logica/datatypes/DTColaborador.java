/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.culturarte.logica.datatypes;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author maicol
 */
public class DTColaborador {
    private String nickname;
    private String password;
    private String nombre;
    private String apellido;
    private String email;
    private LocalDate fechaNacimiento;
    private String imagen;
    private ArrayList<DTPropuesta> propuestas;
    private ArrayList<DTColaboracion> colaboraciones;

    public DTColaborador(String nickname, String password, String nombre, String apellido, String email, LocalDate fechaNacimiento, String imagen) {
        this.nickname = nickname;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
        this.imagen = imagen;
        this.propuestas = new ArrayList<>();
    }

    public String getNickname() {
        return nickname;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getImagen() {
        return imagen;
    }

    public ArrayList<DTPropuesta> getPropuestas() {
        return propuestas;
    }
    
    public void addPropuesta(DTPropuesta p) {
        this.propuestas.add(p);
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setPropuestas(ArrayList<DTPropuesta> propuestas) {
        this.propuestas = propuestas;
    }

    public ArrayList<DTColaboracion> getColaboraciones() {
        return colaboraciones;
    }

    public void setColaboraciones(ArrayList<DTColaboracion> colaboraciones) {
        this.colaboraciones = colaboraciones;
    }
}
