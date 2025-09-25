/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Exception.java to edit this template
 */
package com.culturarte.exepciones;

/**
 *
 * @author pablo
 */
public class ColaboracionYaExiste extends Exception {

    /**
     * Creates a new instance of <code>ColaboracionYaExiste</code> without
     * detail message.
     */
    public ColaboracionYaExiste() {
    }

    /**
     * Constructs an instance of <code>ColaboracionYaExiste</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ColaboracionYaExiste(String msg) {
        super(msg);
    }
}
