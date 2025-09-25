/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.culturarte.logica.datatypes;

import com.culturarte.logica.enums.TipoEstado;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DTPropuesta {
    private String titulo;
    private String descripcion;
    private String lugar;
    private LocalDate fechaPrevista;
    private float precioEntrada;
    private float montoNecesario;
    private float montoRecaudado; // Calculado
    private File imagen;
    private ArrayList<String> colaboradores = new ArrayList<>();
    private TipoEstado estadoActual;
    private List<DTEstado> histEstados = new ArrayList();
    private String categoria;
    private String nickProponente;
    
    public DTPropuesta(){}
    
    public DTPropuesta(String titulo, String descripcion, String lugar, LocalDate fechaPrevista, float precioEntrada, float montoNecesario, File imagen, ArrayList<String> colaboradores, String nickProponente, TipoEstado estadoActual, String categoria, ArrayList<DTEstado> histEstados, float montoRecaudado){
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.fechaPrevista = fechaPrevista;
        this.precioEntrada = precioEntrada;
        this.montoNecesario = montoNecesario;
        this.imagen = imagen;
        this.colaboradores = colaboradores;
        this.estadoActual = estadoActual;
        this.categoria = categoria;
        this.nickProponente = nickProponente;
        this.histEstados = histEstados;
        this.montoRecaudado = montoRecaudado;
    }
    
    public DTPropuesta(String titulo, TipoEstado estado, ArrayList<String> colaboradores, float montoRecaudado, float montoNecesario) {
        this.titulo = titulo;
        this.estadoActual = estado;
        this.colaboradores = colaboradores;
        this.montoRecaudado = montoRecaudado;
        this.montoNecesario = montoNecesario;
    }
    
    public DTPropuesta(String titulo, TipoEstado estado, String nickProponente, float montoRecaudado, float montoNecesario) {
        this.titulo = titulo;
        this.estadoActual = estado;
        this.nickProponente = nickProponente;
        this.montoRecaudado = montoRecaudado;
        this.montoNecesario = montoNecesario;
    }
    
    public DTPropuesta(String titulo, String descripcion, String lugar, LocalDate fechaPrevista, float precioEntrada, float montoNecesario){
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.fechaPrevista = fechaPrevista;
        this.precioEntrada = precioEntrada;
        this.montoNecesario = montoNecesario;
    }

    public List<DTEstado> getHistEstados() {
        return histEstados;
    }

    public String getNickProponente() {
        return nickProponente;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getLugar() {
        return lugar;
    }

    public LocalDate getFechaPrevista() {
        return fechaPrevista;
    }

    public float getPrecioEntrada() {
        return precioEntrada;
    }

    public float getMontoNecesario() {
        return montoNecesario;
    }
    
    public float getMontoRecaudado() {
        return montoRecaudado;
    }

    public File getImagen() {
        return imagen;
    }

    public ArrayList<String> getColaboradores() {
        return colaboradores;
    }

    public TipoEstado getEstadoActual() {
        return estadoActual;
    }

    public String getCategoria() {
        return categoria;
    }
    public String getProponente(){
     return nickProponente;
    }
    
    public void addColaborador(String nom) {
        this.colaboradores.add(nom);
    }

    public ArrayList<String> getNomColaboradores(){
    ArrayList<String> retorno = new ArrayList<>();
        for (String nomCol : this.colaboradores) {
            retorno.add(nomCol);
        }
        retorno.sort(String.CASE_INSENSITIVE_ORDER);
        return retorno;
    }

    
}
