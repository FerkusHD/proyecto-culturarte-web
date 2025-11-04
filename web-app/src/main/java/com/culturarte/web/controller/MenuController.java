package com.culturarte.web.controller;
import com.culturarte.logica.datatypes.DTUsuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.culturarte.logica.IControlador;

@Controller
@RequestMapping("/")
public class MenuController {

    private final IControlador ctrl;

    public MenuController(IControlador ctrl) {
        this.ctrl = ctrl;
    }

    @GetMapping("/")
    public String index(HttpSession session, Model model, HttpServletRequest request) {

    DTUsuario u = (DTUsuario) session.getAttribute("usuarioLogueado");
    if (u == null) {
        u = new DTUsuario();
        u.setNickname("visitante");
        u.setTipo("visitante");
        session.setAttribute("usuarioLogueado", u);
    }

    String userAgent = request.getHeader("User-Agent");
    boolean esMovil = userAgent != null && userAgent.toLowerCase().matches(".*(mobi|android|iphone|ipad).*");

    model.addAttribute("usuario", u);
    model.addAttribute("esMovil", esMovil);

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

        boolean existeUsuario = ctrl.verificarPassword(password, nickOemail);

        if (!existeUsuario) {
            model.addAttribute("mensaje", "⚠️ Contraseña o nickname/email incorrecto");
            model.addAttribute("nickname", nickOemail);
            return "login";
        }
        DTUsuario usuario = ctrl.getDTUsuario(nickOemail);
        session.setAttribute("usuarioLogueado", usuario);
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}