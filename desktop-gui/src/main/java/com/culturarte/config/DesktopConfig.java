package com.culturarte.config;

import com.culturarte.logica.ControladorSOAP;
import com.culturarte.logica.IControlador;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DesktopConfig {

    @Bean
    public IControlador controlador() {
        return new ControladorSOAP();
    }
}
