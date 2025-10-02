package com.culturarte.web.controller;

import com.culturarte.exepciones.UsuarioYaExiste;
import com.culturarte.logica.IControlador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private IControlador ctrl;

    @GetMapping("/alta")
    public String altaUsuario() {
        return "altaUsuario";
    }

    @PostMapping("/alta")
    public String altaUsuario(
            @RequestParam String nickname,
            @RequestParam String nombre,
            @RequestParam String password,
            @RequestParam String apellido,
            @RequestParam String confirmar,
            @RequestParam String email,
            @RequestParam String fecha,
            @RequestParam(required=false) String rol,
            @RequestParam(required=false) String direccion,
            @RequestParam(required=false) String biografia,
            @RequestParam(required=false) String web,
            Model model
    ) {
        LocalDate fechaNac = LocalDate.parse(fecha);

        try {
            if (rol == "proponente") {
                ctrl.altaProponente(nickname,password, nombre, apellido, email, fechaNac, null, direccion, web, biografia);
                model.addAttribute("mensaje", "✅ Proponente registrado con éxito");
                return "exitoAltaUsuario";
            } else if (rol == "colaborador") {
                ctrl.altaColaborador(nickname,password, nombre, apellido, email, fechaNac, null);
                model.addAttribute("mensaje", "✅ Colaborador registrado con éxito");
                return "exitoAltaUsuario";
            } else {
                model.addAttribute("mensaje", "⚠️ Debe seleccionar un rol");
            }

        } catch (UsuarioYaExiste e) {
            model.addAttribute("mensaje", "⚠️ " + "El usuario ya existe");
        }

        return "altaUsuario";
    }
}
