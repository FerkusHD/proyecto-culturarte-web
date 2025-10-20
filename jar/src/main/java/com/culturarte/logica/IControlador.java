/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.culturarte.logica;

import com.culturarte.exepciones.*;
import com.culturarte.logica.datatypes.DTProponente;
import java.util.ArrayList;

import com.culturarte.logica.datatypes.DTColaborador;
import com.culturarte.logica.datatypes.DTPropuesta;
import com.culturarte.logica.datatypes.DTColaboracion;
import com.culturarte.logica.datatypes.DTUsuario;
import com.culturarte.logica.enums.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author maicol
 */
public interface IControlador {
    
    public abstract void altaColaborador(String nickname, String password, String nombre, String apellido, String email, LocalDate fechaNacimiento, String imagen) throws UsuarioYaExiste;
    public abstract void altaProponente(String nickname, String password, String nombre, String apellido, String email, LocalDate fechaNacimiento, String imagen, String direccion, String linkWeb, String bibliografia) throws UsuarioYaExiste;
    public abstract ArrayList<String> getNomProponentes();
    public abstract DTProponente getDTProponente(String nickname);
    public abstract void altaCategoria(String nombre, String catPadre) throws CategoriaYaExiste;
    public abstract DefaultTreeModel listarCategorias();
    public abstract void altaPropuesta(String titulo, String descripcion, String lugar, LocalDate fechaPrevista, Float precioEntrada, Float montoNecesario, EnumSet<TipoRetorno> tipoRetornos, String imagen, String proponente, String categoria, LocalDate fechaActual, LocalTime horaActual) throws PropuestaYaExiste;
    public abstract ArrayList<String> getNickColaboradores();
    public abstract ArrayList<String> getNomColaboradores();
    public abstract DTColaborador getDTColaborador(String nickname);
    public abstract ArrayList<DTPropuesta> getDTPropuestas();
    public abstract ArrayList<String> getTituloPropuestas(); // cu6 y cu7
    public abstract DTPropuesta getDTPropuesta(String titulo); // cu6 y cu7
    public abstract ArrayList<String> getTituloPropuestasPorEstado(TipoEstado estado); // cu6 y cu7
    public abstract void altaColaboracion( float monto, LocalDate fecha, LocalTime hora, TipoRetorno tipoRetorno, String tituloPropuesta, String nickColaborador) throws ColaboracionYaExiste; // cu9
    public abstract String getNickProponente(String tituloPropuesta); //cu9
    public abstract ArrayList<String> getNickUsuarios(); // cu12 y cu13
    public abstract void seguirUsuario(String nickSeguidor, String nickSeguido) throws UsuarioYaSeguido; // cu12
    public abstract void dejarDeSeguirUsuario(String nickSeguidor, String nickSeguido) throws UsuarioNoSeguido; //cu13
    public abstract ArrayList<DTColaboracion> getDTColaboracionesPropuestas(String nickColab);//cu10
    public abstract DTColaboracion getDTColaboracionPropuesta(String nickColab, String tituloProp);//cu10
    public abstract void cancelarColaboracionPropuesta(String tituloPropuesta, String nickColaborador);//cu11
    public abstract ArrayList<DTColaboracion> getDTColaboraciones();//cu11
    public abstract DTUsuario getDTUsuario(String nickname);
    public abstract void nuevoEstadoPropuesta(String propuesta, TipoEstado estado, LocalDate fecha, LocalTime hora);
    public abstract void modificarPropuesta(String titulo, String descripcion, String lugar, LocalDate fechaPrevista, Float precioEntrada, Float montoNecesario, String imagen, String proponente, String categoria, String nuevoEstado) throws DatosIncorrectos;
    public abstract ArrayList<DTPropuesta> getDTPropuestasWeb();
    public abstract boolean verificarPassword(String password, String nick);
    public abstract List<String> listarCategoriasWeb();
    public abstract List<String> listarCategoriasWebCompletas();
    public abstract List<DTPropuesta> buscarPropuestas(String texto);
    public abstract String[] getTiposRetorno();
    public abstract void extenderFinanciacion(String tituloPropuesta, LocalDate nuevaFecha);
    public abstract void agregarComentario(String texto, String nickColaborador, String tituloPropuesta);
    public abstract List<DTUsuario> buscarUsuarios(String nombre);
    public abstract void agregarPropuestaFavorita(String nickname, String tituloPropuesta);
    public abstract void sacarPropuestaFavorita(String nickname, String tituloPropuesta);
    public abstract boolean colaboradorPuedeComentar(String colaborador, String tituloPropuesta);
}
