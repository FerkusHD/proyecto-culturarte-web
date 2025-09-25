/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package com.culturarte.exepciones;

/**
 *
 * @author maicol
 */
public class DatosIncorrectos extends Exception {

    /**
     * Creates a new instance of <code>DatosIncorrectos</code> without detail
     * message.
     */
    public DatosIncorrectos() {
    }

    /**
     * Constructs an instance of <code>DatosIncorrectos</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DatosIncorrectos(String msg) {
        super(msg);
    }
}
