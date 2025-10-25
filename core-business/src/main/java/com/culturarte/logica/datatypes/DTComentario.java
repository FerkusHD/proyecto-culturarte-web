package com.culturarte.logica.datatypes;

import com.culturarte.logica.clases.Colaborador;
import com.culturarte.logica.clases.Propuesta;
import jakarta.persistence.*;

import java.time.LocalDate;

public class DTComentario {
    private int id;
    private String texto;
    private DTColaborador colaborador;
    private DTPropuesta propuesta;
    private LocalDate fecha;

    public DTComentario() {}

    public DTComentario(String texto, DTColaborador colaborador, DTPropuesta propuesta, LocalDate fecha) {
        this.texto = texto;
        this.colaborador = colaborador;
        this.propuesta = propuesta;
        this.fecha = fecha;
    }

    public String getTexto() {
        return texto;
    }

    public DTColaborador getColaborador() {
        return colaborador;
    }

    public DTPropuesta getPropuesta() {
        return propuesta;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setColaborador(DTColaborador colaborador) {
        this.colaborador = colaborador;
    }

    public void setPropuesta(DTPropuesta propuesta) {
        this.propuesta = propuesta;
    }

    public LocalDate getFecha() { return fecha;}

    public void setFecha(LocalDate fecha) { this.fecha = fecha; }


}
