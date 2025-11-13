package com.culturarte.web.controller;

import com.culturarte.logica.datatypes.DTPropuesta;
import com.culturarte.logica.datatypes.DTUsuario;
import com.culturarte.web.soap.client.UsuarioSoapClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.datatype.DatatypeFactory;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioSoapClient usuariosSoapClient;

    // ------------------- ALTA USUARIO -------------------
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
            @RequestParam(required = false) MultipartFile imagenFile,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String biografia,
            @RequestParam(required = false) String web,
            Model model,
            HttpSession session
    ) {
        LocalDate fechaNac = LocalDate.parse(fecha);
        String imagen = null;

        try {
            // Guardar imagen si se sube
            if (imagenFile != null && !imagenFile.isEmpty()) {
                Path directorio = Paths.get(System.getProperty("user.dir"), "uploads", "imagenes");
                if (!Files.exists(directorio)) Files.createDirectories(directorio);

                String nombreArchivo = nickname + "_" + System.currentTimeMillis() + "_" + imagenFile.getOriginalFilename();
                Path rutaCompleta = directorio.resolve(nombreArchivo);
                Files.copy(imagenFile.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);
                imagen = "uploads/imagenes/" + nombreArchivo;
            }

            // Crear usuario según tipo
            if ("proponente".equalsIgnoreCase(rol)) {
                usuariosSoapClient.agregarProponente(nickname, password, nombre, apellido, email,
                        DatatypeFactory.newInstance().newXMLGregorianCalendar(fechaNac.toString()),
                        imagen, direccion, web, biografia);
            } else if ("colaborador".equalsIgnoreCase(rol)) {
                usuariosSoapClient.agregarColaborador(nickname, password, nombre, apellido, email,
                        DatatypeFactory.newInstance().newXMLGregorianCalendar(fechaNac.toString()), imagen);
            } else {
                model.addAttribute("mensaje", "⚠️ Debe seleccionar un rol válido");
                return "altaUsuario";
            }

            // Obtener usuario recién creado y guardarlo en sesión
            DTUsuario usuario = convertirDT(usuariosSoapClient.getUsuario(nickname).getUsuario());
            session.setAttribute("usuarioLogueado", usuario);
            return "redirect:/";

        } catch (Exception e) {
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
        DTUsuario usuarioLogueado = (DTUsuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            usuarioLogueado = new DTUsuario();
            usuarioLogueado.setTipo("visitante");
            usuarioLogueado.setNickname("visitante");
            session.setAttribute("usuarioLogueado", usuarioLogueado);
        }
        model.addAttribute("usuarioLogueado", usuarioLogueado);

        DTUsuario perfilVisitado = convertirDT(usuariosSoapClient.getUsuario(nick).getUsuario());
        if (perfilVisitado == null) return "error/404";

        model.addAttribute("perfilVisitado", perfilVisitado);
        model.addAttribute("esMiPropioPerfil", perfilVisitado.getNickname().equals(usuarioLogueado.getNickname()));
        model.addAttribute("loSigo", usuarioLogueado.buscarUsuarioSeguido(perfilVisitado.getNickname()));

        // Propuestas favoritas del usuario
        List<DTPropuesta> favoritas = usuariosSoapClient.getPropuestasFavoritas(perfilVisitado.getNickname())
                .stream().map(this::convertirPropuesta).collect(Collectors.toList());
        model.addAttribute("propuestasFavoritas", favoritas);

        return "perfil";
    }

    // ------------------- RANKING -------------------
    @GetMapping("/ranking")
    public String rankingUsu(Model model) {
        try {
            List<DTUsuario> usuarios = usuariosSoapClient.listarUsuarios()
                    .stream().map(this::convertirDT).collect(Collectors.toList());
            model.addAttribute("usuarios", usuarios);
        } catch (Exception e) {
            model.addAttribute("usuarios", new ArrayList<>());
            model.addAttribute("mensaje", "⚠️ Error al cargar el ranking: " + e.getMessage());
        }
        return "rankingUsuarios";
    }

    // ------------------- BUSCAR -------------------
    @GetMapping("/buscar")
    public String buscarUsuarios(@RequestParam(required = false) String nombre, Model model) {
        List<DTUsuario> resultados = usuariosSoapClient.buscarUsuarios(nombre)
                .stream().map(this::convertirDT).collect(Collectors.toList());
        model.addAttribute("resultados", resultados);
        model.addAttribute("nombre", nombre);
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
        return usuariosSoapClient.verificarNickname(nickname);
    }

    @GetMapping("/verificar-email")
    @ResponseBody
    public VerificacionResponse verificarEmail(@RequestParam("email") String email) {
        return usuariosSoapClient.verificarEmail(email);
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

    private DTPropuesta convertirPropuesta(com.culturarte.soap.gen.PropuestaType p) {
        DTPropuesta dp = new DTPropuesta();
        dp.setTitulo(p.getTitulo());
        dp.setDescripcion(p.getDescripcion());
        dp.setImagen(p.getImagen());
        dp.setFechaCreacion(p.getFechaCreacion() != null ? p.getFechaCreacion().toGregorianCalendar().toZonedDateTime().toLocalDate() : null);
        dp.setEstado(p.getEstado());
        return dp;
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
