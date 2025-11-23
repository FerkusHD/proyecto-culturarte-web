package com.culturarte.web.controller;

import com.culturarte.logica.datatypes.DTColaboracion;
import com.culturarte.logica.datatypes.DTColaborador;
import com.culturarte.logica.datatypes.DTPago;
import com.culturarte.logica.datatypes.DTProponente;
import com.culturarte.logica.datatypes.DTPropuesta;
import com.culturarte.logica.datatypes.DTUsuario;
import com.culturarte.logica.enums.TipoPago;
import com.culturarte.logica.enums.TipoRetorno;
import com.culturarte.logica.enums.TipoTarjeta;
import com.culturarte.soap.endpoint.PropuestasEndpoint;
import com.culturarte.soap.gen.ColaboracionType;
import com.culturarte.soap.gen.ColaboradorType;
import com.culturarte.soap.gen.GetColaboracionResponse;
import com.culturarte.soap.gen.ProponenteType;
import com.culturarte.soap.gen.PropuestaType;
import com.culturarte.web.service.EmailService;
import com.culturarte.web.service.PDFService;
import com.culturarte.web.soap.client.ColaboracionSoapClient;
import com.culturarte.web.soap.client.PropuestasSoapClient;
import com.culturarte.web.soap.client.UsuarioSoapClient;
import com.culturarte.web.util.MobileDetectionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

@Controller
@RequestMapping("/colaboraciones")
public class ColaboracionController {

    private static final Logger logger = LoggerFactory.getLogger(ColaboracionController.class);

    @Autowired
    private ColaboracionSoapClient colaboracionSoapClient;

    @Autowired
    private UsuarioSoapClient usuarioSoapClient;

    @Autowired
    private PropuestasSoapClient propuestasSoapClient;

    @Autowired
    private PDFService pdfService;

    @Autowired
    private EmailService emailService;

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
            DTUsuario usuarioLogueadoObj = (DTUsuario) session.getAttribute("usuarioLogueado");
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
                if (colab.getPropuesta().getTitulo().equals(tituloPropuesta)) {
                    colaboraciónElegida = colab;
                    propuesta = colab.getPropuesta();
                    break;
                }
            }

            logger.debug("Generando PDF de constancia de pago");
            byte[] pdfBytes = pdfService.generarConstanciaPago(
                    colaboraciónElegida,
                    colaborador,
                    propuesta);

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

    /**
     * Lista las colaboraciones sin pago del colaborador logueado (solo móvil)
     */
    @GetMapping("/pago/listar")
    public String listarColaboracionesSinPago(HttpServletRequest request, HttpSession session, Model model) {
        logger.info("=== INICIO listarColaboracionesSinPago ===");

        // Verificar que sea dispositivo móvil
        if (!MobileDetectionUtil.isMobileDevice(request)) {
            logger.warn("Intento de acceso desde dispositivo no móvil");
            model.addAttribute("error", "Esta funcionalidad solo está disponible desde dispositivos móviles");
            return "error";
        }

        // Verificar sesión
        DTUsuario usuarioLogueado = (DTUsuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            logger.warn("Intento de acceso sin sesión");
            return "redirect:/login";
        }

        try {
            // Obtener colaboraciones sin pago usando SOAP
            ArrayList<DTColaboracion> colaboraciones = colaboracionSoapClient
                    .getColaboracionesSinPago(usuarioLogueado.getNickname());

            // Obtener información adicional de cada colaboración (imagen de propuesta)
            for (DTColaboracion colab : colaboraciones) {
                PropuestaType propuesta = propuestasSoapClient.getPropuesta(colab.getTituloPropuesta());
                if (propuesta != null) {
                    // Convertir PropuestaType a DTPropuesta básico
                    DTPropuesta dtp = new DTPropuesta();
                    dtp.setTitulo(propuesta.getTitulo());
                    dtp.setImagen(propuesta.getImagen());
                    colab.setPropuesta(dtp);
                }
            }

            model.addAttribute("colaboraciones", colaboraciones);
            model.addAttribute("usuario", usuarioLogueado);

            logger.info("Se listaron {} colaboraciones sin pago para {}", colaboraciones.size(),
                    usuarioLogueado.getNickname());
            return "listarColaboracionesPago";

        } catch (Exception e) {
            logger.error("Error al listar colaboraciones sin pago", e);
            model.addAttribute("error", "Error al obtener las colaboraciones: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Muestra el formulario de pago para una colaboración específica (solo móvil)
     */
    @GetMapping("/pago/formulario")
    public String mostrarFormularioPago(
            @RequestParam("tituloPropuesta") String tituloPropuesta,
            HttpServletRequest request,
            HttpSession session,
            Model model) {

        logger.info("=== INICIO mostrarFormularioPago ===");
        logger.info("Propuesta: {}", tituloPropuesta);

        // Verificar que sea dispositivo móvil
        if (!MobileDetectionUtil.isMobileDevice(request)) {
            logger.warn("Intento de acceso desde dispositivo no móvil");
            model.addAttribute("error", "Esta funcionalidad solo está disponible desde dispositivos móviles");
            return "error";
        }

        // Verificar sesión
        DTUsuario usuarioLogueado = (DTUsuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            logger.warn("Intento de acceso sin sesión");
            return "redirect:/login";
        }

        try {
            // Obtener la colaboración via SOAP
            DTColaboracion colaboracion = colaboracionSoapClient.getDTColaboracionPropuesta(
                    usuarioLogueado.getNickname(), tituloPropuesta);

            if (colaboracion == null) {
                model.addAttribute("error", "No se encontró la colaboración");
                return "error";
            }

            // Obtener información de la propuesta via SOAP
            PropuestaType propuestaType = propuestasSoapClient.getPropuesta(tituloPropuesta);
            DTPropuesta propuesta = new DTPropuesta();
            if (propuestaType != null) {
                propuesta.setTitulo(propuestaType.getTitulo());
                propuesta.setImagen(propuestaType.getImagen());
            }

            model.addAttribute("colaboracion", colaboracion);
            model.addAttribute("propuesta", propuesta);
            model.addAttribute("usuario", usuarioLogueado);

            logger.info("Mostrando formulario de pago para colaboración de {} en propuesta {}",
                    usuarioLogueado.getNickname(), tituloPropuesta);
            return "formPago";

        } catch (Exception e) {
            logger.error("Error al mostrar formulario de pago", e);
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Procesa el pago de una colaboración (solo móvil)
     */
    @PostMapping("/pago/procesar")
    public String procesarPago(
            @RequestParam("tituloPropuesta") String tituloPropuesta,
            @RequestParam("monto") float monto,
            @RequestParam("tipoPago") String tipoPagoStr,
            @RequestParam("nombreTitular") String nombreTitular,
            // Campos opcionales según tipo de pago
            @RequestParam(value = "tipoTarjeta", required = false) String tipoTarjetaStr,
            @RequestParam(value = "numeroTarjeta", required = false) String numeroTarjeta,
            @RequestParam(value = "fechaVencimiento", required = false) String fechaVencimiento,
            @RequestParam(value = "cvc", required = false) String cvc,
            @RequestParam(value = "nombreBanco", required = false) String nombreBanco,
            @RequestParam(value = "numeroCuenta", required = false) String numeroCuenta,
            HttpServletRequest request,
            HttpSession session,
            Model model) {

        logger.info("=== INICIO procesarPago ===");
        logger.info("Propuesta: {}, Monto: {}, TipoPago: {}", tituloPropuesta, monto, tipoPagoStr);

        // Verificar que sea dispositivo móvil
        if (!MobileDetectionUtil.isMobileDevice(request)) {
            logger.warn("Intento de acceso desde dispositivo no móvil");
            model.addAttribute("error", "Esta funcionalidad solo está disponible desde dispositivos móviles");
            return "error";
        }

        // Verificar sesión
        DTUsuario usuarioLogueado = (DTUsuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            logger.warn("Intento de acceso sin sesión");
            return "redirect:/login";
        }

        try {
            // Crear DTPago
            DTPago pago = new DTPago();
            pago.setMonto(monto);
            pago.setFechaPago(LocalDate.now());
            pago.setHoraPago(LocalTime.now());
            pago.setTipoPago(TipoPago.valueOf(tipoPagoStr));
            pago.setNombreTitular(nombreTitular);

            // Setear campos específicos según tipo de pago
            if (pago.getTipoPago() == TipoPago.TARJETA) {
                pago.setTipoTarjeta(TipoTarjeta.valueOf(tipoTarjetaStr));
                pago.setNumeroTarjeta(numeroTarjeta);
                pago.setFechaVencimiento(fechaVencimiento);
                pago.setCvc(cvc);
            } else if (pago.getTipoPago() == TipoPago.TRANSFERENCIA) {
                pago.setNombreBanco(nombreBanco);
                pago.setNumeroCuenta(numeroCuenta);
            } else if (pago.getTipoPago() == TipoPago.PAYPAL) {
                pago.setNumeroCuenta(numeroCuenta);
            }

            // Registrar el pago via SOAP
            colaboracionSoapClient.registrarPago(pago, usuarioLogueado.getNickname(), tituloPropuesta);

            // Obtener información para el email via SOAP
            DTColaboracion colaboracion = colaboracionSoapClient.getDTColaboracionPropuesta(
                    usuarioLogueado.getNickname(), tituloPropuesta);

            PropuestaType propuestaType = propuestasSoapClient.getPropuesta(tituloPropuesta);
            DTPropuesta propuesta = new DTPropuesta();
            if (propuestaType != null) {
                propuesta.setTitulo(propuestaType.getTitulo());
                propuesta.setImagen(propuestaType.getImagen());
                propuesta.setProponente(propuestaType.getProponente());
            }

            ColaboradorType colaboradorType = usuarioSoapClient.getDTColaborador(usuarioLogueado.getNickname());
            DTColaborador colaborador = convertColaboradorType(colaboradorType);

            ProponenteType proponenteType = usuarioSoapClient.getProponente(propuesta.getProponente());
            DTProponente proponente = convertProponenteType(proponenteType);

            // Enviar notificaciones por email
            emailService.enviarNotificacionColaborador(colaboracion, colaborador, propuesta, proponente);
            emailService.enviarNotificacionProponente(colaboracion, colaborador, propuesta, proponente);

            model.addAttribute("colaboracion", colaboracion);
            model.addAttribute("propuesta", propuesta);
            model.addAttribute("pago", pago);

            logger.info("Pago procesado exitosamente para colaboración de {} en propuesta {}",
                    usuarioLogueado.getNickname(), tituloPropuesta);
            return "exitoPago";

        } catch (Exception e) {
            logger.error("Error al procesar pago", e);
            model.addAttribute("error", "Error al procesar el pago: " + e.getMessage());
            model.addAttribute("tituloPropuesta", tituloPropuesta);
            return "error";
        }
    }

    // Métodos auxiliares de conversión
    private DTColaborador convertColaboradorType(ColaboradorType ct) {
        if (ct == null)
            return null;
        DTColaborador dt = new DTColaborador(
                ct.getNickname(),
                ct.getPassword() != null ? ct.getPassword() : "",
                ct.getNombre(),
                ct.getApellido(),
                ct.getEmail(),
                null, // fecha nacimiento
                ct.getImagen());
        return dt;
    }

    private DTProponente convertProponenteType(ProponenteType pt) {
        if (pt == null)
            return null;
        DTProponente dt = new DTProponente(
                pt.getNickname(),
                pt.getPassword() != null ? pt.getPassword() : "",
                pt.getNombre(),
                pt.getApellido(),
                pt.getEmail(),
                null, // fecha nacimiento
                pt.getImagen(),
                pt.getDireccion(),
                pt.getLinkWeb(),
                pt.getBiografia());
        return dt;
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
                tipoRetorno);
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
