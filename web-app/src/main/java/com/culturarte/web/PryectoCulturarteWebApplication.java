package com.culturarte.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EntityScan(basePackages = {"com.culturarte.logica.clases", "com.culturarte.web.entity"})
@ComponentScan(
    basePackages = {"com.culturarte.web", "com.culturarte.logica"},
    excludeFilters = {
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
