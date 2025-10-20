package com.culturarte.logica;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final IControlador controlador;

    public DataLoader(IControlador controlador) {
        this.controlador = controlador;
    }

    @Override
    public void run(String... args) {
        try {
            System.out.println("Agregando datos de prueba...");
            controlador.cargarDatosPrueba();
            System.out.println("✅ Datos de prueba cargados al iniciar la app");
        } catch (Exception e) {
            System.err.println("❌ Error cargando datos de prueba:");
        }
    }

}
