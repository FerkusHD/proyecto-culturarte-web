package com.culturarte.web.controller;

import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.culturarte.exepciones.UsuarioYaExiste;
import com.culturarte.logica.IControlador;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MenuController {

    private final IControlador ctrl;

    public MenuController(IControlador ctrl) {
        this.ctrl = ctrl;
    }

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

    @PostMapping("/altaUsuario")
    public String altaUsuario(
            @RequestParam String nickname,
            @RequestParam String nombre,
            @RequestParam String password,
            @RequestParam String apellido,
            @RequestParam String confirmar,
            @RequestParam String email,
            @RequestParam String fecha,
            @RequestParam(required=false, name="rol[]") String[] roles,
            @RequestParam(required=false) String direccion,
            @RequestParam(required=false) String biografia,
            @RequestParam(required=false) String web,
            Model model
    ) {
        try {
            LocalDate fechaNac = LocalDate.parse(fecha);

            boolean esProponente = roles != null && Arrays.asList(roles).contains("proponente");
            boolean esColaborador = roles != null && Arrays.asList(roles).contains("colaborador");

            if (esProponente) {
                ctrl.altaProponente(nickname,password, nombre, apellido, email, fechaNac, null, direccion, web, biografia);
                model.addAttribute("mensaje", "✅ Proponente registrado con éxito");
                
            } else if (esColaborador) {
                ctrl.altaColaborador(nickname,password, nombre, apellido, email, fechaNac, null);
                model.addAttribute("mensaje", "✅ Colaborador registrado con éxito");
            } else {
                model.addAttribute("mensaje", "⚠️ Debe seleccionar un rol");
            }

        } catch (UsuarioYaExiste e) {
            model.addAttribute("mensaje", "⚠️ " + e.getMessage());
        }

        return "altaUsuario";
    }
}