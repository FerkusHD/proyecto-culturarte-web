package com.culturarte.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.culturarte.logica.clases"})
@ComponentScan(
    basePackages = {"com.culturarte.web", "com.culturarte.logica"},
    excludeFilters = {
        // Excluir Controlador y DataLoader del escaneo
        // web-app SIEMPRE usa SOAP, por lo que no necesita acceso directo a Controlador
        // Los datos se cargan solo en soap-service
        @ComponentScan.Filter(
            type = org.springframework.context.annotation.FilterType.REGEX,
            pattern = "com\\.culturarte\\.logica\\.Controlador"
        ),
        @ComponentScan.Filter(
            type = org.springframework.context.annotation.FilterType.REGEX,
            pattern = "com\\.culturarte\\.logica\\.DataLoader"
        )
    }
)
public class PryectoCulturarteWebApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(PryectoCulturarteWebApplication.class);
    }

	public static void main(String[] args) {
		SpringApplication.run(PryectoCulturarteWebApplication.class, args);
	}

}
