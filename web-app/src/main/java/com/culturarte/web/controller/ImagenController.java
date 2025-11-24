package com.culturarte.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;

@Controller
public class ImagenController {

    @Value("${soap.service.url}")
    private String soapServiceUrl;

    @GetMapping("/uploads/imagenes/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            String baseUrl = soapServiceUrl;
            // Eliminar /ws al final si existe, pero MANTENER el context path (/soap)
            if (baseUrl.endsWith("/ws")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 3);
            } else if (baseUrl.endsWith("/ws/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 4);
            }

            String remoteUrl = baseUrl + "/uploads/imagenes/" + filename;

            Resource file = new UrlResource(remoteUrl);

            if (file.exists() || file.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(file);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
