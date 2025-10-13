package com.culturarte.web.controller;

import com.culturarte.exepciones.UsuarioYaExiste;
import com.culturarte.logica.IControlador;
import com.culturarte.logica.datatypes.DTPropuesta;
import com.culturarte.logica.datatypes.DTUsuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

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


        DTUsuario u = (DTUsuario) session.getAttribute("usuarioLogueado");
        if (u == null) {
            u = new DTUsuario();
            u.setNickname("visitante");
            u.setTipo("visitante");
            session.setAttribute("usuarioLogueado", u);
        }

        model.addAttribute("usuarioLogueado", u);

        DTUsuario perfilVisitado = ctrl.getDTUsuario(nick);

        if (perfilVisitado == null) {
            return "error/404";
        }

        boolean esMiPerfil = false;
        if (perfilVisitado.getNickname().equals(u.getNickname())) {
            esMiPerfil = true;
        }

        model.addAttribute("esMiPropioPerfil", esMiPerfil);
        model.addAttribute("perfilVisitado", perfilVisitado);

        return "perfil";
    }

    @GetMapping("/buscar")
    public String buscarUsuarios(@RequestParam(required = false) String nombre, Model model) {
        List<DTUsuario> resultados = ctrl.buscarUsuarios(nombre);
        model.addAttribute("resultados", resultados);
        model.addAttribute("nombre", nombre);
        return "busquedaUsuario";
    }

}
