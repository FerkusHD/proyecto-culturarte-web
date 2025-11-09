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
            System.out.println("🔍 Verificando si los datos de prueba ya están cargados...");
            boolean datosYaCargados = false;

            try {
                controlador.buscarUsuarios("hrubino");
                datosYaCargados = true;
            } catch (Exception e) {
                datosYaCargados = false;
            }

            if (!datosYaCargados) {
                System.out.println("📦 Cargando datos de prueba...");
                controlador.cargarDatosPrueba();
                System.out.println("✅ Datos de prueba cargados al iniciar la app");
            } else {
                System.out.println("⚠️ Los datos de prueba ya estaban cargados. No se vuelven a insertar.");
            }

        } catch (Exception e) {
            System.err.println("❌ Error cargando datos de prueba: " + e.getMessage());
        }
    }
}
