package com.culturarte.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * DTO para serializar PropuestaType a JSON correctamente.
 * Resuelve problemas de serialización de objetos JAXB.
 */
public class PropuestaDTO {

    @JsonProperty("titulo")
    private String titulo;

    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("proponente")
    private String proponente;

    @JsonProperty("categoria")
    private String categoria;

    @JsonProperty("estado")
    private String estado;

    @JsonProperty("estadoActual")
    private String estadoActual;

    @JsonProperty("imagenBase64")
    private String imagenBase64;

    @JsonProperty("imagen")
    private String imagen;

    @JsonProperty("fechaPrevista")
    private String fechaPrevista;

    @JsonProperty("montoEntrada")
    private Float montoEntrada;

    @JsonProperty("montoNecesario")
    private Float montoNecesario;

    @JsonProperty("montoRecaudado")
    private Float montoRecaudado;

    @JsonProperty("cantColaboradores")
    private Integer cantColaboradores;

    @JsonProperty("colaboradores")
    private java.util.List<String> colaboradores;

    public PropuestaDTO() {
    }

    public PropuestaDTO(com.culturarte.soap.gen.PropuestaType propuesta) {
        if (propuesta != null) {
            this.titulo = propuesta.getTitulo();
            this.descripcion = propuesta.getDescripcion();
            this.proponente = propuesta.getProponente();
            this.categoria = propuesta.getCategoria();
            this.estado = propuesta.getEstadoActual();
            this.estadoActual = propuesta.getEstadoActual();

            // La imagen viene como ruta, no como base64
            String img = propuesta.getImagen();
            if (img != null && !img.trim().isEmpty()) {
                // Si parece ser base64 (muy largo o empieza con data:), usar imagenBase64
                if (img.length() > 500 || img.startsWith("data:")) {
                    this.imagenBase64 = img;
                    this.imagen = null;
                } else {
                    // Si es una ruta normal, usar imagen
                    this.imagen = img;
                    this.imagenBase64 = null;
                }
            } else {
                this.imagen = null;
                this.imagenBase64 = null;
            }

            this.montoEntrada = propuesta.getMontoEntrada();
            this.montoNecesario = propuesta.getMontoNecesario();
            this.montoRecaudado = propuesta.getMontoRecaudado();
            this.cantColaboradores = propuesta.getCantColaboradores();
            this.colaboradores = propuesta.getColaboradores();

            // Convertir fecha XMLGregorianCalendar a string ISO
            if (propuesta.getFechaPrevista() != null) {
                try {
                    XMLGregorianCalendar cal = propuesta.getFechaPrevista();
                    this.fechaPrevista = String.format("%04d-%02d-%02d",
                            cal.getYear(), cal.getMonth(), cal.getDay());
                } catch (Exception e) {
                    this.fechaPrevista = null;
                }
            }
        }
    }

    // Getters y setters
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getProponente() {
        return proponente;
    }

    public void setProponente(String proponente) {
        this.proponente = proponente;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    public String getImagenBase64() {
        return imagenBase64;
    }

    public void setImagenBase64(String imagenBase64) {
        this.imagenBase64 = imagenBase64;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getFechaPrevista() {
        return fechaPrevista;
    }

    public void setFechaPrevista(String fechaPrevista) {
        this.fechaPrevista = fechaPrevista;
    }

    public Float getMontoEntrada() {
        return montoEntrada;
    }

    public void setMontoEntrada(Float montoEntrada) {
        this.montoEntrada = montoEntrada;
    }

    public Float getMontoNecesario() {
        return montoNecesario;
    }

    public void setMontoNecesario(Float montoNecesario) {
        this.montoNecesario = montoNecesario;
    }

    public Float getMontoRecaudado() {
        return montoRecaudado;
    }

    public void setMontoRecaudado(Float montoRecaudado) {
        this.montoRecaudado = montoRecaudado;
    }

    public Integer getCantColaboradores() {
        return cantColaboradores;
    }

    public void setCantColaboradores(Integer cantColaboradores) {
        this.cantColaboradores = cantColaboradores;
    }

    public java.util.List<String> getColaboradores() {
        return colaboradores;
    }

    public void setColaboradores(java.util.List<String> colaboradores) {
        this.colaboradores = colaboradores;
    }
}
