package com.culturarte.web.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.culturarte.logica.IControlador;
import org.springframework.web.bind.annotation.GetMapping;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;

import com.culturarte.logica.datatypes.DTCategoria;

@Controller
@RequestMapping("/categorias")
public class CategoriasController {

    @Autowired
    private IControlador ctrl;

    @ResponseBody
    @GetMapping("/lista")
    List<String> categorias(){
        return ctrl.listarCategoriasWebCompletas();
    }

    @ResponseBody
    @GetMapping("/tree")
    public List<DTCategoria> categoriasTree() {
        try {
            DefaultTreeModel model = ctrl.listarCategorias();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
            List<DTCategoria> lista = new ArrayList<>();
            if (root.getChildCount() > 0) {
                for (int i = 0; i < root.getChildCount(); i++) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
                    lista.add(nodeToDto(child));
                }
            }
            return lista;
        } catch (UnsupportedOperationException e) {
            List<String> categorias = ctrl.listarCategoriasWeb();
            List<DTCategoria> lista = new ArrayList<>();
            for (String cat : categorias) {
                lista.add(new DTCategoria(cat));
            }
            return lista;
        }
    }

    private DTCategoria nodeToDto(DefaultMutableTreeNode node) {
        Object user = node.getUserObject();
        String nombre = user != null ? user.toString() : "";
        DTCategoria dto = new DTCategoria(nombre);
        if (node.getChildCount() > 0) {
            for (int i = 0; i < node.getChildCount(); i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
                dto.addHijo(nodeToDto(child));
            }
        }
        return dto;
    }
}
