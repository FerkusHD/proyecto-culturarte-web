package com.culturarte.web.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.culturarte.logica.IControlador;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/categorias")
public class CategoriasController {

    @Autowired
    private IControlador ctrl;

    @ResponseBody
    @GetMapping("/lista")
    List<String> categorias(){
        return ctrl.listarCategoriasWeb();
    }
}
