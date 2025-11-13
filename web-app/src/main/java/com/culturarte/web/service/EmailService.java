package com.culturarte.web.service;

import com.culturarte.logica.datatypes.DTColaboracion;
import com.culturarte.logica.datatypes.DTColaborador;
import com.culturarte.logica.datatypes.DTProponente;
import com.culturarte.logica.datatypes.DTPropuesta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.format.DateTimeFormatter;


@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@culturarte.com}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * Envía notificación de pago de colaboración al colaborador.
     * 
     * @param colaboracion Información de la colaboración
     * @param colaborador Información del colaborador
     * @param propuesta Información de la propuesta
     * @param proponente Información del proponente
     */
    @Async
    public void enviarNotificacionColaborador(DTColaboracion colaboracion,
                                             DTColaborador colaborador,
                                             DTPropuesta propuesta,
                                             DTProponente proponente) {
        try {
            String to = colaborador.getEmail();
            String subject = generarAsunto(colaboracion);
            String body = generarCuerpoEmailColaborador(colaboracion, colaborador, propuesta, proponente);

            enviarEmail(to, subject, body);
        } catch (Exception e) {
            // Log del error pero no lanzar excepción para no afectar el flujo principal
            logger.error("Error al enviar email al colaborador: {}", e.getMessage(), e);
        }
    }

    /**
     * Envía notificación de pago de colaboración al proponente.
     * 
     * @param colaboracion Información de la colaboración
     * @param colaborador Información del colaborador
     * @param propuesta Información de la propuesta
     * @param proponente Información del proponente
     */
    @Async
    public void enviarNotificacionProponente(DTColaboracion colaboracion,
                                             DTColaborador colaborador,
                                             DTPropuesta propuesta,
                                             DTProponente proponente) {
        try {
            String to = proponente.getEmail();
            String subject = generarAsunto(colaboracion);
            String body = generarCuerpoEmailProponente(colaboracion, colaborador, propuesta, proponente);

            enviarEmail(to, subject, body);
        } catch (Exception e) {
            // Log del error pero no lanzar excepción para no afectar el flujo principal
            logger.error("Error al enviar email al proponente: {}", e.getMessage(), e);
        }
    }

    /**
     * Genera el asunto del email según el formato especificado.
     */
    private String generarAsunto(DTColaboracion colaboracion) {
        String fechaHora = "";
        if (colaboracion.getFecha() != null) {
            fechaHora = colaboracion.getFecha().format(DATE_FORMATTER);
            if (colaboracion.getHora() != null) {
                fechaHora += " " + colaboracion.getHora().format(TIME_FORMATTER);
            }
        } else {
            fechaHora = java.time.LocalDate.now().format(DATE_FORMATTER) + " " +
                       java.time.LocalTime.now().format(TIME_FORMATTER);
        }
        return "[Culturarte] [" + fechaHora + "] Pago de colaboración registrado";
    }

    /**
     * Genera el cuerpo del email para el colaborador (incluye link a constancia de pago).
     */
    private String generarCuerpoEmailColaborador(DTColaboracion colaboracion,
                                                DTColaborador colaborador,
                                                DTPropuesta propuesta,
                                                DTProponente proponente) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        html.append(".header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }");
        html.append(".content { padding: 20px; background-color: #f9f9f9; }");
        html.append(".details { background-color: white; padding: 15px; margin: 15px 0; border-left: 4px solid #4CAF50; }");
        html.append(".detail-item { margin: 10px 0; }");
        html.append(".detail-label { font-weight: bold; }");
        html.append(".link-button { display: inline-block; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; margin: 15px 0; }");
        html.append(".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");
        html.append("<div class='header'>");
        html.append("<h1>Culturarte</h1>");
        html.append("</div>");
        html.append("<div class='content'>");
        html.append("<p>Estimado <strong>Colaborador</strong>.</p>");
        html.append("<p>El pago correspondiente a la colaboración de la propuesta <strong>");
        html.append(escapeHtml(propuesta.getTitulo()));
        html.append("</strong> realizada por <strong>");
        html.append(escapeHtml(colaborador.getNombre() + " " + colaborador.getApellido()));
        html.append("</strong> ha sido registrado en forma exitosa.</p>");
        
        html.append("<div class='details'>");
        html.append("<h3>---Detalles de la Colaboración</h3>");
        html.append("<div class='detail-item'>");
        html.append("<span class='detail-label'>-Propuesta:</span><br>");
        html.append("&nbsp;&nbsp;- ").append(escapeHtml(propuesta.getTitulo()));
        html.append("</div>");
        html.append("<div class='detail-item'>");
        html.append("<span class='detail-label'>-Proponente:</span><br>");
        html.append("&nbsp;&nbsp;- ").append(escapeHtml(proponente.getNombre() + " " + proponente.getApellido()));
        html.append("</div>");
        html.append("<div class='detail-item'>");
        html.append("<span class='detail-label'>-Colaborador:</span><br>");
        html.append("&nbsp;&nbsp;- ").append(escapeHtml(colaborador.getNombre() + " " + colaborador.getApellido()));
        html.append("</div>");
        html.append("<div class='detail-item'>");
        html.append("<span class='detail-label'>-Monto:</span><br>");
        html.append("&nbsp;&nbsp;- $ ").append(String.format("%.2f", colaboracion.getMonto()));
        html.append("</div>");
        html.append("<div class='detail-item'>");
        html.append("<span class='detail-label'>-Fecha de pago:</span><br>");
        String fechaHora = "";
        if (colaboracion.getFecha() != null) {
            fechaHora = colaboracion.getFecha().format(DATE_FORMATTER);
            if (colaboracion.getHora() != null) {
                fechaHora += " " + colaboracion.getHora().format(TIME_FORMATTER);
            }
        }
        html.append("&nbsp;&nbsp;- ").append(fechaHora);
        html.append("</div>");
        html.append("</div>");
        
        // Link para solicitar constancia de pago (solo para colaborador)
        String constanciaUrl = baseUrl + "/colaboraciones/constancia-pago?nickColaborador=" +
                              colaborador.getNickname() + "&tituloPropuesta=" +
                              java.net.URLEncoder.encode(propuesta.getTitulo(), java.nio.charset.StandardCharsets.UTF_8);
        html.append("<p>Puede solicitar la constancia de pago correspondiente haciendo clic en el siguiente enlace:</p>");
        html.append("<a href='").append(constanciaUrl).append("' class='link-button'>Solicitar Constancia de Pago</a>");
        
        html.append("<p>Gracias por preferirnos,<br>Saludos.<br><strong>Culturarte.</strong></p>");
        html.append("</div>");
        html.append("<div class='footer'>");
        html.append("<p>Este es un mensaje automático, por favor no responda a este correo.</p>");
        html.append("</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }

    /**
     * Genera el cuerpo del email para el proponente (sin link a constancia).
     */
    private String generarCuerpoEmailProponente(DTColaboracion colaboracion,
                                               DTColaborador colaborador,
                                               DTPropuesta propuesta,
                                               DTProponente proponente) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        html.append(".header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }");
        html.append(".content { padding: 20px; background-color: #f9f9f9; }");
        html.append(".details { background-color: white; padding: 15px; margin: 15px 0; border-left: 4px solid #4CAF50; }");
        html.append(".detail-item { margin: 10px 0; }");
        html.append(".detail-label { font-weight: bold; }");
        html.append(".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");
        html.append("<div class='header'>");
        html.append("<h1>Culturarte</h1>");
        html.append("</div>");
        html.append("<div class='content'>");
        html.append("<p>Estimado <strong>Proponente</strong>.</p>");
        html.append("<p>El pago correspondiente a la colaboración de la propuesta <strong>");
        html.append(escapeHtml(propuesta.getTitulo()));
        html.append("</strong> realizada por <strong>");
        html.append(escapeHtml(colaborador.getNombre() + " " + colaborador.getApellido()));
        html.append("</strong> ha sido registrado en forma exitosa.</p>");
        
        html.append("<div class='details'>");
        html.append("<h3>---Detalles de la Colaboración</h3>");
        html.append("<div class='detail-item'>");
        html.append("<span class='detail-label'>-Propuesta:</span><br>");
        html.append("&nbsp;&nbsp;- ").append(escapeHtml(propuesta.getTitulo()));
        html.append("</div>");
        html.append("<div class='detail-item'>");
        html.append("<span class='detail-label'>-Proponente:</span><br>");
        html.append("&nbsp;&nbsp;- ").append(escapeHtml(proponente.getNombre() + " " + proponente.getApellido()));
        html.append("</div>");
        html.append("<div class='detail-item'>");
        html.append("<span class='detail-label'>-Colaborador:</span><br>");
        html.append("&nbsp;&nbsp;- ").append(escapeHtml(colaborador.getNombre() + " " + colaborador.getApellido()));
        html.append("</div>");
        html.append("<div class='detail-item'>");
        html.append("<span class='detail-label'>-Monto:</span><br>");
        html.append("&nbsp;&nbsp;- $ ").append(String.format("%.2f", colaboracion.getMonto()));
        html.append("</div>");
        html.append("<div class='detail-item'>");
        html.append("<span class='detail-label'>-Fecha de pago:</span><br>");
        String fechaHora = "";
        if (colaboracion.getFecha() != null) {
            fechaHora = colaboracion.getFecha().format(DATE_FORMATTER);
            if (colaboracion.getHora() != null) {
                fechaHora += " " + colaboracion.getHora().format(TIME_FORMATTER);
            }
        }
        html.append("&nbsp;&nbsp;- ").append(fechaHora);
        html.append("</div>");
        html.append("</div>");
        
        html.append("<p>Gracias por preferirnos,<br>Saludos.<br><strong>Culturarte.</strong></p>");
        html.append("</div>");
        html.append("<div class='footer'>");
        html.append("<p>Este es un mensaje automático, por favor no responda a este correo.</p>");
        html.append("</div>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");
        
        return html.toString();
    }

    /**
     * Envía un email HTML.
     */
    private void enviarEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true indica que es HTML

        mailSender.send(message);
    }

    /**
     * Escapa caracteres HTML para prevenir XSS.
     */
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}

