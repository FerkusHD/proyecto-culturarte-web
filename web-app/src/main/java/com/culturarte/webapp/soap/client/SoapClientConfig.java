package com.culturarte.webapp.soap.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

@Configuration
public class SoapClientConfig {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // Este paquete debe coincidir con el paquete donde se generan las clases JAXB en el soap-service
        marshaller.setPackagesToScan("com.culturarte.ws");
        return marshaller;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller) {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(marshaller);
        webServiceTemplate.setUnmarshaller(marshaller);
        // La URL del servicio SOAP - ajusta según tu configuración
        webServiceTemplate.setDefaultUri("http://localhost:8080/ws");
        return webServiceTemplate;
    }
}