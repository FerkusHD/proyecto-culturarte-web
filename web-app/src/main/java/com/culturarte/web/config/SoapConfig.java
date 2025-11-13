package com.culturarte.web.config;

import com.culturarte.logica.IControlador;
import com.culturarte.web.soap.SoapControladorAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Configuration
public class SoapConfig {
    @Bean
    @Primary
    public IControlador soapControlador(SoapControladorAdapter adapter) {
        return adapter;
    }
}

