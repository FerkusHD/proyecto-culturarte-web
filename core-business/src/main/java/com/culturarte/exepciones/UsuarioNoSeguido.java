/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package com.culturarte.exepciones;

/**
 *
 * @author maicol
 */
public class UsuarioNoSeguido extends Exception {

    /**
     * Creates a new instance of <code>UsuarioNoSeguido</code> without detail
     * message.
     */
    public UsuarioNoSeguido() {
    }

    /**
     * Constructs an instance of <code>UsuarioNoSeguido</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UsuarioNoSeguido(String msg) {
        super(msg);
    }
}
