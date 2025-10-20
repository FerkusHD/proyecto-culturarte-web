/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package com.culturarte.exepciones;

/**
 *
 * @author maicol
 */
public class CargaFallida extends Exception {

    /**
     * Creates a new instance of <code>CargaFallida</code> without detail
     * message.
     */
    public CargaFallida() {
    }

    /**
     * Constructs an instance of <code>CargaFallida</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public CargaFallida(String msg) {
        super(msg);
    }
}
