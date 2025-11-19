package com.culturarte.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LoggingInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInitializer.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            // Crear directorio de logs si no existe
            String logPath = System.getProperty("user.dir") + File.separator + "logs";
            File logDir = new File(logPath);
            if (!logDir.exists()) {
                boolean created = logDir.mkdirs();
                if (created) {
                    logger.info("Directorio de logs creado: {}", logPath);
                } else {
                    logger.warn("No se pudo crear el directorio de logs: {}", logPath);
                }
            } else {
                logger.debug("Directorio de logs ya existe: {}", logPath);
            }
        } catch (Exception e) {
            logger.error("Error al inicializar directorio de logs", e);
        }
    }
}

