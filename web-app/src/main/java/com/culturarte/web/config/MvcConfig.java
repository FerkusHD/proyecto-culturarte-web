package com.culturarte.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${app.uploads.dir:uploads}")
    private String uploadsDir;

    // Resolver de vistas JSP
    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    // Recursos estáticos (para mostrar imágenes subidas)
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Normaliza el path y asegura barra final
        String dir = uploadsDir != null ? uploadsDir.trim() : "uploads";
        if (!StringUtils.hasText(dir)) {
            dir = "uploads";
        }
        boolean usingDefault = "uploads".equals(dir);
        String catalinaBase = System.getProperty("catalina.base");
        if (usingDefault && StringUtils.hasText(catalinaBase)) {
            dir = Paths.get(catalinaBase, "uploads").toString();
        }
        Path path = Paths.get(dir).toAbsolutePath().normalize();
        String location = path.toUri().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location, "classpath:/static/uploads/");
    }
}
