package com.culturarte.logica.clases;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Usuario {
    @Id
    private String nickname;
    private String password;
    private String nombre;
    private String apellido;
    private String email;
    private LocalDate fechaNacimiento;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Propuesta> propuestasSeguidas;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Usuario> usuariosSeguidos;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Usuario> usuariosSeguidores;
    private String imagen;

    public Usuario() { }

    public Usuario(String nickname,String password, String nombre, String apellido, String email, LocalDate fechaNacimiento, String imagen) {
        this.nickname = nickname;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
        this.imagen = imagen;
        this.propuestasSeguidas = new ArrayList<>();
        this.usuariosSeguidos = new ArrayList<>();
        this.usuariosSeguidores = new ArrayList<>();
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public List<Usuario> getUsuariosSeguidos() {
        return usuariosSeguidos;
    }

    public void addUsuariosSeguidos(Usuario usuario) {
        this.usuariosSeguidos.add(usuario);
    }

    public List<Propuesta> getPropuestasSeguidas() {
        return propuestasSeguidas;
    }

    public void getPropuestasSeguida(Propuesta propuesta) {
        this.propuestasSeguidas.add(propuesta);
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

    public String getPassword() {return password;}

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setUsuariosSeguidos(List<Usuario> usuariosSeguidos) {
        this.usuariosSeguidos = usuariosSeguidos;
    }

    public void setPropuestasSeguidas(List<Propuesta> propuestasSeguidas) {
        this.propuestasSeguidas = propuestasSeguidas;
    }

    public List<Usuario> getUsuariosSeguidores() {
        return usuariosSeguidores;
    }

    public void setUsuariosSeguidores(List<Usuario> usuariosSeguidores) {
        this.usuariosSeguidores = usuariosSeguidores;
    }

    public void addUsuarioSeguidor(Usuario usuario) {
        this.usuariosSeguidores.add(usuario);
    }

    public void agregarPropuestaFavorita(Propuesta propuesta) {
        if (this.propuestasSeguidas == null) {
            this.propuestasSeguidas= new ArrayList<>();
        }

        for (Propuesta fav : this.propuestasSeguidas) {
            if (fav.getTitulo().equals(propuesta.getTitulo())) {
                throw new IllegalArgumentException("La propuesta ya está en favoritos");
            }
        }

        this.propuestasSeguidas.add(propuesta);
    }

    public void sacarPropuestaFavorita(Propuesta propuesta) {
        if (this.propuestasSeguidas == null) {
            this.propuestasSeguidas= new ArrayList<>();
        }

        if( this.propuestasSeguidas.contains(propuesta)) {
            this.propuestasSeguidas.remove(propuesta);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario u = (Usuario) o;
        return nickname != null && nickname.equals(u.getNickname());
    }

    @Override
    public int hashCode() {
        return nickname != null ? nickname.hashCode() : 0;
    }

}
