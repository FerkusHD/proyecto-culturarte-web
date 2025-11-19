package com.culturarte.web.soap;

import jakarta.xml.bind.Marshaller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.HashMap;
import java.util.Map;


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

        // 1. Configura el paquete donde se generaron tus clases SOAP
        marshaller.setContextPath("com.culturarte.soap.gen");

        // 2. Define las propiedades para el Marshaller
        Map<String, Object> properties = new HashMap<>();

        // **ESTO ES CLAVE:** Indica a JAXB que serialice el objeto sin el prólogo XML,
        // lo que a menudo corrige problemas de manejo de tipos genéricos (Object).
        properties.put(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

        marshaller.setMarshallerProperties(properties);

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
