package com.culturarte.logica.clases;

import jakarta.persistence.*;

public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String texto;
    @ManyToOne(fetch = FetchType.EAGER)
    private Colaborador colaborador;
    @ManyToOne(fetch = FetchType.EAGER)
    private Propuesta propuesta;

    public Comentario() {}

    public Comentario(String texto, Colaborador colaborador, Propuesta propuesta) {
        this.texto = texto;
        this.colaborador = colaborador;
        this.propuesta = propuesta;
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
}
