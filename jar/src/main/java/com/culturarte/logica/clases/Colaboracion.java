package com.culturarte.logica.clases;

import com.culturarte.logica.enums.TipoRetorno;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Colaboracion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    private float monto;
    private LocalDate fechaAporte;
    private LocalTime horaAporte;
    @Enumerated(EnumType.STRING)
    private TipoRetorno tipoRetorno;
    @ManyToOne(fetch = FetchType.EAGER)
    private Propuesta propuesta;
    @ManyToOne(fetch = FetchType.EAGER)
    private Colaborador colaborador;
   
    public Colaboracion(){}

    public Colaboracion(float monto, LocalDate fechaAporte, LocalTime horaAporte, TipoRetorno tipoRetorno, Propuesta propuesta, Colaborador colaborador) {
        this.monto = monto;
        this.fechaAporte = fechaAporte;
        this.tipoRetorno = tipoRetorno;
        this.propuesta = propuesta;
        this.colaborador = colaborador;
        this.horaAporte = horaAporte;
    }

    public LocalTime getHoraAporte() {
        return horaAporte;
    }

    public void setHoraAporte(LocalTime horaAporte) {
        this.horaAporte = horaAporte;
    }

    public int getId() {
        return id;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public LocalDate getFechaAporte() {
        return fechaAporte;
    }

    public void setFechaAporte(LocalDate fechaAporte) {
        this.fechaAporte = fechaAporte;
    }

    public TipoRetorno getTipoRetorno() {
        return tipoRetorno;
    }

    public void setTipoRetorno(TipoRetorno tipoRetorno) {
        this.tipoRetorno = tipoRetorno;
    }

    public Propuesta getPropuesta() {
        return propuesta;
    }

    public void setPropuesta(Propuesta propuesta) {
        this.propuesta = propuesta;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }
    
    public String getColaboradorNick() {
        return colaborador.getNickname();
    }
}
