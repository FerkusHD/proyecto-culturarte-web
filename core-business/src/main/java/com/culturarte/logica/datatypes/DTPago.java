package com.culturarte.logica.datatypes;

import com.culturarte.logica.enums.TipoPago;
import com.culturarte.logica.enums.TipoTarjeta;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DataType para transferir información de pagos
 */
public class DTPago {

    private float monto;
    private LocalDate fechaPago;
    private LocalTime horaPago;
    private TipoPago tipoPago;

    // Campos para pago con tarjeta
    private TipoTarjeta tipoTarjeta;
    private String numeroTarjeta;
    private String fechaVencimiento;
    private String cvc;

    // Campos comunes
    private String nombreTitular;

    // Campos para transferencia bancaria
    private String nombreBanco;
    private String numeroCuenta;

    public DTPago() {
    }

    public DTPago(float monto, LocalDate fechaPago, LocalTime horaPago, TipoPago tipoPago,
            String nombreTitular) {
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.horaPago = horaPago;
        this.tipoPago = tipoPago;
        this.nombreTitular = nombreTitular;
    }

    // Getters y Setters
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
}
