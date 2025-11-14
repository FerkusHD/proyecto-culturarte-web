package com.culturarte.web.controller;

import com.culturarte.logica.datatypes.DTColaboracion;
import com.culturarte.logica.datatypes.DTPropuesta;
import com.culturarte.logica.enums.TipoRetorno;
import com.culturarte.soap.gen.GetColaboracionResponse;
import com.culturarte.web.service.PDFService;
import com.culturarte.web.soap.client.ColaboracionSoapClient;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

            DTColaboracion colaboracion = convertirColaboracion(soapResponse.getColaboracion());
            DTPropuesta propuesta = convertirPropuesta(soapResponse.getPropuesta());

            if (colaboracion == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No se pudo convertir la colaboración");
                return;
            }

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

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ISO_TIME;

    private DTColaboracion convertirColaboracion(com.culturarte.soap.gen.DTColaboracion soapColaboracion) {
        if (soapColaboracion == null) {
            return null;
        }
        LocalDate fecha = parseFecha(soapColaboracion.getFecha());
        LocalTime hora = parseHora(soapColaboracion.getHora());
        TipoRetorno tipoRetorno = parseTipoRetorno(soapColaboracion.getTipoRetorno());

        DTColaboracion dt = new DTColaboracion(
                soapColaboracion.getNickColaborador(),
                soapColaboracion.getTituloPropuesta(),
                fecha,
                hora,
                soapColaboracion.getMonto(),
                tipoRetorno
        );
        return dt;
    }

    private DTPropuesta convertirPropuesta(com.culturarte.soap.gen.DTPropuesta soapPropuesta) {
        if (soapPropuesta == null) {
            return null;
        }
        return new DTPropuesta(
                soapPropuesta.getTitulo(),
                soapPropuesta.getDescripcion() != null ? soapPropuesta.getDescripcion() : "",
                null,
                0,
                0f,
                0f,
                null,
                "",
                "",
                ""
        );
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
