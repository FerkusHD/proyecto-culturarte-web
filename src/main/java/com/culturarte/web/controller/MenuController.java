package com.culturarte.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/inicioSesion")
    public String inicioSesion() {
        return "inicioSesion";
    }

    @GetMapping("/altaUsuario")
    public String altaUsuario() {
        return "altaUsuario";
    }

}

