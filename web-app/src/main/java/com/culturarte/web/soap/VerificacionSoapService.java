package com.culturarte.web.soap;

import com.culturarte.soap.gen.VerificarEmailRequest;
import com.culturarte.soap.gen.VerificarEmailResponse;
import com.culturarte.soap.gen.VerificarNicknameRequest;
import com.culturarte.soap.gen.VerificarNicknameResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Servicio para verificar disponibilidad de nickname y email usando SOAP.
 * 
 * Este servicio llama directamente a los endpoints SOAP del Servidor Central
 * para verificar disponibilidad, cumpliendo con el requisito de que todo
 * funcione con SOAP.
 */
@Service
public class VerificacionSoapService {

    private static final Logger logger = LoggerFactory.getLogger(VerificacionSoapService.class);

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Value("${soap.service.url:}")
    private String soapServiceUrl;

    @Value("${soap.service.host:localhost}")
    private String soapServiceHost;

    @Value("${soap.service.port:8081}")
    private String soapServicePort;

    @Value("${soap.service.context-path:/soap/ws}")
    private String soapServiceContextPath;

    /**
     * Obtiene la URL base del servicio SOAP.
     */
    private String getSoapServiceUrl() {
        if (soapServiceUrl != null && !soapServiceUrl.isEmpty() && !soapServiceUrl.startsWith("${")) {
            return soapServiceUrl;
        }
        return String.format("http://%s:%s%s", soapServiceHost, soapServicePort, soapServiceContextPath);
    }

    /**
     * Verifica disponibilidad de nickname usando SOAP.
     * 
     * @param nickname Nickname a verificar
     * @return VerificarNicknameResponse con el resultado
     */
    public VerificarNicknameResponse verificarNickname(String nickname) {
        try {
            VerificarNicknameRequest request = new VerificarNicknameRequest();
            request.setNickname(nickname);

            // Pasar el request directamente (similar a GetUsuarioRequest y GetPropuestaRequest)
            VerificarNicknameResponse response = (VerificarNicknameResponse) 
                webServiceTemplate.marshalSendAndReceive(
                    getSoapServiceUrl() + "/usuarios", 
                    request
                );

            return response != null ? response : crearRespuestaErrorNickname();
        } catch (Exception e) {
            logger.error("Error al verificar nickname vía SOAP: {}", e.getMessage(), e);
            return crearRespuestaErrorNickname();
        }
    }

    /**
     * Verifica disponibilidad de email usando SOAP.
     * 
     * @param email Email a verificar
     * @return VerificarEmailResponse con el resultado
     */
    public VerificarEmailResponse verificarEmail(String email) {
        try {
            VerificarEmailRequest request = new VerificarEmailRequest();
            request.setEmail(email);

            // Pasar el request directamente (similar a GetUsuarioRequest y GetPropuestaRequest)
            VerificarEmailResponse response = (VerificarEmailResponse) 
                webServiceTemplate.marshalSendAndReceive(
                    getSoapServiceUrl() + "/usuarios", 
                    request
                );

            return response != null ? response : crearRespuestaErrorEmail();
        } catch (Exception e) {
            logger.error("Error al verificar email vía SOAP: {}", e.getMessage(), e);
            return crearRespuestaErrorEmail();
        }
    }

    private VerificarNicknameResponse crearRespuestaErrorNickname() {
        VerificarNicknameResponse response = new VerificarNicknameResponse();
        response.setDisponible(false);
        response.setMensaje("Error al verificar disponibilidad");
        return response;
    }

    private VerificarEmailResponse crearRespuestaErrorEmail() {
        VerificarEmailResponse response = new VerificarEmailResponse();
        response.setDisponible(false);
        response.setMensaje("Error al verificar disponibilidad");
        return response;
    }
}

