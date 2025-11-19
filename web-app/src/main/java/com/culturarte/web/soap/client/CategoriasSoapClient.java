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

    @Value("${soap.service.url:}")
    private String soapServiceUrl;

    @Value("${soap.service.host:localhost}")
    private String soapServiceHost;

    @Value("${soap.service.port:8081}")
    private String soapServicePort;

    @Value("${soap.service.context-path:/soap/ws}")
    private String soapServiceContextPath;

    public CategoriasSoapClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    private String getSoapUrl() {
        if (soapServiceUrl != null && !soapServiceUrl.isEmpty() && !soapServiceUrl.startsWith("${")) {
            logger.debug("Usando URL SOAP configurada directamente: {}", soapServiceUrl);
            return soapServiceUrl + "/categorias";
        }
        String endpoint = String.format("http://%s:%s%s/categorias", soapServiceHost, soapServicePort, soapServiceContextPath);
        logger.debug("Construyendo endpoint SOAP: {}", endpoint);
        return endpoint;
    }

    public GetCategoriasResponse obtenerCategorias(GetCategoriasRequest request) {
        String soapUrl = getSoapUrl();
        logger.info("=== INICIO obtenerCategorias ===");
        logger.info("URL SOAP: {}", soapUrl);
        logger.info("Host: {}, Port: {}, ContextPath: {}", soapServiceHost, soapServicePort, soapServiceContextPath);
        
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
            
            // Si es un error de conexión, lanzar una excepción más específica
            if (e.getCause() instanceof java.net.ConnectException) {
                logger.error("⚠️ El servicio SOAP no está disponible en: {}", soapUrl);
                logger.error("Por favor, verifica que el servicio SOAP esté corriendo y accesible");
            }
            
            throw new RuntimeException("Error al invocar SOAP de categorías", e);
        }
    }
}
