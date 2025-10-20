
package com.culturarte.exepciones;

public class UsuarioYaExiste extends Exception {

    public UsuarioYaExiste() {
    }

    public UsuarioYaExiste(String msg) {
        super(msg);
    }
}
