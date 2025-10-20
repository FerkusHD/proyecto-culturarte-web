/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package com.culturarte.exepciones;

/**
 *
 * @author maicol
 */
public class UsuarioYaSeguido extends Exception {

    /**
     * Creates a new instance of <code>UsuarioYaSeguido</code> without detail
     * message.
     */
    public UsuarioYaSeguido() {
    }

    /**
     * Constructs an instance of <code>UsuarioYaSeguido</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UsuarioYaSeguido(String msg) {
        super(msg);
    }
}
