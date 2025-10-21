package com.culturarte.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ImagePathResolver {
    private ImagePathResolver() {}

    public static String resolve(String rutaImagen) {
        if (rutaImagen == null || rutaImagen.isBlank()) {
            return null;
        }
        String trimmed = rutaImagen.trim();
        // URLs o URIs explícitas
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.startsWith("file:")) {
            return trimmed;
        }
        // Si es absoluto en el SO y existe, usar tal cual
        try {
            Path asPath = Paths.get(trimmed);
            if (asPath.isAbsolute() && Files.exists(asPath)) {
                return asPath.toString();
            }
        } catch (Exception ignored) {}

        // Base dir configurable (propiedad JVM o variable de entorno)
        String base = System.getProperty("app.uploads.dir");
        if (base == null || base.isBlank()) {
            base = System.getenv("APP_UPLOADS_DIR");
        }
        if (base == null || base.isBlank()) {
            base = "uploads";
        }

        String relative = trimmed;
        // Normalizar común: valores como "/uploads/imagenes/x.jpg" o "uploads/imagenes/x.jpg"
        String normalized = trimmed.replace("\\", "/");
        if (normalized.startsWith("/uploads/")) {
            relative = normalized.substring("/uploads/".length());
        } else if (normalized.startsWith("uploads/")) {
            relative = normalized.substring("uploads/".length());
        }
        // Si quedó con "/" inicial, quitarlo
        while (relative.startsWith("/")) {
            relative = relative.substring(1);
        }

        Path candidate = Paths.get(base).toAbsolutePath().normalize().resolve(relative).normalize();
        return candidate.toString();
    }
}
