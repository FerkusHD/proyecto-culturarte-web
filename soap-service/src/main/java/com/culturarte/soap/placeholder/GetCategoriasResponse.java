package com.culturarte.soap.placeholder;

import java.util.ArrayList;
import java.util.List;

public class GetCategoriasResponse {

    private List<String> categoria = new ArrayList<>();

    public List<String> getCategoria() {
        return categoria;
    }

    public void setCategoria(List<String> categoria) {
        this.categoria = categoria;
    }
}
