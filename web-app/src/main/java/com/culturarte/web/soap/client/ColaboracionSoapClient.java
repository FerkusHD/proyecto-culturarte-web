package com.culturarte.web.soapclient;

import culturarte.soap.colaboraciones.GetColaboracionRequest;
import culturarte.soap.colaboraciones.GetColaboracionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public class ColaboracionSoapClient {

    private static final Logger logger = LoggerFactory.getLogger(ColaboracionSoapClient.class);

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

    private String getSoapServiceUrl() {
        if (soapServiceUrl != null && !soapServiceUrl.isEmpty() && !soapServiceUrl.startsWith("${")) {
            return soapServiceUrl;
        }
        return String.format("http://%s:%s%s", soapServiceHost, soapServicePort, soapServiceContextPath);
    }

    public GetColaboracionResponse getColaboracion(String nickColaborador, String tituloPropuesta) {
        try {
            GetColaboracionRequest request = new GetColaboracionRequest();
            request.setNickColaborador(nickColaborador);
            request.setTituloPropuesta(tituloPropuesta);

            String endpointUrl = getSoapServiceUrl() + "/colaboraciones";

            logger.info("📡 Enviando solicitud SOAP a {}", endpointUrl);

            GetColaboracionResponse response = (GetColaboracionResponse)
                    webServiceTemplate.marshalSendAndReceive(endpointUrl, request);

            logger.info("✅ Respuesta SOAP recibida correctamente para {}", nickColaborador);

            return response;
        } catch (Exception e) {
            logger.error("❌ Error al comunicarse con el servicio SOAP de colaboraciones: {}", e.getMessage(), e);
            return null;
        }
    }
}
