package com.culturarte.soap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = {"com.culturarte.soap", "com.culturarte.logica"})
@EntityScan(basePackages = {"com.culturarte.logica.clases"})
public class SoapServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SoapServiceApplication.class, args);
    }
}
