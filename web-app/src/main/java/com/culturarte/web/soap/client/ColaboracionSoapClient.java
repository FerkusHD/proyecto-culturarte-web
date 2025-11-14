package com.culturarte.web.soap.client;

import com.culturarte.soap.gen.GetColaboracionRequest;
import com.culturarte.soap.gen.GetColaboracionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public class ColaboracionSoapClient {

    private static final Logger logger = LoggerFactory.getLogger(ColaboracionSoapClient.class);

    private final WebServiceTemplate webServiceTemplate;

    @Value("${soap.service.url:}")
    private String soapServiceUrl;

    @Value("${soap.service.host:localhost}")
    private String soapServiceHost;

    @Value("${soap.service.port:8081}")
    private String soapServicePort;

    @Value("${soap.service.context-path:/soap/ws}")
    private String soapServiceContextPath;

    public ColaboracionSoapClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    private String getSoapServiceUrl() {
        if (soapServiceUrl != null && !soapServiceUrl.isEmpty() && !soapServiceUrl.startsWith("${")) {
            return soapServiceUrl;
        }
        return String.format("http://%s:%s%s/colaboraciones", soapServiceHost, soapServicePort, soapServiceContextPath);
    }

    public GetColaboracionResponse getColaboracion(String nickColaborador, String tituloPropuesta) {
        try {
            GetColaboracionRequest request = new GetColaboracionRequest();
            request.setNickColaborador(nickColaborador);
            request.setTituloPropuesta(tituloPropuesta);

            logger.info("📡 Enviando solicitud SOAP a {}", getSoapServiceUrl());
            return (GetColaboracionResponse) webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), request);
        } catch (Exception e) {
            logger.error("❌ Error al comunicarse con el servicio SOAP de colaboraciones: {}", e.getMessage(), e);
            return null;
        }
    }
}
