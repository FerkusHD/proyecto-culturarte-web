/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package com.culturarte.exepciones;

/**
 *
 * @author maicol
 */
public class CategoriaYaExiste extends Exception {

    /**
     * Creates a new instance of <code>CategoriaYaExiste</code> without detail
     * message.
     */
    public CategoriaYaExiste() {
    }

    /**
     * Constructs an instance of <code>CategoriaYaExiste</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CategoriaYaExiste(String msg) {
        super(msg);
    }
}
