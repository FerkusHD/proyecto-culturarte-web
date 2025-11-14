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
import com.culturarte.soap.gen.QuitarFavoritaRequest;
import com.culturarte.soap.gen.QuitarFavoritaResponse;
import com.culturarte.web.soap.client.PropuestasSoapClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/propuestas")
public class PropuestasController {

    @Autowired
    private PropuestasSoapClient soapClient; // Cliente SOAP inyectado

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

            // Usuario logueado (si existe)
            String nickUsuario = (String) session.getAttribute("usuarioLogueado");
            model.addAttribute("nickUsuario", nickUsuario);

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
