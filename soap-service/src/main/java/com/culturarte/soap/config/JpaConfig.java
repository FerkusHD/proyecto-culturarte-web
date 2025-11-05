package com.culturarte.soap.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "com.culturarte.logica.clases")
@EnableJpaRepositories(basePackages = "com.culturarte.logica.manejadores")
public class JpaConfig {
    // La configuración automática de Spring Boot se encargará del resto
}