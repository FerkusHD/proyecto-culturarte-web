package com.culturarte.exepciones;

public class EmailYaExiste extends Exception {

    public EmailYaExiste() {
    }

    public EmailYaExiste(String message) {
        super(message);
    }
}
