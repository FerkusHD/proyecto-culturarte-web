package com.culturarte.web.controller;

import com.culturarte.soap.gen.AgregarComentarioRequest;
import com.culturarte.soap.gen.AgregarComentarioResponse;
import com.culturarte.soap.gen.AgregarFavoritaRequest;
import com.culturarte.soap.gen.AgregarFavoritaResponse;
import com.culturarte.soap.gen.AltaColaboracionRequest;
import com.culturarte.soap.gen.AltaColaboracionResponse;
import com.culturarte.soap.gen.CancelarPropuestaRequest;
import com.culturarte.soap.gen.CancelarPropuestaResponse;
import com.culturarte.soap.gen.PropuestaType;
import com.culturarte.logica.datatypes.DTUsuario;
import com.culturarte.soap.gen.QuitarFavoritaRequest;
import com.culturarte.soap.gen.QuitarFavoritaResponse;
import com.culturarte.soap.gen.UsuarioType;
import com.culturarte.web.soap.client.PropuestasSoapClient;
import com.culturarte.web.soap.client.UsuarioSoapClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/propuestas")
public class PropuestasController {

    @Autowired
    private PropuestasSoapClient soapClient; // Cliente SOAP inyectado

    @Autowired
    private UsuarioSoapClient usuarioSoapClient; // Cliente SOAP de usuarios

    // --- Listar todas las propuestas ---
    @GetMapping("/listar")
    @ResponseBody
    public List<PropuestaType> listarPropuestas() {
        try {
            return soapClient.listarPropuestas();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // --- Ver detalle de una propuesta ---
    @GetMapping("/{titulo}")
    public String mostrarPropuesta(
            @PathVariable String titulo,
            Model model,
            HttpSession session,
            HttpServletRequest request) {

        try {
            PropuestaType propuesta = soapClient.getPropuesta(titulo);

            if (propuesta == null) {
                model.addAttribute("mensajeError", "⚠️ La propuesta no existe");
                return "redirect:/propuestas/listar";
            }

            model.addAttribute("propuesta", propuesta);

            // Obtener proponente completo
            if (propuesta.getProponente() != null && !propuesta.getProponente().isEmpty()) {
                try {
                    UsuarioType proponente = usuarioSoapClient.getUsuario(propuesta.getProponente()).getUsuario();
                    model.addAttribute("proponente", proponente);
                } catch (Exception e) {
                    // Si falla, no se agrega proponente (el JSP manejará el caso null)
                }
            }

            // Obtener colaboradores completos
            List<UsuarioType> colaboradores = new ArrayList<>();
            if (propuesta.getColaboradores() != null && !propuesta.getColaboradores().isEmpty()) {
                for (String nickColaborador : propuesta.getColaboradores()) {
                    try {
                        UsuarioType colaborador = usuarioSoapClient.getUsuario(nickColaborador).getUsuario();
                        if (colaborador != null) {
                            colaboradores.add(colaborador);
                        }
                    } catch (Exception e) {
                        // Continuar con el siguiente colaborador si falla
                    }
                }
            }
            model.addAttribute("colaboradores", colaboradores);

            // Obtener usuario logueado completo
            Object usuarioLogueadoObj = session.getAttribute("usuarioLogueado");
            DTUsuario usuarioLogueado = null;
            String nickUsuarioFinal = null;
            
            if (usuarioLogueadoObj instanceof DTUsuario) {
                usuarioLogueado = (DTUsuario) usuarioLogueadoObj;
                nickUsuarioFinal = usuarioLogueado.getNickname();
            } else if (usuarioLogueadoObj instanceof com.culturarte.soap.gen.GetUsuarioResponse) {
                com.culturarte.soap.gen.GetUsuarioResponse resp = (com.culturarte.soap.gen.GetUsuarioResponse) usuarioLogueadoObj;
                if (resp.getUsuario() != null) {
                    usuarioLogueado = convertirDT(resp.getUsuario());
                    nickUsuarioFinal = resp.getUsuario().getNickname();
                }
            } else if (usuarioLogueadoObj instanceof UsuarioType) {
                UsuarioType u = (UsuarioType) usuarioLogueadoObj;
                usuarioLogueado = convertirDT(u);
                nickUsuarioFinal = u.getNickname();
            }
            
            if (usuarioLogueado == null) {
                usuarioLogueado = new DTUsuario();
                usuarioLogueado.setTipo("visitante");
                usuarioLogueado.setNickname("visitante");
            }
            
            final String nickUsuario = nickUsuarioFinal != null ? nickUsuarioFinal : "visitante";
            model.addAttribute("usuarioLogueado", usuarioLogueado);
            model.addAttribute("nickUsuario", nickUsuario);

            // Verificar si es favorita
            boolean esFavorita = false;
            if (nickUsuario != null && !"visitante".equals(nickUsuario)) {
                try {
                    List<PropuestaType> favoritas = usuarioSoapClient.getPropuestasFavoritas(nickUsuario);
                    final String tituloFinal = titulo;
                    esFavorita = favoritas != null && favoritas.stream()
                            .anyMatch(p -> p.getTitulo() != null && p.getTitulo().equals(tituloFinal));
                } catch (Exception e) {
                    // Si falla, se asume que no es favorita
                }
            }
            model.addAttribute("esFavorita", esFavorita);

            // Verificar si puede comentar (colaborador que ha colaborado)
            boolean puedeComentar = false;
            if (usuarioLogueado != null && "colaborador".equals(usuarioLogueado.getTipo()) && nickUsuario != null) {
                final String nickUsuarioParaLambda = nickUsuario;
                puedeComentar = colaboradores.stream()
                        .anyMatch(c -> c.getNickname() != null && c.getNickname().equals(nickUsuarioParaLambda));
            }
            model.addAttribute("puedeComentar", puedeComentar);

            // Detección de dispositivo
            String userAgent = request.getHeader("User-Agent");
            boolean esMovil = userAgent != null && userAgent.toLowerCase().matches(".*(mobi|android|iphone|ipad).*");

            return esMovil ? "consultarPropuestaMovil" : "consultarPropuesta";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensajeError", "❌ Error al cargar la propuesta.");
            return "error";
        }
    }

    // Método auxiliar para convertir UsuarioType a DTUsuario
    private DTUsuario convertirDT(UsuarioType u) {
        if (u == null) return null;
        DTUsuario dt = new DTUsuario();
        dt.setNickname(u.getNickname());
        dt.setNombre(u.getNombre());
        dt.setApellido(u.getApellido());
        dt.setEmail(u.getEmail());
        dt.setImagen(u.getImagen());
        dt.setTipo(u.getTipo());
        if (u.getFechaNacimiento() != null) {
            dt.setFechaNacimiento(u.getFechaNacimiento().toGregorianCalendar().toZonedDateTime().toLocalDate());
        }
        // Nota: UsuarioType no tiene getUsuariosSeguidos(), 
        // los usuarios seguidos se cargan cuando se necesita desde el servicio
        return dt;
    }

    // --- Alta de colaboración ---
    @PostMapping("/altaColaboracion")
    public String altaColaboracion(
            @RequestParam("monto") float monto,
            @RequestParam("tipoRetorno") String tipoRetorno,
            @RequestParam("tituloPropuesta") String tituloPropuesta,
            @RequestParam("nickColaborador") String nickColaborador,
            RedirectAttributes redirectAttributes) {

        try {
            AltaColaboracionRequest request = new AltaColaboracionRequest();
            request.setMonto(monto);
            request.setTipoRetorno(tipoRetorno);
            request.setTituloPropuesta(tituloPropuesta);
            request.setNickColaborador(nickColaborador);

            AltaColaboracionResponse response = soapClient.altaColaboracion(request);

            if (response.isExito()) {
                redirectAttributes.addFlashAttribute("mensajeExito", response.getMensaje());
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", response.getMensaje());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "❌ Error al registrar la colaboración.");
        }

        return "redirect:/propuestas/" + tituloPropuesta;
    }

    // --- Agregar comentario ---
    @PostMapping("/agregarComentario")
    public String agregarComentario(
            @RequestParam String tituloPropuesta,
            @RequestParam String texto,
            @RequestParam String nickColaborador,
            RedirectAttributes redirectAttributes) {

        try {
            AgregarComentarioRequest request = new AgregarComentarioRequest();
            request.setTituloPropuesta(tituloPropuesta);
            request.setTexto(texto);
            request.setNickColaborador(nickColaborador);

            AgregarComentarioResponse response = soapClient.agregarComentario(request);

            if (response.isExito()) {
                redirectAttributes.addFlashAttribute("mensajeExito", response.getMensaje());
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", response.getMensaje());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "❌ Error al agregar comentario.");
        }

        return "redirect:/propuestas/" + tituloPropuesta;
    }

    // --- Agregar a favoritas ---
    @PostMapping("/agregarFavorita")
    public String agregarFavorita(
            @RequestParam String tituloPropuesta,
            @RequestParam String nickUsuario,
            RedirectAttributes redirectAttributes) {

        try {
            AgregarFavoritaRequest request = new AgregarFavoritaRequest();
            request.setTituloPropuesta(tituloPropuesta);
            request.setNickUsuario(nickUsuario);

            AgregarFavoritaResponse response = soapClient.agregarFavorita(request);

            if (response.isExito()) {
                redirectAttributes.addFlashAttribute("mensajeExito", response.getMensaje());
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", response.getMensaje());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "❌ Error al agregar favorita.");
        }

        return "redirect:/propuestas/" + tituloPropuesta;
    }

    // --- Quitar de favoritas ---
    @PostMapping("/quitarFavorita")
    public String quitarFavorita(
            @RequestParam String tituloPropuesta,
            @RequestParam String nickUsuario,
            RedirectAttributes redirectAttributes) {

        try {
            QuitarFavoritaRequest request = new QuitarFavoritaRequest();
            request.setTituloPropuesta(tituloPropuesta);
            request.setNickUsuario(nickUsuario);

            QuitarFavoritaResponse response = soapClient.quitarFavorita(request);

            if (response.isExito()) {
                redirectAttributes.addFlashAttribute("mensajeExito", response.getMensaje());
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", response.getMensaje());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "❌ Error al quitar favorita.");
        }

        return "redirect:/propuestas/" + tituloPropuesta;
    }

    // --- Cancelar propuesta ---
    @PostMapping("/cancelar/{titulo}")
    public String cancelarPropuesta(
            @PathVariable String titulo,
            RedirectAttributes redirectAttributes) {

        try {
            CancelarPropuestaRequest request = new CancelarPropuestaRequest();
            request.setTituloPropuesta(titulo);

            CancelarPropuestaResponse response = soapClient.cancelarPropuesta(request);

            if (response.isExito()) {
                redirectAttributes.addFlashAttribute("mensajeExito", response.getMensaje());
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", response.getMensaje());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "❌ Error al cancelar la propuesta.");
        }

        return "redirect:/propuestas/" + titulo;
    }
}
