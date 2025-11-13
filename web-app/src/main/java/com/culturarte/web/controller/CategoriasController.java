package com.culturarte.web.controller;

import com.culturarte.soap.gen.GetCategoriasRequest;
import com.culturarte.soap.gen.GetCategoriasResponse;
import com.culturarte.web.soap.CategoriasSoapClient;
import com.culturarte.logica.datatypes.DTCategoria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

            // Retornamos la lista de categorías
            return response.getCategoria();
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

            List<DTCategoria> lista = new ArrayList<>();
            for (String cat : response.getCategoria()) {
                lista.add(new DTCategoria(cat));
            }

            return lista;

        } catch (Exception e) {
            e.printStackTrace();
            List<DTCategoria> error = new ArrayList<>();
            DTCategoria errorCat = new DTCategoria("Error al obtener categorías: " + e.getMessage());
            error.add(errorCat);
            return error;
        }
    }
}
