package com.culturarte.web.config;

import com.culturarte.logica.IControlador;
import com.culturarte.web.soap.SoapControladorAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuración para usar el adaptador SOAP cuando la propiedad use.soap esté habilitada.
 * Esto permite cambiar entre uso directo de Controlador y uso vía SOAP.
 */
@Configuration
public class SoapConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "use.soap", havingValue = "true", matchIfMissing = false)
    public IControlador soapControlador(SoapControladorAdapter adapter) {
        return adapter;
    }
}

