package com.culturarte.soap.config;

import com.culturarte.exepciones.CargaFallida;
import com.culturarte.logica.IControlador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final IControlador controlador;
    private final Environment environment;

    public DataLoader(IControlador controlador, Environment environment) {
        this.controlador = controlador;
        this.environment = environment;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verificar si se debe cargar datos (por defecto sí, a menos que se deshabilite)
        String cargarDatos = environment.getProperty("app.cargar.datos.prueba", "true");
        if (!Boolean.parseBoolean(cargarDatos)) {
            logger.info("Carga de datos de prueba deshabilitada por configuración");
            return;
        }

        logger.info("=== INICIO carga de datos de prueba ===");
        
        // Verificar si ya hay datos cargados
        try {
            var usuarios = controlador.listarUsuarios();
            if (usuarios != null && !usuarios.isEmpty()) {
                logger.info("Ya existen {} usuarios en la base de datos. Saltando carga de datos de prueba.", usuarios.size());
                logger.info("=== FIN carga de datos de prueba (saltada) ===");
                return;
            }
        } catch (Exception e) {
            logger.debug("No se pudo verificar usuarios existentes (esto es normal en la primera ejecución), procediendo con la carga: {}", e.getMessage());
        }

        try {
            controlador.cargarDatosPrueba();
            logger.info("✅ Datos de prueba cargados exitosamente");
        } catch (CargaFallida e) {
            logger.error("❌ Error al cargar datos de prueba: {}", e.getMessage(), e);
            // No lanzamos la excepción para que la aplicación pueda iniciar aunque falle la carga
        } catch (Exception e) {
            logger.error("❌ Error inesperado al cargar datos de prueba: {}", e.getMessage(), e);
            logger.error("Stack trace completo:", e);
        }
        logger.info("=== FIN carga de datos de prueba ===");
    }
}

