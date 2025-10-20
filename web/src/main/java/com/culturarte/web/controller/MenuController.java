package com.culturarte.web.controller;
import com.culturarte.logica.datatypes.DTUsuario;
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
        System.out.println("✅ MenuController inicializado correctamente");
    }

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        System.out.println("🔍 MenuController.index() llamado");

        DTUsuario u = (DTUsuario) session.getAttribute("usuarioLogueado");
        if (u == null) {
            u = new DTUsuario();
            u.setNickname("visitante");
            u.setTipo("visitante");
            session.setAttribute("usuarioLogueado", u);
        }

        model.addAttribute("usuario", u);

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
        
        boolean existeUsuario = ctrl.verificarUsuario(nickOemail, password);

        if (!existeUsuario) {
            model.addAttribute("mensaje", "⚠️ Contraseña o nickname/email incorrecto");
            model.addAttribute("nickname", nickOemail);
            return "login";
        }
        if(!nickOemail.contains("@") && !nickOemail.contains(".com")){
        DTUsuario usuario = ctrl.getDTUsuario(nickOemail);
        session.setAttribute("usuarioLogueado", usuario);
        return "index";
        }
        else{
            DTUsuario usuario = ctrl.getDTUsuarioEmail(nickOemail);
            session.setAttribute("usuarioLogueado", usuario);
                return "index"; 
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}