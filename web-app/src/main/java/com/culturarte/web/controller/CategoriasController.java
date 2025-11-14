package com.culturarte.web.controller;

import com.culturarte.logica.datatypes.DTCategoria;
import com.culturarte.soap.gen.CategoriaType;
import com.culturarte.soap.gen.GetCategoriasRequest;
import com.culturarte.soap.gen.GetCategoriasResponse;
import com.culturarte.web.soap.client.CategoriasSoapClient;
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

    @Autowired
    private CategoriasSoapClient categoriasSoapClient;

    @ResponseBody
    @GetMapping("/lista")
    public List<String> categorias() {
        try {
            // Creamos el request vacío (según el XSD)
            GetCategoriasRequest request = new GetCategoriasRequest();

            // Llamamos al servicio SOAP
            GetCategoriasResponse response = categoriasSoapClient.obtenerCategorias(request);

            return response.getCategoria()
                    .stream()
                    .map(CategoriaType::getNombre)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            List<String> error = new ArrayList<>();
            error.add("Error al obtener categorías vía SOAP: " + e.getMessage());
            return error;
        }
    }

    @ResponseBody
    @GetMapping("/tree")
    public List<DTCategoria> categoriasTree() {
        try {
            // Por ahora simplemente mapeamos las categorías planas a un formato simple
            GetCategoriasRequest request = new GetCategoriasRequest();
            GetCategoriasResponse response = categoriasSoapClient.obtenerCategorias(request);

            return response.getCategoria()
                    .stream()
                    .map(CategoriaType::getNombre)
                    .map(DTCategoria::new)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            List<DTCategoria> error = new ArrayList<>();
            DTCategoria errorCat = new DTCategoria("Error al obtener categorías: " + e.getMessage());
            error.add(errorCat);
            return error;
        }
    }
}
