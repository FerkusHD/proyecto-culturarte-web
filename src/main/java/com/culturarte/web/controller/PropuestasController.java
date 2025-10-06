package com.culturarte.web.controller;

import com.culturarte.logica.IControlador;
import com.culturarte.logica.datatypes.DTPropuesta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/propuestas")
public class PropuestasController {

    @Autowired
    private IControlador ctrl;

    @ResponseBody
    @GetMapping("/listar")
    List<DTPropuesta> propuestas() {
        return ctrl.getDTPropuestasWeb();
    }

    @ResponseBody
    @GetMapping("/listar/{titulo}")
    DTPropuesta propuestas(@PathVariable String titulo) {
        return ctrl.getDTPropuesta(titulo);
    }
}
