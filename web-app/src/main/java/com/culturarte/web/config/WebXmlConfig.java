package com.culturarte.web.config;

import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Configuración que lee los parámetros de contexto definidos en web.xml
 * y los expone como propiedades del sistema para que Spring pueda acceder a ellos.
 * 
 * Esto permite que la configuración del Servidor Central (SOAP) esté
 * definida en el Web Application Deployment Descriptor (web.xml) como
 * se requiere en el punto 7.7 de los requisitos.
 */
@Configuration
public class WebXmlConfig {

    @Autowired
    private ServletContext servletContext;

    /**
     * Lee los parámetros de contexto de web.xml y los establece como
     * propiedades del sistema para que Spring pueda acceder a ellos.
     */
    @PostConstruct
    public void init() {
        // Leer parámetros de web.xml y establecerlos como propiedades del sistema
        // si no están ya definidos como variables de entorno
        String host = servletContext.getInitParameter("soap.service.host");
        if (host != null && !host.isEmpty() && System.getProperty("soap.service.host") == null) {
            System.setProperty("soap.service.host", host);
        }

        String port = servletContext.getInitParameter("soap.service.port");
        if (port != null && !port.isEmpty() && System.getProperty("soap.service.port") == null) {
            System.setProperty("soap.service.port", port);
        }

        String contextPath = servletContext.getInitParameter("soap.service.context-path");
        if (contextPath != null && !contextPath.isEmpty() && System.getProperty("soap.service.context-path") == null) {
            System.setProperty("soap.service.context-path", contextPath);
        }

        String url = servletContext.getInitParameter("soap.service.url");
        if (url != null && !url.isEmpty() && System.getProperty("soap.service.url") == null) {
            System.setProperty("soap.service.url", url);
        }
    }
}

