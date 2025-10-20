package com.culturarte.logica.clases;
import java.time.LocalDate;
import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class Colaborador extends Usuario {
    @OneToMany(mappedBy="colaborador", fetch = FetchType.EAGER)
    private List<Colaboracion> colaboraciones;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios;

    public Colaborador() {}

    public Colaborador(String nickname, String password, String nombre, String apellido, String email, LocalDate fechaNacimiento, String imagen) {
        super(nickname, password, nombre, apellido, email, fechaNacimiento, imagen);
        this.colaboraciones = new ArrayList<>();
    }

    public List<Colaboracion> getColaboraciones() {
        return colaboraciones;
    }

    public void addColaboracion(Colaboracion colaboracion) {
        this.colaboraciones.add(colaboracion);
    }

    public void agregarComentario(Comentario comentario) {
        if (this.comentarios == null) {
            this.comentarios = new ArrayList<>();
        }
        this.comentarios.add(comentario);
    }

}
