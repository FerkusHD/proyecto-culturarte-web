package com.culturarte.soap.config;

import org.springframework.context.annotation.Configuration;
import com.culturarte.logica.Controlador;
import com.culturarte.logica.manejadores.*;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Configuration
@Import({PersistenceConfig.class})
public class ControladorConfig {
    // La configuración se mantiene vacía ya que Spring Boot manejará la inyección automáticamente
}