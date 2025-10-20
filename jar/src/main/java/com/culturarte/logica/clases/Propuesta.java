package com.culturarte.logica.clases;
import com.culturarte.logica.enums.TipoRetorno;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Entity
public class Propuesta {
    
    @Id
    private String titulo;
    
    @Column(length = 5000)
    private String descripcion;
    private String lugar;
    private LocalDate fechaPrevista;
    private float precioEntrada;
    private float montoNecesario;
    
    @ElementCollection(targetClass = TipoRetorno.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "propuesta_retornos", joinColumns = @JoinColumn(name = "propuesta_id"))
    @Column(name = "retorno")
    @Enumerated(EnumType.STRING)
    private Set<TipoRetorno> tipoRetornos = EnumSet.noneOf(TipoRetorno.class);

    private String imagen;
    
    @OneToMany(mappedBy = "propuesta")
    private List<Colaboracion> colaboraciones;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private Proponente proponente;
    
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Estado> historialEstados;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Estado estadoActual;
    
    @ManyToOne
    private Categoria categoria;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comentario> comentarios;

    public Propuesta() {
    }

    public Propuesta(String titulo, String descripcion, String lugar, LocalDate fechaPrevista, float precioEntrada, float montoNecesario, EnumSet<TipoRetorno> tipoRetornos, String imagen, Proponente proponente, Categoria categoria) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.fechaPrevista = fechaPrevista;
        this.precioEntrada = precioEntrada;
        this.montoNecesario = montoNecesario;
        this.tipoRetornos = tipoRetornos;
        this.imagen = imagen;
        this.proponente = proponente;
        this.categoria = categoria;
        this.colaboraciones = new ArrayList<>();
        this.historialEstados = new ArrayList<>();
        this.comentarios = new ArrayList<>();

    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    
    
    
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

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public LocalDate getFechaPrevista() {
        return fechaPrevista;
    }

    public void setFechaPrevista(LocalDate fechaPrevista) {
        this.fechaPrevista = fechaPrevista;
    }

    public float getPrecioEntrada() {
        return precioEntrada;
    }

    public void setPrecioEntrada(float precioEntrada) {
        this.precioEntrada = precioEntrada;
    }

    public float getMontoNecesario() {
        return montoNecesario;
    }

    public void setMontoNecesario(float montoNecesario) {
        this.montoNecesario = montoNecesario;
    }

    public Proponente getProponente() {
        return proponente;
    }

    public void setProponente(Proponente proponente) {
        this.proponente = proponente;
    }

    public Estado getEstadoActual() {
        return historialEstados.getLast();
    }

    public void setEstadoActual(Estado estadoActual) {
        this.estadoActual = estadoActual;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
    public String getImagen(){
        return imagen;
    }
    
    public List<Colaboracion> getColaboraciones(){
        return colaboraciones;
    }

    public Set<TipoRetorno> getTipoRetornos() {
        return tipoRetornos;
    }

    public void setTipoRetornos(Set<TipoRetorno> tipoRetornos) {
        this.tipoRetornos = tipoRetornos;
    }

    public List<Estado> getHistorialEstados() {
        return historialEstados;
    }
    
    public void addColaboracion(Colaboracion colab){
        this.colaboraciones.add(colab);
    }
    
    public ArrayList<String> getNicknameColaboradores(){
        ArrayList<String> listaNicks = new ArrayList<>();
        for(Colaboracion c : colaboraciones){
            listaNicks.add(c.getColaboradorNick());
        }
        return listaNicks;
    }
    
    public String getProponenteNick() {
        return proponente.getNickname();
    }
    
    public float getMontoRecaudado() {
        float montoRecuadado = 0;
        for(Colaboracion c : this.colaboraciones){
            montoRecuadado += c.getMonto();
        }
        return montoRecuadado;
    }
    
    public void agregarEstado(Estado estado) {
        this.historialEstados.add(estado);
        this.estadoActual = estado;
    }

    public void agregarComentario(Comentario comentario) {
        if (this.comentarios == null) {
            this.comentarios = new ArrayList<>();
        }
        this.comentarios.add(comentario);
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }
}
