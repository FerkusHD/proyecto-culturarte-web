package com.culturarte.soap.service;

import com.culturarte.logica.datatypes.DTColaboracion;
import com.culturarte.logica.datatypes.DTPago;
import com.culturarte.logica.datatypes.DTProponente;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@culturarte.com}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envía email de confirmación de pago al colaborador
     */
    public void enviarEmailConfirmacionColaborador(String emailColaborador, String nombreColaborador,
            DTColaboracion colaboracion, DTPago pago) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(emailColaborador);
            helper.setSubject("✅ Confirmación de Pago - " + colaboracion.getTituloPropuesta());

            String htmlContent = construirEmailColaborador(nombreColaborador, colaboracion, pago);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Email de confirmación enviado a colaborador: {}", emailColaborador);

        } catch (MessagingException e) {
            logger.error("Error al enviar email a colaborador {}: {}", emailColaborador, e.getMessage(), e);
        }
    }

    /**
     * Envía email de notificación al proponente
     */
    public void enviarEmailNotificacionProponente(String emailProponente, String nombreProponente,
            String nombreColaborador, DTColaboracion colaboracion,
            DTPago pago) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(emailProponente);
            helper.setSubject("💰 Nuevo Pago Recibido - " + colaboracion.getTituloPropuesta());

            String htmlContent = construirEmailProponente(nombreProponente, nombreColaborador, colaboracion, pago);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            logger.info("Email de notificación enviado a proponente: {}", emailProponente);

        } catch (MessagingException e) {
            logger.error("Error al enviar email a proponente {}: {}", emailProponente, e.getMessage(), e);
        }
    }

    private String construirEmailColaborador(String nombreColaborador, DTColaboracion colaboracion, DTPago pago) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String constanciaUrl = baseUrl + "/colaboraciones/constancia-pago?nickColaborador=" +
                colaboracion.getNickColaborador() + "&tituloPropuesta=" + colaboracion.getTituloPropuesta();

        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Confirmación de Pago</title>
                </head>
                <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
                        <tr>
                            <td align="center">
                                <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                    <!-- Header -->
                                    <tr>
                                        <td style="background: linear-gradient(135deg, #28a745 0%%, #20c997 100%%); padding: 40px 20px; text-align: center;">
                                            <h1 style="color: #ffffff; margin: 0; font-size: 28px;">✅ Pago Confirmado</h1>
                                            <p style="color: #ffffff; margin: 10px 0 0 0; font-size: 16px;">Tu pago ha sido procesado exitosamente</p>
                                        </td>
                                    </tr>

                                    <!-- Body -->
                                    <tr>
                                        <td style="padding: 40px 30px;">
                                            <p style="margin: 0 0 20px 0; font-size: 16px; color: #333;">Hola <strong>%s</strong>,</p>

                                            <p style="margin: 0 0 20px 0; font-size: 16px; color: #333;">
                                                Confirmamos que hemos recibido tu pago para la propuesta <strong>"%s"</strong>.
                                            </p>

                                            <!-- Payment Details -->
                                            <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f8f9fa; border-radius: 6px; padding: 20px; margin: 20px 0;">
                                                <tr>
                                                    <td style="padding: 10px 0; border-bottom: 1px solid #dee2e6;">
                                                        <span style="color: #6c757d; font-size: 14px;">Fecha de Pago:</span>
                                                        <br>
                                                        <strong style="color: #333; font-size: 16px;">%s</strong>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 10px 0; border-bottom: 1px solid #dee2e6;">
                                                        <span style="color: #6c757d; font-size: 14px;">Hora:</span>
                                                        <br>
                                                        <strong style="color: #333; font-size: 16px;">%s</strong>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 10px 0; border-bottom: 1px solid #dee2e6;">
                                                        <span style="color: #6c757d; font-size: 14px;">Método de Pago:</span>
                                                        <br>
                                                        <strong style="color: #333; font-size: 16px;">%s</strong>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 10px 0;">
                                                        <span style="color: #6c757d; font-size: 14px;">Monto Pagado:</span>
                                                        <br>
                                                        <strong style="color: #28a745; font-size: 24px;">$%.2f</strong>
                                                    </td>
                                                </tr>
                                            </table>

                                            <!-- Constancia Button -->
                                            <div style="text-align: center; margin: 30px 0;">
                                                <a href="%s" style="display: inline-block; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: #ffffff; text-decoration: none; padding: 15px 40px; border-radius: 6px; font-size: 16px; font-weight: bold;">
                                                    📄 Descargar Constancia de Pago
                                                </a>
                                            </div>

                                            <p style="margin: 20px 0 0 0; font-size: 14px; color: #6c757d;">
                                                Gracias por tu colaboración. Tu apoyo es fundamental para hacer realidad esta propuesta cultural.
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #dee2e6;">
                                            <p style="margin: 0; font-size: 12px; color: #6c757d;">
                                                Este es un correo automático, por favor no respondas a este mensaje.
                                            </p>
                                            <p style="margin: 10px 0 0 0; font-size: 12px; color: #6c757d;">
                                                © 2025 Culturarte. Todos los derechos reservados.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """
                .formatted(
                        nombreColaborador,
                        colaboracion.getTituloPropuesta(),
                        pago.getFechaPago().format(dateFormatter),
                        pago.getHoraPago().format(timeFormatter),
                        pago.getTipoPago().toString(),
                        pago.getMonto(),
                        constanciaUrl);
    }

    private String construirEmailProponente(String nombreProponente, String nombreColaborador,
            DTColaboracion colaboracion, DTPago pago) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Nuevo Pago Recibido</title>
                </head>
                <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
                        <tr>
                            <td align="center">
                                <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                                    <!-- Header -->
                                    <tr>
                                        <td style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 40px 20px; text-align: center;">
                                            <h1 style="color: #ffffff; margin: 0; font-size: 28px;">💰 Nuevo Pago Recibido</h1>
                                            <p style="color: #ffffff; margin: 10px 0 0 0; font-size: 16px;">Tu propuesta ha recibido un nuevo aporte</p>
                                        </td>
                                    </tr>

                                    <!-- Body -->
                                    <tr>
                                        <td style="padding: 40px 30px;">
                                            <p style="margin: 0 0 20px 0; font-size: 16px; color: #333;">Hola <strong>%s</strong>,</p>

                                            <p style="margin: 0 0 20px 0; font-size: 16px; color: #333;">
                                                ¡Buenas noticias! <strong>%s</strong> ha confirmado el pago de su colaboración para tu propuesta <strong>"%s"</strong>.
                                            </p>

                                            <!-- Payment Details -->
                                            <table width="100%%" cellpadding="0" cellspacing="0" style="background-color: #f8f9fa; border-radius: 6px; padding: 20px; margin: 20px 0;">
                                                <tr>
                                                    <td style="padding: 10px 0; border-bottom: 1px solid #dee2e6;">
                                                        <span style="color: #6c757d; font-size: 14px;">Colaborador:</span>
                                                        <br>
                                                        <strong style="color: #333; font-size: 16px;">%s</strong>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 10px 0; border-bottom: 1px solid #dee2e6;">
                                                        <span style="color: #6c757d; font-size: 14px;">Fecha de Pago:</span>
                                                        <br>
                                                        <strong style="color: #333; font-size: 16px;">%s</strong>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 10px 0; border-bottom: 1px solid #dee2e6;">
                                                        <span style="color: #6c757d; font-size: 14px;">Hora:</span>
                                                        <br>
                                                        <strong style="color: #333; font-size: 16px;">%s</strong>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding: 10px 0;">
                                                        <span style="color: #6c757d; font-size: 14px;">Monto Recibido:</span>
                                                        <br>
                                                        <strong style="color: #28a745; font-size: 24px;">$%.2f</strong>
                                                    </td>
                                                </tr>
                                            </table>

                                            <p style="margin: 20px 0 0 0; font-size: 14px; color: #6c757d;">
                                                Este pago acerca tu propuesta a su objetivo de financiación. ¡Sigue adelante!
                                            </p>
                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #dee2e6;">
                                            <p style="margin: 0; font-size: 12px; color: #6c757d;">
                                                Este es un correo automático, por favor no respondas a este mensaje.
                                            </p>
                                            <p style="margin: 10px 0 0 0; font-size: 12px; color: #6c757d;">
                                                © 2025 Culturarte. Todos los derechos reservados.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """
                .formatted(
                        nombreProponente,
                        nombreColaborador,
                        colaboracion.getTituloPropuesta(),
                        nombreColaborador,
                        pago.getFechaPago().format(dateFormatter),
                        pago.getHoraPago().format(timeFormatter),
                        pago.getMonto());
    }
}
