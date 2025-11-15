package com.culturarte.web.soap.client;

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
        String soapUrl = getSoapUrl();
        logger.info("=== INICIO obtenerCategorias ===");
        logger.info("URL SOAP: {}", soapUrl);
        logger.info("Host: {}, Port: {}, ContextPath: {}", soapHost, soapPort, soapContextPath);
        
        try {
            logger.debug("Enviando request SOAP para obtener categorías");
            long startTime = System.currentTimeMillis();
            GetCategoriasResponse response = (GetCategoriasResponse) webServiceTemplate.marshalSendAndReceive(soapUrl, request);
            long duration = System.currentTimeMillis() - startTime;
            
            logger.info("Respuesta SOAP recibida en {} ms", duration);
            
            if (response == null) {
                logger.warn("La respuesta SOAP es null");
                throw new RuntimeException("Respuesta SOAP null al obtener categorías");
            }
            
            if (response.getCategoria() == null) {
                logger.warn("La lista de categorías en la respuesta es null");
            } else {
                int cantidad = response.getCategoria().size();
                logger.info("Se obtuvieron {} categorías exitosamente", cantidad);
            }
            
            logger.debug("=== FIN obtenerCategorias (exitoso) ===");
            return response;
            
        } catch (Exception e) {
            logger.error("=== ERROR en obtenerCategorias ===", e);
            logger.error("Tipo de excepción: {}", e.getClass().getName());
            logger.error("Mensaje de error: {}", e.getMessage());
            if (e.getCause() != null) {
                logger.error("Causa: {}", e.getCause().getMessage());
                if (e.getCause().getCause() != null) {
                    logger.error("Causa raíz: {}", e.getCause().getCause().getMessage());
                }
            }
            logger.error("URL que falló: {}", soapUrl);
            logger.error("Stack trace completo:", e);
            throw new RuntimeException("Error al invocar SOAP de categorías", e);
        }
    }
}
