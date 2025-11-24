package com.culturarte.soap.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ImagenController {

    @GetMapping("/uploads/imagenes/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            // Intentar primero desde ~/.Culturarte/uploads/imagenes/ (imágenes nuevas)
            String userHome = System.getProperty("user.home");
            Path userImagePath = Paths.get(userHome, ".Culturarte", "uploads", "imagenes", filename);

            if (Files.exists(userImagePath) && Files.isReadable(userImagePath)) {
                Resource file = new UrlResource(userImagePath.toUri());
                return ResponseEntity.ok()
                        .contentType(getMediaType(filename))
                        .body(file);
            }

            // Si no existe, intentar desde el directorio del proyecto (imágenes antiguas)
            Path projectImagePath = Paths.get("uploads", "imagenes", filename);

            if (Files.exists(projectImagePath) && Files.isReadable(projectImagePath)) {
                Resource file = new UrlResource(projectImagePath.toUri());
                return ResponseEntity.ok()
                        .contentType(getMediaType(filename))
                        .body(file);
            }

            // Si no se encuentra en ninguna ubicación, retornar 404
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    private MediaType getMediaType(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            case "gif":
                return MediaType.IMAGE_GIF;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
