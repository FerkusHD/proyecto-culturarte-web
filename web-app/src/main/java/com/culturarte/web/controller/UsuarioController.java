package com.culturarte.web.controller;

import com.culturarte.logica.datatypes.DTPropuesta;
import com.culturarte.logica.datatypes.DTUsuario;
import com.culturarte.logica.enums.TipoEstado;
import com.culturarte.soap.gen.GetUsuarioResponse;
import com.culturarte.soap.gen.PropuestaType;
import com.culturarte.soap.gen.VerificarEmailResponse;
import com.culturarte.soap.gen.VerificarNicknameResponse;
import com.culturarte.web.soap.client.UsuarioSoapClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.datatype.DatatypeFactory;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioSoapClient usuariosSoapClient;

    // ------------------- ALTA USUARIO -------------------
    @GetMapping("/alta")
    public String altaUsuario() {
        logger.info("Mostrando formulario de alta de usuario");
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
            @RequestParam(required = false) MultipartFile imagenFile,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String biografia,
            @RequestParam(required = false) String web,
            Model model,
            HttpSession session
    ) {
        logger.info("=== INICIO altaUsuario (POST) ===");
        logger.info("Alta de usuario: nickname={}, rol={}, email={}", nickname, rol, email);
        try {
            LocalDate fechaNac = LocalDate.parse(fecha);
            String imagen = null;

            // Guardar imagen si se sube
            if (imagenFile != null && !imagenFile.isEmpty()) {
                logger.debug("Procesando imagen para usuario: {}", nickname);
                Path directorio = Paths.get(System.getProperty("user.dir"), "uploads", "imagenes");
                if (!Files.exists(directorio)) Files.createDirectories(directorio);

                String nombreArchivo = nickname + "_" + System.currentTimeMillis() + "_" + imagenFile.getOriginalFilename();
                Path rutaCompleta = directorio.resolve(nombreArchivo);
                Files.copy(imagenFile.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);
                imagen = "uploads/imagenes/" + nombreArchivo;
                logger.debug("Imagen guardada: {}", imagen);
            }

            // Crear usuario según tipo
            if ("proponente".equalsIgnoreCase(rol)) {
                logger.info("Creando proponente: {}", nickname);
                usuariosSoapClient.agregarProponente(nickname, password, nombre, apellido, email,
                        DatatypeFactory.newInstance().newXMLGregorianCalendar(fechaNac.toString()),
                        imagen, direccion, web, biografia);
            } else if ("colaborador".equalsIgnoreCase(rol)) {
                logger.info("Creando colaborador: {}", nickname);
                usuariosSoapClient.agregarColaborador(nickname, password, nombre, apellido, email,
                        DatatypeFactory.newInstance().newXMLGregorianCalendar(fechaNac.toString()), imagen);
            } else {
                logger.warn("Rol inválido en alta de usuario: {}", rol);
                model.addAttribute("mensaje", "⚠️ Debe seleccionar un rol válido");
                return "altaUsuario";
            }

            // Obtener usuario recién creado y guardarlo en sesión
            logger.debug("Obteniendo usuario recién creado: {}", nickname);
            DTUsuario usuario = convertirDT(usuariosSoapClient.getUsuario(nickname).getUsuario());
            session.setAttribute("usuarioLogueado", usuario);
            logger.info("Usuario creado exitosamente: {} (rol: {})", nickname, rol);
            logger.debug("=== FIN altaUsuario (exitoso) ===");
            return "redirect:/";

        } catch (Exception e) {
            logger.error("=== ERROR en altaUsuario ===", e);
            logger.error("Error al crear usuario: nickname={}, rol={}", nickname, rol);
            model.addAttribute("mensaje", "⚠️ " + e.getMessage());
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
    }

    // ------------------- PERFIL USUARIO -------------------
    @GetMapping("/{nick}")
    public String mostrarPerfil(@PathVariable String nick, HttpSession session, Model model) {
        logger.info("=== INICIO mostrarPerfil ===");
        logger.info("Mostrando perfil de usuario: {}", nick);
        try {
            DTUsuario usuarioLogueado = (DTUsuario) session.getAttribute("usuarioLogueado");
            if (usuarioLogueado == null) {
                logger.debug("No hay usuario en sesión, creando visitante");
                usuarioLogueado = new DTUsuario();
                usuarioLogueado.setTipo("visitante");
                usuarioLogueado.setNickname("visitante");
                session.setAttribute("usuarioLogueado", usuarioLogueado);
            }
            model.addAttribute("usuarioLogueado", usuarioLogueado);

            logger.debug("Obteniendo usuario desde SOAP: {}", nick);
            GetUsuarioResponse usuarioResponse = usuariosSoapClient.getUsuario(nick);
            if (usuarioResponse == null) {
                logger.warn("Respuesta SOAP null al obtener usuario: {}", nick);
                model.addAttribute("mensajeError", "Usuario no encontrado");
                return "error/404";
            }
            
            if (usuarioResponse.getUsuario() == null) {
                logger.warn("Usuario null en respuesta SOAP: {}", nick);
                model.addAttribute("mensajeError", "Usuario no encontrado");
                return "error/404";
            }

            DTUsuario perfilVisitado = convertirDT(usuarioResponse.getUsuario());
            if (perfilVisitado == null) {
                logger.warn("Error al convertir usuario a DTUsuario: {}", nick);
                model.addAttribute("mensajeError", "Error al cargar el perfil");
                return "error/404";
            }

            model.addAttribute("perfilVisitado", perfilVisitado);
            model.addAttribute("esMiPropioPerfil", perfilVisitado.getNickname().equals(usuarioLogueado.getNickname()));
            
            // Verificar si lo sigue (solo si no es visitante)
            boolean loSigo = false;
            if (usuarioLogueado != null && !"visitante".equals(usuarioLogueado.getTipo())) {
                try {
                    loSigo = usuarioLogueado.buscarUsuarioSeguido(perfilVisitado.getNickname());
                } catch (Exception e) {
                    logger.debug("Error al verificar si sigue al usuario (puede ser normal): {}", e.getMessage());
                }
            }
            model.addAttribute("loSigo", loSigo);

            // Propuestas favoritas del usuario
            logger.debug("Obteniendo propuestas favoritas de: {}", nick);
            List<DTPropuesta> favoritas = new ArrayList<>();
            try {
                List<PropuestaType> favoritasSoap = usuariosSoapClient.getPropuestasFavoritas(perfilVisitado.getNickname());
                if (favoritasSoap != null) {
                    favoritas = favoritasSoap.stream()
                            .map(this::convertirPropuesta)
                            .filter(p -> p != null)
                            .collect(Collectors.toList());
                }
            } catch (Exception e) {
                logger.warn("Error al obtener propuestas favoritas (continuando sin ellas): {}", e.getMessage());
            }
            model.addAttribute("propuestasFavoritas", favoritas);
            logger.info("Perfil mostrado exitosamente: {} ({} favoritas)", nick, favoritas.size());
            logger.debug("=== FIN mostrarPerfil ===");
            return "perfil";
        } catch (Exception e) {
            logger.error("=== ERROR en mostrarPerfil ===", e);
            logger.error("Error al mostrar perfil de usuario: {}", nick, e);
            model.addAttribute("mensajeError", "Error al cargar el perfil: " + e.getMessage());
            return "error/404";
        }
    }

    // ------------------- RANKING -------------------
    @GetMapping("/ranking")
    public String rankingUsu(Model model) {
        logger.info("=== INICIO rankingUsu ===");
        try {
            logger.debug("Obteniendo lista de usuarios para ranking");
            List<DTUsuario> usuarios = usuariosSoapClient.listarUsuarios()
                    .stream().map(this::convertirDT).collect(Collectors.toList());
            model.addAttribute("usuarios", usuarios);
            logger.info("Ranking cargado exitosamente: {} usuarios", usuarios.size());
            logger.debug("=== FIN rankingUsu (exitoso) ===");
        } catch (Exception e) {
            logger.error("=== ERROR en rankingUsu ===", e);
            model.addAttribute("usuarios", new ArrayList<>());
            model.addAttribute("mensaje", "⚠️ Error al cargar el ranking: " + e.getMessage());
        }
        return "rankingUsuarios";
    }

    // ------------------- BUSCAR -------------------
    @GetMapping("/buscar")
    public String buscarUsuarios(@RequestParam(required = false) String nombre, Model model) {
        logger.info("=== INICIO buscarUsuarios ===");
        logger.info("Búsqueda de usuarios: nombre={}", nombre);
        try {
            List<DTUsuario> resultados = usuariosSoapClient.buscarUsuarios(nombre)
                    .stream().map(this::convertirDT).collect(Collectors.toList());
            model.addAttribute("resultados", resultados);
            model.addAttribute("nombre", nombre);
            logger.info("Búsqueda completada: {} resultados", resultados.size());
            logger.debug("=== FIN buscarUsuarios ===");
        } catch (Exception e) {
            logger.error("=== ERROR en buscarUsuarios ===", e);
        }
        return "busquedaUsuario";
    }

    // ------------------- SEGUIR / DEJAR DE SEGUIR -------------------
    @PostMapping("/seguir")
    public String seguir(@RequestParam String nickSeguido, HttpSession session, HttpServletRequest request) {
        DTUsuario usuarioLogueado = (DTUsuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || "visitante".equals(usuarioLogueado.getTipo())) return "redirect:/login";

        try {
            usuariosSoapClient.seguirUsuario(usuarioLogueado.getNickname(), nickSeguido);
        } catch (Exception ignored) {}

        usuarioLogueado = convertirDT(usuariosSoapClient.getUsuario(usuarioLogueado.getNickname()).getUsuario());
        session.setAttribute("usuarioLogueado", usuarioLogueado);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/dejarDeSeguir")
    public String dejarDeSeguir(@RequestParam String nickSeguido, HttpSession session, HttpServletRequest request) {
        DTUsuario usuarioLogueado = (DTUsuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || "visitante".equals(usuarioLogueado.getTipo())) return "redirect:/login";

        try {
            usuariosSoapClient.dejarDeSeguirUsuario(usuarioLogueado.getNickname(), nickSeguido);
        } catch (Exception ignored) {}

        usuarioLogueado = convertirDT(usuariosSoapClient.getUsuario(usuarioLogueado.getNickname()).getUsuario());
        session.setAttribute("usuarioLogueado", usuarioLogueado);
        return "redirect:" + request.getHeader("Referer");
    }

    // ------------------- FAVORITOS -------------------
    @PostMapping("/agregar-favorito")
    public String agregarFavorito(@RequestParam String titulo, HttpSession session, HttpServletRequest request) {
        DTUsuario usuarioLogueado = (DTUsuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || "visitante".equals(usuarioLogueado.getTipo())) return "redirect:/login";

        try {
            usuariosSoapClient.agregarPropuestaFavorita(usuarioLogueado.getNickname(), titulo);
        } catch (Exception ignored) {}

        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/sacar-favorito")
    public String sacarFavorito(@RequestParam String titulo, HttpSession session, HttpServletRequest request) {
        DTUsuario usuarioLogueado = (DTUsuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null || "visitante".equals(usuarioLogueado.getTipo())) return "redirect:/login";

        try {
            usuariosSoapClient.sacarPropuestaFavorita(usuarioLogueado.getNickname(), titulo);
        } catch (Exception ignored) {}

        return "redirect:" + request.getHeader("Referer");
    }

    // ------------------- VERIFICACIONES AJAX -------------------
    @GetMapping("/verificar-nickname")
    @ResponseBody
    public VerificacionResponse verificarNickname(@RequestParam("nickname") String nickname) {
        logger.debug("Verificando disponibilidad de nickname: {}", nickname);
        try {
            VerificarNicknameResponse resp = usuariosSoapClient.verificarNickname(nickname);
            boolean disponible = resp.isDisponible();
            logger.debug("Nickname '{}' disponible: {}", nickname, disponible);
            return new VerificacionResponse(disponible, resp.getMensaje());
        } catch (Exception e) {
            logger.error("Error al verificar nickname: {}", nickname, e);
            return new VerificacionResponse(false, "Error al verificar disponibilidad");
        }
    }

    @GetMapping("/verificar-email")
    @ResponseBody
    public VerificacionResponse verificarEmail(@RequestParam("email") String email) {
        logger.debug("Verificando disponibilidad de email: {}", email);
        try {
            VerificarEmailResponse resp = usuariosSoapClient.verificarEmail(email);
            boolean disponible = resp.isDisponible();
            logger.debug("Email '{}' disponible: {}", email, disponible);
            return new VerificacionResponse(disponible, resp.getMensaje());
        } catch (Exception e) {
            logger.error("Error al verificar email: {}", email, e);
            return new VerificacionResponse(false, "Error al verificar disponibilidad");
        }
    }

    // ------------------- UTILIDADES -------------------
    private DTUsuario convertirDT(com.culturarte.soap.gen.UsuarioType u) {
        if (u == null) return null;
        DTUsuario dt = new DTUsuario();
        dt.setNickname(u.getNickname());
        dt.setNombre(u.getNombre());
        dt.setApellido(u.getApellido());
        dt.setEmail(u.getEmail());
        dt.setImagen(u.getImagen());
        dt.setTipo(u.getTipo());
        if (u.getFechaNacimiento() != null)
            dt.setFechaNacimiento(u.getFechaNacimiento().toGregorianCalendar().toZonedDateTime().toLocalDate());
        return dt;
    }

    private DTPropuesta convertirPropuesta(PropuestaType p) {
        if (p == null) {
            return null;
        }
        TipoEstado estado = null;
        if (p.getEstado() != null) {
            try {
                estado = TipoEstado.valueOf(p.getEstado());
            } catch (IllegalArgumentException ignored) {
            }
        }
        java.time.LocalDate fechaPrevista = null;
        if (p.getFechaPrevista() != null) {
            fechaPrevista = p.getFechaPrevista().toGregorianCalendar().toZonedDateTime().toLocalDate();
        }
        int cantColaboradores = p.getCantColaboradores() != null ? p.getCantColaboradores() : 0;
        float montoRecaudado = p.getMontoRecaudado() != null ? p.getMontoRecaudado() : 0f;
        float montoNecesario = p.getMontoNecesario() != null ? p.getMontoNecesario() : 0f;
        return new DTPropuesta(
                p.getTitulo(),
                p.getDescripcion() != null ? p.getDescripcion() : "",
                estado,
                cantColaboradores,
                montoRecaudado,
                montoNecesario,
                fechaPrevista,
                p.getImagenBase64(),
                p.getCategoria(),
                p.getProponente()
        );
    }

    public static class VerificacionResponse {
        private boolean disponible;
        private String mensaje;

        public VerificacionResponse() {}
        public VerificacionResponse(boolean disponible, String mensaje) { this.disponible = disponible; this.mensaje = mensaje; }

        public boolean isDisponible() { return disponible; }
        public void setDisponible(boolean disponible) { this.disponible = disponible; }
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    }
}
