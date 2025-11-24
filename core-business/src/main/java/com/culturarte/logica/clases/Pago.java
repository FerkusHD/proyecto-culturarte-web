package com.culturarte.logica.clases;

import com.culturarte.logica.enums.TipoPago;
import com.culturarte.logica.enums.TipoTarjeta;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private float monto;
    private LocalDate fechaPago;
    private LocalTime horaPago;

    @Enumerated(EnumType.STRING)
    private TipoPago tipoPago;

    // Campos para pago con tarjeta
    @Enumerated(EnumType.STRING)
    private TipoTarjeta tipoTarjeta;
    private String numeroTarjeta;
    private String fechaVencimiento; // Formato: MM/YY
    private String cvc;

    // Campos comunes
    private String nombreTitular;

    // Campos para transferencia bancaria
    private String nombreBanco;
    private String numeroCuenta;

    // Relación con Colaboracion
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "colaboracion_id")
    private Colaboracion colaboracion;

    public Pago() {
    }

    public Pago(float monto, LocalDate fechaPago, LocalTime horaPago, TipoPago tipoPago,
            String nombreTitular, Colaboracion colaboracion) {
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.horaPago = horaPago;
        this.tipoPago = tipoPago;
        this.nombreTitular = nombreTitular;
        this.colaboracion = colaboracion;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public LocalTime getHoraPago() {
        return horaPago;
    }

    public void setHoraPago(LocalTime horaPago) {
        this.horaPago = horaPago;
    }

    public TipoPago getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(TipoPago tipoPago) {
        this.tipoPago = tipoPago;
    }

    public TipoTarjeta getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(TipoTarjeta tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public Colaboracion getColaboracion() {
        return colaboracion;
    }

    public void setColaboracion(Colaboracion colaboracion) {
        this.colaboracion = colaboracion;
    }
}
