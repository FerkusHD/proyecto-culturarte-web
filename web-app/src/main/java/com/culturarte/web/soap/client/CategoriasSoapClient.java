package com.culturarte.web.soap;

import com.culturarte.soap.gen.GetCategoriasRequest;
import com.culturarte.soap.gen.GetCategoriasResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public class CategoriasSoapClient {

    private static final Logger logger = LoggerFactory.getLogger(CategoriasSoapClient.class);

    private final WebServiceTemplate webServiceTemplate;

    @Value("${soap.service.host:localhost}")
    private String soapHost;

    @Value("${soap.service.port:8081}")
    private String soapPort;

    @Value("${soap.service.context-path:/soap/ws}")
    private String soapContextPath;

    public CategoriasSoapClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    private String getSoapUrl() {
        return String.format("http://%s:%s%s/categorias", soapHost, soapPort, soapContextPath);
    }

    public GetCategoriasResponse obtenerCategorias(GetCategoriasRequest request) {
        try {
            logger.info("Llamando servicio SOAP de categorías en {}", getSoapUrl());
            GetCategoriasResponse response = (GetCategoriasResponse)
                    webServiceTemplate.marshalSendAndReceive(getSoapUrl(), request);
            return response;
        } catch (Exception e) {
            logger.error("Error al invocar SOAP categorías: {}", e.getMessage(), e);
            GetCategoriasResponse errorResponse = new GetCategoriasResponse();
            errorResponse.getCategoria().add("Error SOAP: " + e.getMessage());
            return errorResponse;
        }
    }
}
