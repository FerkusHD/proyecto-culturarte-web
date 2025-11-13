package com.culturarte.web.controller;

import com.culturarte.logica.IControlador;
import com.culturarte.logica.datatypes.DTColaboracion;
import com.culturarte.logica.datatypes.DTColaborador;
import com.culturarte.logica.datatypes.DTPropuesta;
import com.culturarte.web.service.PDFService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@RequestMapping("/colaboraciones")
public class ColaboracionController {

    @Autowired
    private IControlador ctrl;

    @Autowired
    private PDFService pdfService;

    /**
     * Genera y descarga el PDF de constancia de pago para una colaboración.
     * 
     * @param nickColaborador Nickname del colaborador
     * @param tituloPropuesta Título de la propuesta
     * @param session Sesión HTTP
     * @param response Respuesta HTTP
     * @return Redirección o error
     */
    @GetMapping("/constancia-pago")
    public void descargarConstanciaPago(
            @RequestParam("nickColaborador") String nickColaborador,
            @RequestParam("tituloPropuesta") String tituloPropuesta,
            HttpSession session,
            HttpServletResponse response) {

        try {
            // Verificar que el usuario esté logueado y sea el propietario de la colaboración
            Object usuarioLogueadoObj = session.getAttribute("usuarioLogueado");
            if (usuarioLogueadoObj == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Debe iniciar sesión");
                return;
            }

            // Obtener información del colaborador
            DTColaborador colaborador = ctrl.getDTColaborador(nickColaborador);
            if (colaborador == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Colaborador no encontrado");
                return;
            }

            // Buscar la colaboración específica
            DTColaboracion colaboracion = null;
            if (colaborador.getColaboraciones() != null) {
                for (DTColaboracion col : colaborador.getColaboraciones()) {
                    if (col.getTituloPropuesta() != null && 
                        col.getTituloPropuesta().equals(tituloPropuesta)) {
                        colaboracion = col;
                        break;
                    }
                }
            }

            if (colaboracion == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, 
                        "Colaboración no encontrada");
                return;
            }

            // Obtener información de la propuesta
            DTPropuesta propuesta = colaboracion.getPropuesta();
            if (propuesta == null) {
                // Si no está en la colaboración, obtenerla por separado
                propuesta = ctrl.getDTPropuesta(tituloPropuesta);
            }

            // Generar el PDF
            byte[] pdfBytes = pdfService.generarConstanciaPago(
                    colaboracion, colaborador, propuesta);

            // Configurar la respuesta HTTP para descargar el PDF
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"constancia-pago-" + 
                    nickColaborador + "-" + 
                    tituloPropuesta.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf\"");
            response.setContentLength(pdfBytes.length);

            // Escribir el PDF en la respuesta
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();

        } catch (Exception e) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                        "Error al generar la constancia de pago: " + e.getMessage());
            } catch (IOException ioException) {
                e.printStackTrace();
            }
        }
    }
}

