package com.culturarte;

import com.culturarte.logica.IControlador;
import com.culturarte.presentacion.MenuPrincipal;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;

@SpringBootApplication(scanBasePackages = {"com.culturarte.presentacion", "com.culturarte.logica"})
@EntityScan(basePackages = {"com.culturarte.logica.clases"})
public class DesktopGuiApplication {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        ConfigurableApplicationContext context = SpringApplication.run(DesktopGuiApplication.class, args);
        IControlador ctrl = context.getBean(IControlador.class);

        if (!GraphicsEnvironment.isHeadless()) {
            SwingUtilities.invokeLater(() -> new MenuPrincipal(ctrl).setVisible(true));
        }
    }
}
