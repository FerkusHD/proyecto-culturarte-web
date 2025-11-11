package com.culturarte.web.config;

import com.culturarte.logica.IControlador;
import com.culturarte.web.soap.SoapControladorAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuración para usar el adaptador SOAP.
 * web-app SIEMPRE usa SOAP para acceder a core-business a través de soap-service.
 */
@Configuration
public class SoapConfig {

    /**
     * Bean primario que usa el adaptador SOAP.
     * El @Primary asegura que este bean tenga prioridad y sea el único IControlador disponible.
     */
    @Bean
    @Primary
    public IControlador soapControlador(SoapControladorAdapter adapter) {
        return adapter;
    }
}

