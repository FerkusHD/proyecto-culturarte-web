package com.culturarte.logica;

import com.culturarte.logica.IControlador;
import com.culturarte.logica.IControlador;
import com.culturarte.presentacion.MenuPrincipal;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import javax.swing.SwingUtilities;
import java.awt.*;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AppApplication {


    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        ConfigurableApplicationContext context = SpringApplication.run(AppApplication.class, args);
        IControlador ctrl = context.getBean(IControlador.class); // <- Aquí Spring busca el bean

        if (!GraphicsEnvironment.isHeadless()) {
            SwingUtilities.invokeLater(() -> {
                new MenuPrincipal(ctrl).setVisible(true);
            });
        }
    }
    
}

