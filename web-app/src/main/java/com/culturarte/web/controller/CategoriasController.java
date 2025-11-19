package com.culturarte.web.controller;

import com.culturarte.logica.datatypes.DTCategoria;
import com.culturarte.soap.gen.CategoriaType;
import com.culturarte.soap.gen.GetCategoriasRequest;
import com.culturarte.soap.gen.GetCategoriasResponse;
import com.culturarte.web.soap.client.CategoriasSoapClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/categorias")
public class CategoriasController {

    private static final Logger logger = LoggerFactory.getLogger(CategoriasController.class);

    @Autowired
    private CategoriasSoapClient categoriasSoapClient;

    @ResponseBody
    @GetMapping("/lista")
    public List<String> categorias() {
        logger.info("=== INICIO categorias/lista (REST endpoint) ===");
        try {
            GetCategoriasRequest request = new GetCategoriasRequest();
            logger.debug("Llamando al servicio SOAP para obtener categorías");

            GetCategoriasResponse response = categoriasSoapClient.obtenerCategorias(request);

            if (response == null) {
                logger.warn("Respuesta SOAP null en categorias/lista");
                return new ArrayList<>();
            }
            
            if (response.getCategoria() == null || response.getCategoria().isEmpty()) {
                logger.warn("Lista de categorías vacía o null en respuesta");
                return new ArrayList<>();
            }

            List<String> resultado = response.getCategoria()
                    .stream()
                    .filter(cat -> cat != null && cat.getNombre() != null)
                    .map(CategoriaType::getNombre)
                    .collect(Collectors.toList());
            
            logger.info("Retornando {} categorías al cliente", resultado.size());
            logger.debug("=== FIN categorias/lista (exitoso) ===");
            return resultado;
        } catch (Exception e) {
            logger.error("=== ERROR en categorias/lista (REST endpoint) ===", e);
            logger.error("Tipo de excepción: {}", e.getClass().getName());
            logger.error("Mensaje: {}", e.getMessage());
            if (e.getCause() != null) {
                logger.error("Causa: {}", e.getCause().getMessage());
            }
            // Retornar lista vacía en lugar de error para evitar problemas en el frontend
            return new ArrayList<>();
        }
    }

    @ResponseBody
    @GetMapping("/tree")
    public List<DTCategoria> categoriasTree() {
        logger.info("=== INICIO categorias/tree (REST endpoint) ===");
        try {
            GetCategoriasRequest request = new GetCategoriasRequest();
            logger.debug("Llamando al servicio SOAP para obtener categorías (tree)");

            GetCategoriasResponse response = categoriasSoapClient.obtenerCategorias(request);

            if (response == null) {
                logger.warn("Respuesta SOAP null en categorias/tree");
                return new ArrayList<>();
            }
            
            if (response.getCategoria() == null || response.getCategoria().isEmpty()) {
                logger.warn("Lista de categorías vacía o null en respuesta (tree)");
                return new ArrayList<>();
            }

            List<DTCategoria> raices = response.getCategoria().stream()
                    .filter(cat -> cat != null && cat.getNombre() != null && !cat.getNombre().trim().isEmpty())
                    .map(this::convertirCategoriaSoap)
                    .collect(Collectors.toList());
            
            logger.debug("Categorías raíz construidas: {}", raices.size());
            
            logger.info("Retornando {} categorías raíz (tree) al cliente", raices.size());
            logger.debug("=== FIN categorias/tree (exitoso) ===");
            return raices;

        } catch (Exception e) {
            logger.error("=== ERROR en categorias/tree (REST endpoint) ===", e);
            logger.error("Tipo de excepción: {}", e.getClass().getName());
            logger.error("Mensaje: {}", e.getMessage());
            if (e.getCause() != null) {
                logger.error("Causa: {}", e.getCause().getMessage());
            }
            // Retornar lista vacía en lugar de error para evitar problemas en el frontend
            return new ArrayList<>();
        }
    }
    
    private DTCategoria convertirCategoriaSoap(CategoriaType soapCategoria) {
        DTCategoria dto = new DTCategoria();
        dto.setNombre(soapCategoria.getNombre().trim());
        if (soapCategoria.getHijos() != null && !soapCategoria.getHijos().isEmpty()) {
            for (CategoriaType hijo : soapCategoria.getHijos()) {
                if (hijo != null && hijo.getNombre() != null && !hijo.getNombre().trim().isEmpty()) {
                    dto.addHijo(convertirCategoriaSoap(hijo));
                }
            }
        }
        return dto;
    }
}
