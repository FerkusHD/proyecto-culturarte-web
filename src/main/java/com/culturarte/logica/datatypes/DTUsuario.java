/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.culturarte.logica.datatypes;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author pablo
 */
public class DTUsuario {
    private String nickname;
    private String password;
    private String nombre;
    private String apellido;
    private String email;
    private LocalDate fechaNacimiento;
    private ArrayList<DTUsuario> usuariosSeguidos;
    private ArrayList<DTUsuario> usuariosSeguidores;
    private ArrayList<DTPropuesta> propuestasSeguidas;
    private String imagen;
    private String tipo;
    
    public DTUsuario(){}
    
    public DTUsuario(String nickname, String nombre, String apellido, String email, LocalDate fechaNacimiento, ArrayList<DTUsuario> usuariosSeguidos, ArrayList<DTUsuario> usuariosSeguidores, String tipo, String imagen, ArrayList<DTPropuesta> propuestasSeguidas) {
        this.nickname = nickname;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
        this.imagen = imagen;
        this.usuariosSeguidos = usuariosSeguidos;
        this.usuariosSeguidores = usuariosSeguidores;
        this.propuestasSeguidas = propuestasSeguidas;
        this.tipo = tipo;
    }

    public DTUsuario(String nickname, String nombre, String apellido, String email, LocalDate fechaNacimiento, ArrayList<String> usuariosSeguidos, String imagen){
        this.nickname = nickname;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
        this.imagen = imagen;
        //this.usuariosSeguidos = usuariosSeguidos;
    }
    
    public DTUsuario(String nickname, String nombre, ArrayList<String> usuariosSeguidos){
        this.nickname = nickname;
        this.nombre = nombre;
        //this.usuariosSeguidos = usuariosSeguidos;
    }

    public DTUsuario(String nickname, String tipo, String imagen){
        this.nickname = nickname;
        this.tipo = tipo;
        this.imagen = imagen;
    }

    public DTUsuario(com.culturarte.logica.clases.Usuario u) {
        this.nickname = u.getNickname();
        this.nombre = u.getNombre();
        this.apellido = u.getApellido();
        this.email = u.getEmail();
        this.fechaNacimiento = u.getFechaNacimiento();
        this.imagen = u.getImagen();
        this.usuariosSeguidos = new ArrayList<>();
        if (u instanceof com.culturarte.logica.clases.Proponente) {
            this.tipo = "proponente";
        } else{
            this.tipo = "colaborador";
        }
    }




    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public ArrayList<DTUsuario> getUsuariosSeguidos() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsuariosSeguidos(ArrayList<DTUsuario> usuariosSeguidos) {
        this.usuariosSeguidos = usuariosSeguidos;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void addUsuariosSeguidos(DTUsuario seguido){
        this.usuariosSeguidos.add(seguido);
    }

    public void addUsuariosSeguidores(DTUsuario seguidor){
        this.usuariosSeguidores.add(seguidor);
    }

    public ArrayList<DTUsuario> getUsuariosSeguidores() {
        return usuariosSeguidores;
    }

    public void setUsuariosSeguidores(ArrayList<DTUsuario> usuariosSeguidores) {
        this.usuariosSeguidores = usuariosSeguidores;
    }

    public ArrayList<DTPropuesta> getPropuestasSeguidas() {
        return propuestasSeguidas;
    }

    public void setPropuestasSeguidas(ArrayList<DTPropuesta> propuestasSeguidas) {
        this.propuestasSeguidas = propuestasSeguidas;
    }

    public boolean buscarUsuarioSeguido(String seguido) {
        if (this.usuariosSeguidos == null ) return false;
        for (DTUsuario u : this.usuariosSeguidos) {
            if (u.getNickname().equals(seguido)) {
                return true;
            }
        }
        return false;
    }

    public boolean buscarPropuestaFavorita(String titulo) {
        if (this.propuestasSeguidas == null ) return false;
        for (DTPropuesta p : this.propuestasSeguidas) {
            if (p.getTitulo().equals(titulo)) {
                return true;
            }
        }
        return false;
    }
}
