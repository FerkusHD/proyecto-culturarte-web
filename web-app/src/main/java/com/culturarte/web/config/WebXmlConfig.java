package com.culturarte.web.config;

import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class WebXmlConfig {

    @Autowired
    private ServletContext servletContext;

    @PostConstruct
    public void init() {
        // Leer parámetros de web.xml y establecerlos como propiedades del sistema
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

