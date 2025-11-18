package com.culturarte.web.controller;

import com.culturarte.logica.datatypes.DTUsuario;
import com.culturarte.soap.gen.*;
import com.culturarte.web.soap.client.UsuarioSoapClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class MenuController {

    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    private UsuarioSoapClient soapClient;
    @Autowired
    private HttpSession httpSession;

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        logger.info("=== INICIO index (página principal) ===");
        try {
            DTUsuario usuarioLogueado =(DTUsuario) session.getAttribute("usuarioLogueado");

            if (usuarioLogueado == null) {
                logger.debug("No hay usuario en sesión, creando usuario visitante");
                usuarioLogueado = new DTUsuario();
                usuarioLogueado.setNickname("visitante");
                usuarioLogueado.setTipo("visitante");
                session.setAttribute("usuarioLogueado", usuarioLogueado);
            }

            logger.info("Página principal cargada para usuario: {}", usuarioLogueado.getNickname());
            logger.debug("=== FIN index (exitoso) ===");
            return "index";
        } catch (Exception e) {
            logger.error("=== ERROR en index ===", e);
            return "index";
        }
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        logger.info("=== INICIO login (GET) ===");
        try {
            String userAgent = request.getHeader("User-Agent");
            boolean esMovil = userAgent != null && userAgent.toLowerCase().matches(".*(mobi|android|iphone|ipad).*");
            model.addAttribute("esMovil", esMovil);
            logger.debug("Mostrando formulario de login. Es móvil: {}", esMovil);
            logger.debug("=== FIN login (GET) ===");
            return "login";
        } catch (Exception e) {
            logger.error("=== ERROR en login (GET) ===", e);
            return "login";
        }
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String nickOemail,
                                @RequestParam String password,
                                HttpSession session,
                                HttpServletRequest request,
                                Model model) {
        logger.info("=== INICIO procesarLogin ===");
        logger.info("Intento de login para: {}", nickOemail);
        try {
            VerificarPasswordResponse verificacion = soapClient.verificarPassword(nickOemail, password);
            if (!verificacion.isExito()) {
                logger.warn("Login fallido para: {} - {}", nickOemail, verificacion.getMensaje());
                model.addAttribute("mensaje", "⚠️ Contraseña o usuario incorrecto");
                model.addAttribute("nickname", nickOemail);
                return "login";
            }

            logger.debug("Password verificado correctamente, obteniendo datos del usuario");
            UsuarioType usuarioResp = soapClient.getUsuario(nickOemail);
            if (usuarioResp == null) {
                logger.error("No se pudo obtener usuario después de verificar password: {}", nickOemail);
                model.addAttribute("mensaje", "⚠️ Error al obtener datos del usuario");
                model.addAttribute("nickname", nickOemail);
                return "login";
            }

            DTUsuario usuario = UsuarioController.convertirDT(usuarioResp);

            session.setAttribute("usuarioLogueado", usuario);
            logger.info("Login exitoso para: {} (tipo: {})",
                    usuario.getNickname(), usuario.getTipo());
            logger.debug("=== FIN procesarLogin (exitoso) ===");
            return "redirect:/";
        } catch (Exception e) {
            logger.error("=== ERROR en procesarLogin ===", e);
            model.addAttribute("mensaje", "⚠️ Error al procesar el login");
            model.addAttribute("nickname", nickOemail);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        logger.info("=== INICIO logout ===");
        try {
            DTUsuario usuarioObj = (DTUsuario) session.getAttribute("usuarioLogueado");
            session.invalidate();
            logger.info("Logout exitoso para: {}", usuarioObj.getNickname());
            logger.debug("=== FIN logout ===");
            return "redirect:/";
        } catch (Exception e) {
            logger.error("=== ERROR en logout ===", e);
            return "redirect:/";
        }
    }

    @GetMapping("/exitoAltaPropuesta")
    public String exitoAltaPropuesta(Model model) {
        logger.info("Mostrando página de éxito de alta de propuesta");
        return "exitoAltaPropuesta";
    }
}
