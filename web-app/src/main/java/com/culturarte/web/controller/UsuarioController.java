package com.culturarte.web.controller;
import com.culturarte.exepciones.EmailYaExiste;
import com.culturarte.exepciones.UsuarioYaExiste;
import com.culturarte.logica.IControlador;
import com.culturarte.logica.datatypes.DTUsuario;
import com.culturarte.web.soap.VerificacionSoapService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private IControlador ctrl;

    @Autowired
    private VerificacionSoapService verificacionSoapService;

    @GetMapping("/alta")
    public String altaUsuario() {
        return "altaUsuario";
    }

    @GetMapping("/ranking")
    public String rankingUsu(Model model){
        try {
            ArrayList<DTUsuario> usuarios = ctrl.listarUsuarios();
            model.addAttribute("usuarios", usuarios);
            return "rankingUsuarios";
        } catch (UnsupportedOperationException e) {
            // Cuando se usa SOAP, listarUsuarios no está disponible
            model.addAttribute("usuarios", new ArrayList<DTUsuario>());
            model.addAttribute("mensaje", "⚠️ El ranking de usuarios no está disponible cuando se usa el servicio SOAP");
            return "rankingUsuarios";
        }
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
            @RequestParam(required = false) MultipartFile imagenFile,
            @RequestParam(required=false) String direccion,
            @RequestParam(required=false) String biografia,
            @RequestParam(required=false) String web,
            Model model,
            HttpSession session
    ) {
        LocalDate fechaNac = LocalDate.parse(fecha);
        String imagen = null;

        try {
            //Si subió una imagen, la guardamos físicamente
            //Obtener la carpeta absoluta del proyecto
            Path directorio = Paths.get(System.getProperty("user.dir"), "uploads", "imagenes");

            if (!Files.exists(directorio)) {
                Files.createDirectories(directorio);
            }
            String nombreArchivo = nickname + "_" + System.currentTimeMillis() + "_" + imagenFile.getOriginalFilename();

            Path rutaCompleta = directorio.resolve(nombreArchivo);
            Files.copy(imagenFile.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

            imagen = "uploads/imagenes/" + nombreArchivo;

            System.out.println("Imagen guardada en: " + rutaCompleta.toAbsolutePath());

            if (rol.equals("proponente")) {
                ctrl.altaProponente(nickname,password, nombre, apellido, email, fechaNac, imagen, direccion, web, biografia);
                DTUsuario usuario = ctrl.getDTUsuario(nickname);
                session.setAttribute("usuarioLogueado", usuario);
                return "redirect:/";
            } else if (rol.equals("colaborador")) {
                ctrl.altaColaborador(nickname,password, nombre, apellido, email, fechaNac, imagen);
                DTUsuario usuario = ctrl.getDTUsuario(nickname);
                session.setAttribute("usuarioLogueado", usuario);
                return "redirect:/";
            } else {
                model.addAttribute("mensaje", "⚠️ Debe seleccionar un rol");
            }

        } catch (UsuarioYaExiste e) {
            model.addAttribute("mensaje", "⚠️ " + "Ese nickname ya está registrado");
        }
        catch(EmailYaExiste e){
            model.addAttribute("mensaje", "⚠️ " + "Ese email ya está registrado");}
         catch (IOException e) {
            model.addAttribute("mensaje", "⚠️ Error al procesar la imagen");
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

    /**
     * Endpoint REST para verificar disponibilidad de nickname mediante AJAX.
     * Internamente usa SOAP para realizar la verificación.
     * 
     * @param nickname Nickname a verificar
     * @return JSON con { "disponible": true/false, "mensaje": "..." }
     */
    @GetMapping("/verificar-nickname")
    @ResponseBody
    public VerificacionResponse verificarNickname(@RequestParam("nickname") String nickname) {
        return verificarNicknameSoap(nickname);
    }

    /**
     * Endpoint REST para verificar disponibilidad de email mediante AJAX.
     * Internamente usa SOAP para realizar la verificación.
     * 
     * @param email Email a verificar
     * @return JSON con { "disponible": true/false, "mensaje": "..." }
     */
    @GetMapping("/verificar-email")
    @ResponseBody
    public VerificacionResponse verificarEmail(@RequestParam("email") String email) {
        return verificarEmailSoap(email);
    }

    /**
     * Verifica disponibilidad de nickname usando SOAP directamente.
     */
    private VerificacionResponse verificarNicknameSoap(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            return new VerificacionResponse(false, "El nickname no puede estar vacío");
        }

        try {
            // Llamar al servicio SOAP para verificar disponibilidad
            com.culturarte.soap.gen.VerificarNicknameResponse response = 
                verificacionSoapService.verificarNickname(nickname.trim());
            
            if (response != null) {
                return new VerificacionResponse(
                    response.isDisponible(), 
                    response.getMensaje()
                );
            } else {
                return new VerificacionResponse(false, "Error al verificar disponibilidad");
            }
        } catch (Exception e) {
            // Si falla SOAP, intentar con el controlador como fallback
            try {
                DTUsuario usuario = ctrl.getDTUsuario(nickname.trim());
                if (usuario != null) {
                    return new VerificacionResponse(false, "El nickname '" + nickname + "' ya está en uso");
                } else {
                    return new VerificacionResponse(true, "El nickname '" + nickname + "' está disponible");
                }
            } catch (Exception ex) {
                return new VerificacionResponse(false, "Error al verificar disponibilidad");
            }
        }
    }

    /**
     * Verifica disponibilidad de email usando SOAP directamente.
     */
    private VerificacionResponse verificarEmailSoap(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new VerificacionResponse(false, "El email no puede estar vacío");
        }

        // Validar formato básico de email
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return new VerificacionResponse(false, "El formato del email no es válido");
        }

        try {
            // Llamar al servicio SOAP para verificar disponibilidad
            com.culturarte.soap.gen.VerificarEmailResponse response = 
                verificacionSoapService.verificarEmail(email.trim());
            
            if (response != null) {
                return new VerificacionResponse(
                    response.isDisponible(), 
                    response.getMensaje()
                );
            } else {
                return new VerificacionResponse(false, "Error al verificar disponibilidad");
            }
        } catch (Exception e) {
            // Si falla SOAP, intentar con el controlador como fallback
            try {
                ArrayList<DTUsuario> usuarios = ctrl.listarUsuarios();
                if (usuarios != null) {
                    for (DTUsuario usuario : usuarios) {
                        if (usuario.getEmail() != null && usuario.getEmail().equalsIgnoreCase(email.trim())) {
                            return new VerificacionResponse(false, "El email '" + email + "' ya está en uso");
                        }
                    }
                }
                return new VerificacionResponse(true, "El email '" + email + "' está disponible");
            } catch (UnsupportedOperationException ex) {
                return new VerificacionResponse(true, "No se pudo verificar (verificación en servidor al registrar)");
            } catch (Exception ex) {
                return new VerificacionResponse(false, "Error al verificar disponibilidad");
            }
        }
    }

    /**
     * Clase interna para la respuesta de verificación.
     */
    public static class VerificacionResponse {
        private boolean disponible;
        private String mensaje;

        public VerificacionResponse(boolean disponible, String mensaje) {
            this.disponible = disponible;
            this.mensaje = mensaje;
        }

        public boolean isDisponible() {
            return disponible;
        }

        public void setDisponible(boolean disponible) {
            this.disponible = disponible;
        }

        public String getMensaje() {
            return mensaje;
        }

        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }
    }
}
