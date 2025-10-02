package com.culturarte.web.controller;

import com.culturarte.logica.IControlador;
import com.culturarte.logica.datatypes.DTPropuesta;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apiPropuestas")
public class PropuestasController {

    private final IControlador ctrl;

    public PropuestasController(IControlador ctrl) {
        this.ctrl = ctrl;
    }

    @GetMapping("/listar")
    List<DTPropuesta> propuestas() {
        return ctrl.getDTPropuestasWeb();
    }

    @GetMapping("/listar/{titulo}")
    DTPropuesta propuestas(@PathVariable String titulo) {
        return ctrl.getDTPropuesta(titulo);
    }
}
