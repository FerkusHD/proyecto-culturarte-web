package com.culturarte.web.controller;

import com.culturarte.logica.datatypes.DTColaboracion;
import com.culturarte.logica.datatypes.DTPropuesta;
import com.culturarte.logica.datatypes.DTUsuario;
import com.culturarte.logica.enums.TipoRetorno;
import com.culturarte.soap.endpoint.PropuestasEndpoint;
import com.culturarte.soap.gen.ColaboracionType;
import com.culturarte.soap.gen.ColaboradorType;
import com.culturarte.soap.gen.GetColaboracionResponse;
import com.culturarte.soap.gen.PropuestaType;
import com.culturarte.web.service.PDFService;
import com.culturarte.web.soap.client.ColaboracionSoapClient;
import com.culturarte.web.soap.client.UsuarioSoapClient;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping("/colaboraciones")
public class ColaboracionController {

    private static final Logger logger = LoggerFactory.getLogger(ColaboracionController.class);

    @Autowired
    private ColaboracionSoapClient colaboracionSoapClient;

    @Autowired
    private UsuarioSoapClient usuarioSoapClient;

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
        logger.info("=== INICIO descargarConstanciaPago ===");
        logger.info("Solicitud de constancia: colaborador={}, propuesta={}", nickColaborador, tituloPropuesta);
        try {
            DTUsuario usuarioLogueadoObj =(DTUsuario) session.getAttribute("usuarioLogueado");
            if (usuarioLogueadoObj == null) {
                logger.warn("Intento de descargar constancia sin sesión");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Debe iniciar sesión");
                return;
            }

            logger.debug("Obteniendo colaboración desde SOAP");

            ColaboradorType colaborador = usuarioSoapClient.getDTColaborador(nickColaborador);

            ColaboracionType colaboraciónElegida = null;
            PropuestaType propuesta = null;
            for (ColaboracionType colab : colaborador.getColaboraciones()) {
                if(colab.getPropuesta().getTitulo().equals(tituloPropuesta)){
                    colaboraciónElegida = colab;
                    propuesta = colab.getPropuesta();
                    break;
                }
            }

            logger.debug("Generando PDF de constancia de pago");
            byte[] pdfBytes = pdfService.generarConstanciaPago(
                    colaboraciónElegida,
                    colaborador,
                    propuesta
            );

            String filename = "constancia-pago-" + nickColaborador + "-" +
                    tituloPropuesta.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";
            
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
            response.setContentLength(pdfBytes.length);

            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
            
            logger.info("Constancia de pago generada y enviada exitosamente: {} bytes", pdfBytes.length);
            logger.debug("=== FIN descargarConstanciaPago (exitoso) ===");

        } catch (Exception e) {
            logger.error("=== ERROR en descargarConstanciaPago ===", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Error al generar la constancia de pago: " + e.getMessage());
            } catch (IOException ioException) {
                logger.error("Error adicional al enviar error HTTP", ioException);
            }
        }
    }

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ISO_TIME;

    private DTColaboracion convertirColaboracion(com.culturarte.soap.gen.ColaboracionType soapColaboracion) {
        if (soapColaboracion == null) {
            return null;
        }
        LocalDate fecha = parseFecha(soapColaboracion.getFecha());
        LocalTime hora = parseHora(soapColaboracion.getHora());
        TipoRetorno tipoRetorno = parseTipoRetorno(soapColaboracion.getTipoRetorno());

        DTColaboracion dt = new DTColaboracion(
                soapColaboracion.getNickColaborador(),
                soapColaboracion.getPropuesta().getTitulo(),
                fecha,
                hora,
                soapColaboracion.getMonto(),
                tipoRetorno
        );
        return dt;
    }

    private LocalDate parseFecha(String fecha) {
        if (fecha == null || fecha.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(fecha, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private LocalTime parseHora(String hora) {
        if (hora == null || hora.isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(hora, TIME_FORMAT);
        } catch (DateTimeParseException e) {
            try {
                return LocalTime.parse(hora);
            } catch (DateTimeParseException ignored) {
                return null;
            }
        }
    }

    private TipoRetorno parseTipoRetorno(String tipo) {
        if (tipo == null) {
            return null;
        }
        try {
            return TipoRetorno.valueOf(tipo);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
