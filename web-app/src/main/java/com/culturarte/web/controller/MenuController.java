package com.culturarte.web.controller;

import com.culturarte.soap.gen.*;
import com.culturarte.web.soap.client.UsuarioSoapClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class MenuController {

    @Autowired
    private UsuarioSoapClient soapClient;

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        GetUsuarioResponse usuarioResp = (GetUsuarioResponse) session.getAttribute("usuarioLogueado");

        if (usuarioResp == null || usuarioResp.getUsuario() == null) {
            UsuarioType visitante = new UsuarioType();
            visitante.setNickname("visitante");
            visitante.setTipo("visitante");
            session.setAttribute("usuarioLogueado", visitante);
            model.addAttribute("usuario", visitante);
        } else {
            model.addAttribute("usuario", usuarioResp.getUsuario());
        }

        return "index";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        String userAgent = request.getHeader("User-Agent");
        boolean esMovil = userAgent != null && userAgent.toLowerCase().matches(".*(mobi|android|iphone|ipad).*");
        model.addAttribute("esMovil", esMovil);
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String nickOemail,
                                @RequestParam String password,
                                HttpSession session,
                                HttpServletRequest request,
                                Model model) {

        VerificarPasswordResponse verificacion = soapClient.verificarPassword(nickOemail, password);
        if (!verificacion.isValido()) {
            model.addAttribute("mensaje", "⚠️ Contraseña o usuario incorrecto");
            model.addAttribute("nickname", nickOemail);
            return "login";
        }

        GetUsuarioResponse usuarioResp = soapClient.getUsuario(nickOemail);
        UsuarioType usuario = usuarioResp.getUsuario();

        String userAgent = request.getHeader("User-Agent");
        boolean esMovil = userAgent != null && userAgent.toLowerCase().matches(".*(mobi|android|iphone|ipad).*");

        if (esMovil && !"colaborador".equalsIgnoreCase(usuario.getTipo())) {
            model.addAttribute("mensaje", "⚠️ Solo los colaboradores pueden iniciar sesión desde un dispositivo móvil.");
            model.addAttribute("nickname", nickOemail);
            return "login";
        }

        session.setAttribute("usuarioLogueado", usuario);
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
