/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.culturarte.exepciones;

/**
 *
 * @author fabriciorivero
 */
public class PropuestaYaExiste extends Exception{

    public PropuestaYaExiste() {
    }

    public PropuestaYaExiste(String msg) {
        super(msg);
    }
}
