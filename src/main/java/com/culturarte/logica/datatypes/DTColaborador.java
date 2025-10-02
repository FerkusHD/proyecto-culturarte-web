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
    
    
    
}
