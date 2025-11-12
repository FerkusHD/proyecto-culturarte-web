package com.culturarte.web.soap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Configuración del cliente SOAP para conectarse al Servidor Central.
 * 
 * La URL del servicio se puede configurar de múltiples formas:
 * 1. Variable de entorno SOAP_SERVICE_URL
 * 2. Propiedad soap.service.url en application.properties
 * 3. Parámetro de contexto soap.service.url en web.xml
 * 4. Construcción automática desde host, port y context-path
 */
@Configuration
public class SoapClientConfig {

    @Value("${soap.service.url:}")
    private String soapServiceUrl;

    @Value("${soap.service.host:localhost}")
    private String soapServiceHost;

    @Value("${soap.service.port:8081}")
    private String soapServicePort;

    @Value("${soap.service.context-path:/soap/ws}")
    private String soapServiceContextPath;

    /**
     * Construye la URL del servicio SOAP si no está definida directamente.
     * La URL se construye como: http://{host}:{port}{context-path}
     */
    private String getSoapServiceUrl() {
        if (soapServiceUrl != null && !soapServiceUrl.isEmpty() && !soapServiceUrl.startsWith("${")) {
            return soapServiceUrl;
        }
        // Construir URL desde componentes
        return String.format("http://%s:%s%s", soapServiceHost, soapServicePort, soapServiceContextPath);
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.culturarte.soap.gen");
        return marshaller;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller) {
        WebServiceTemplate template = new WebServiceTemplate();
        template.setMarshaller(marshaller);
        template.setUnmarshaller(marshaller);
        template.setDefaultUri(getSoapServiceUrl());
        return template;
    }
}
