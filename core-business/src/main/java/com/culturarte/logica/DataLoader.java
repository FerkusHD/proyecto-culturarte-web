package com.culturarte.logica;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final IControlador controlador;

    public DataLoader(IControlador controlador) {
        logger.info("🔧 DataLoader inicializado");
        this.controlador = controlador;
    }

    @Override
    public void run(String... args) {
        logger.info("=== INICIO carga de datos de prueba ===");
        try {
            logger.info("📦 Cargando datos de prueba...");
            controlador.cargarDatosPrueba();
            logger.info("✅ Datos de prueba cargados exitosamente");
            System.out.println("✅ Datos de prueba cargados al iniciar la app");
        } catch (Exception e) {
            logger.error("❌ Error cargando datos de prueba: {}", e.getMessage(), e);
            System.err.println("❌ Error cargando datos de prueba: " + e.getMessage());
            e.printStackTrace();
        }
        logger.info("=== FIN carga de datos de prueba ===");
    }

}