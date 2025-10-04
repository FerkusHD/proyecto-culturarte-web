package com.culturarte.web.controller;

import java.time.LocalDate;
import java.util.Arrays;

import com.culturarte.logica.datatypes.DTUsuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.culturarte.exepciones.UsuarioYaExiste;
import com.culturarte.logica.IControlador;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/")
public class MenuController {

    private final IControlador ctrl;

    public MenuController(IControlador ctrl) {
        this.ctrl = ctrl;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String nickOemail,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {
        boolean existe = ctrl.verificarPassword(nickOemail, password);

        if (!existe) {
            model.addAttribute("mensaje", "⚠️ Contraseña o nickname/email incorrecto");
            model.addAttribute("nickname", nickOemail);
            return "login";
        }

        DTUsuario usuario = ctrl.getDTUsuario(nickOemail);
        session.setAttribute("usuarioLogueado", usuario);

        // Redirigimos a un único dashboard
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }



}