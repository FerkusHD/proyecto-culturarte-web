package com.culturarte.web.controller;

import com.culturarte.logica.IControlador;
import com.culturarte.logica.datatypes.DTPropuesta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping("/registrarColaboracionProp")
    public String registrarColaboracionProp() {
        return "registrarColaboracionProp";
    }

     @GetMapping("/registroColaboracion")
    public String registroCol() {
        return "registroColaboracion";
    }

    @GetMapping("/buscar")
    public String buscar(
            @RequestParam(value = "query", required = false, defaultValue = "") String query,
            @RequestParam(value = "estado", required = false) String estado,
            @RequestParam(value = "orden", required = false) String orden,
            Model model) {

        List<DTPropuesta> resultados = ctrl.buscarPropuestas(query);
        if (resultados == null) resultados = List.of();

        // 🔹 Aplicar filtros secundarios
        if (estado != null && !estado.isBlank()) {
            resultados = resultados.stream()
                    .filter(p -> p.getEstadoActual().toString().equalsIgnoreCase(estado))
                    .toList();
        }

        if ("tituloAsc".equals(orden)) {
            resultados = resultados.stream()
                    .sorted((p1, p2) -> p1.getTitulo().compareToIgnoreCase(p2.getTitulo()))
                    .toList();
        } else if ("fechaDesc".equals(orden)) {
            resultados = resultados.stream()
                    .sorted((p1, p2) -> p2.getFechaPrevista().compareTo(p1.getFechaPrevista()))
                    .toList();
        }

        model.addAttribute("resultados", resultados);
        model.addAttribute("query", query);
        model.addAttribute("estado", estado);
        model.addAttribute("orden", orden);

        return "busquedaPropuestas";
    }


}