package com.culturarte.web.controller;

import com.culturarte.exepciones.UsuarioYaExiste;
import com.culturarte.logica.IControlador;
import com.culturarte.logica.datatypes.DTPropuesta;
import com.culturarte.logica.datatypes.DTUsuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            @RequestParam String email,
            @RequestParam String fecha,
            @RequestParam String rol,
            @RequestParam(required = false) String imagen,
            @RequestParam(required=false) String direccion,
            @RequestParam(required=false) String biografia,
            @RequestParam(required=false) String web,
            Model model
    ) {
        LocalDate fechaNac = LocalDate.parse(fecha);

        try {
            if (rol.equals("proponente")) {
                ctrl.altaProponente(nickname,password, nombre, apellido, email, fechaNac, imagen, direccion, web, biografia);
                model.addAttribute("mensaje", "Proponente registrado con éxito");
                return "exitoAltaUsuario";
            } else if (rol.equals("colaborador")) {
                ctrl.altaColaborador(nickname,password, nombre, apellido, email, fechaNac, imagen);
                model.addAttribute("mensaje", "Colaborador registrado con éxito");
                return "exitoAltaUsuario";
            } else {
                model.addAttribute("mensaje", "⚠️ Debe seleccionar un rol");
            }

        } catch (UsuarioYaExiste e) {
            model.addAttribute("mensaje", "⚠️ " + "Ese nickname ya está registrado");
        }

        // 🔑 Guardamos los datos para que vuelvan al JSP
        model.addAttribute("nickname", nickname);
        model.addAttribute("nombre", nombre);
        model.addAttribute("password", password);
        model.addAttribute("apellido", apellido);
        model.addAttribute("email", email);
        model.addAttribute("fecha", fecha);
        model.addAttribute("rol", rol);
        model.addAttribute("direccion", direccion);
        model.addAttribute("biografia", biografia);
        model.addAttribute("web", web);

        return "altaUsuario";
    }

    @GetMapping("/{nick}")
    public String mostrarPerfil(@PathVariable String nick, HttpSession session, Model model) {
        DTUsuario usuarioLogueado = (DTUsuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            usuarioLogueado = new DTUsuario();
            usuarioLogueado.setTipo("visitante");
            usuarioLogueado.setNickname("visitante");
            session.setAttribute("usuarioLogueado", usuarioLogueado);
        }
        model.addAttribute("usuarioLogueado", usuarioLogueado);

        DTUsuario perfilVisitado = ctrl.getDTUsuario(nick);
        if (perfilVisitado == null) {
            return "error/404";
        }
        model.addAttribute("perfilVisitado", perfilVisitado);

        // Si es mi perfil
        model.addAttribute("esMiPropioPerfil", perfilVisitado.getNickname().equals(usuarioLogueado.getNickname()));

        // Si es prop
        if (perfilVisitado.getTipo().equals("proponente")) {
            model.addAttribute("proponente", ctrl.getDTProponente(nick));
        }

        // Si es colab
        if (perfilVisitado.getTipo().equals("colaborador")) {
            model.addAttribute("colaborador", ctrl.getDTColaborador(nick));
        }

        // Si lo sigo
        boolean loSigo = false;
        if (!usuarioLogueado.getTipo().equals("visitante")) {
            for (DTUsuario u : usuarioLogueado.getUsuariosSeguidos()) {
                if( u.getNickname().equals(nick)) {
                    loSigo = true;
                }
            }
        } else {
            loSigo = true;
        }
        model.addAttribute("loSigo", loSigo);

        return "perfil";
    }


    @GetMapping("/buscar")
    public String buscarUsuarios(@RequestParam(required = false) String nombre, Model model) {
        List<DTUsuario> resultados = ctrl.buscarUsuarios(nombre);
        model.addAttribute("resultados", resultados);
        model.addAttribute("nombre", nombre);
        return "busquedaUsuario";
    }

    @PostMapping("/seguir")
    public String seguir(@RequestParam String nickSeguido, HttpSession session, Model model, HttpServletRequest request) {
        DTUsuario usuarioLogueado = (DTUsuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || usuarioLogueado.getTipo().equals("visitante")) {
            return "redirect:/login";
        }
        try {
            ctrl.seguirUsuario(usuarioLogueado.getNickname(), nickSeguido);
        } catch (Exception e) {
            // TODO : Alerta si ya lo está siguiendo
        }
        usuarioLogueado = ctrl.getDTUsuario(usuarioLogueado.getNickname());
        session.setAttribute("usuarioLogueado", usuarioLogueado);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @PostMapping("/dejarDeSeguir")
    public String dejarDeSeguir(@RequestParam String nickSeguido, HttpSession session, Model model, HttpServletRequest request) {
        DTUsuario usuarioLogueado = (DTUsuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || usuarioLogueado.getTipo().equals("visitante")) {
            return "redirect:/login";
        }
        try {
            ctrl.dejarDeSeguirUsuario(usuarioLogueado.getNickname(), nickSeguido);
        } catch (Exception e) {
            // TODO : Alerta no lo sigue
        }

        usuarioLogueado = ctrl.getDTUsuario(usuarioLogueado.getNickname());
        session.setAttribute("usuarioLogueado", usuarioLogueado);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
}
