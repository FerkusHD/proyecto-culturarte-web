/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.culturarte.logica.datatypes;

import com.culturarte.logica.clases.Usuario;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author pablo
 */
public class DTUsuario {
    private String nickname;
    private String nombre;
    private String apellido;
    private String email;
    private LocalDate fechaNacimiento;
    private ArrayList<String> usuariosSeguidos;
    private File imagen;
    
    public DTUsuario(){}
    
    public DTUsuario(String nickname, String nombre, String apellido, String email, LocalDate fechaNacimiento, ArrayList<String> usuariosSeguidos, File imagen){
        this.nickname = nickname;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
        this.imagen = imagen;
        this.usuariosSeguidos = usuariosSeguidos;
    }
    
    public DTUsuario(String nickname, String nombre, ArrayList<String> usuariosSeguidos){
        this.nickname = nickname;
        this.nombre = nombre;
        this.usuariosSeguidos = usuariosSeguidos;
    }
    
    public File getImagen() {
        return imagen;
    }

    public void setImagen(File imagen) {
        this.imagen = imagen;
    }

    public ArrayList<String> getUsuariosSeguidos() {
        return usuariosSeguidos;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
