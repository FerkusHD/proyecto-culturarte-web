package com.culturarte.logica.datatypes;

import com.culturarte.logica.clases.Colaborador;
import com.culturarte.logica.clases.Propuesta;
import jakarta.persistence.*;

import java.time.LocalDate;

public class DTComentario {
    private int id;
    private String texto;
    private Colaborador colaborador;
    private Propuesta propuesta;
    private LocalDate fecha;

    public DTComentario() {}

    public DTComentario(String texto, Colaborador colaborador, Propuesta propuesta, LocalDate fecha) {
        this.texto = texto;
        this.colaborador = colaborador;
        this.propuesta = propuesta;
        this.fecha = fecha;
    }

    public String getTexto() {
        return texto;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public Propuesta getPropuesta() {
        return propuesta;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public void setPropuesta(Propuesta propuesta) {
        this.propuesta = propuesta;
    }

    public LocalDate getFecha() { return fecha;}

    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
}
