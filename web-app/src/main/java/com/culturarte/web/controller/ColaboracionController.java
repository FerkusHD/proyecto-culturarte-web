package com.culturarte.web.controller;

import com.culturarte.logica.datatypes.DTColaboracion;
import com.culturarte.logica.datatypes.DTPropuesta;
import com.culturarte.web.service.PDFService;
import com.culturarte.web.soapclient.ColaboracionSoapClient;
import culturarte.soap.colaboraciones.GetColaboracionResponse;
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
    private ColaboracionSoapClient colaboracionSoapClient;

    @Autowired
    private PDFService pdfService;

    /**
     * Genera y descarga el PDF de constancia de pago para una colaboración.
     *
     * @param nickColaborador Nickname del colaborador
     * @param tituloPropuesta Título de la propuesta
     * @param session         Sesión HTTP
     * @param response        Respuesta HTTP
     */
    @GetMapping("/constancia-pago")
    public void descargarConstanciaPago(
            @RequestParam("nickColaborador") String nickColaborador,
            @RequestParam("tituloPropuesta") String tituloPropuesta,
            HttpSession session,
            HttpServletResponse response) {

        try {
            Object usuarioLogueadoObj = session.getAttribute("usuarioLogueado");
            if (usuarioLogueadoObj == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Debe iniciar sesión");
                return;
            }

            GetColaboracionResponse soapResponse =
                    colaboracionSoapClient.getColaboracion(nickColaborador, tituloPropuesta);

            if (soapResponse == null || soapResponse.getColaboracion() == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Colaboración no encontrada");
                return;
            }

            DTColaboracion colaboracion = soapResponse.getColaboracion();
            DTPropuesta propuesta = soapResponse.getPropuesta();

            // generar el PDF con la info obtenida
            byte[] pdfBytes = pdfService.generarConstanciaPago(
                    colaboracion,
                    null,
                    propuesta
            );

            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"constancia-pago-" +
                            nickColaborador + "-" +
                            tituloPropuesta.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf\""
            );
            response.setContentLength(pdfBytes.length);

            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();

        } catch (Exception e) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Error al generar la constancia de pago: " + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
