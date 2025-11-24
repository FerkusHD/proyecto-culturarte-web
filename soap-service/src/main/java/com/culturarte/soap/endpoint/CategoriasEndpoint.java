package com.culturarte.soap.endpoint;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.springframework.stereotype.Component;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.culturarte.logica.IControlador;
import com.culturarte.soap.gen.CategoriaType;
import com.culturarte.soap.gen.GetCategoriasRequest;
import com.culturarte.soap.gen.GetCategoriasResponse;
import com.culturarte.soap.gen.ObjectFactory;

@Component
@Endpoint
public class CategoriasEndpoint {

	private static final String NAMESPACE = "http://www.culturarte.com/ws/categorias";
	private final IControlador ctrl;

	public CategoriasEndpoint(IControlador ctrl) {
		this.ctrl = ctrl;
	}

	@PayloadRoot(namespace = NAMESPACE, localPart = "getCategoriasRequest")
	@ResponsePayload
	public GetCategoriasResponse getCategorias(@RequestPayload GetCategoriasRequest request) {

		GetCategoriasResponse response = new GetCategoriasResponse();
		ObjectFactory factory = new ObjectFactory();

		DefaultTreeModel treeModel = ctrl.listarCategorias();
		if (treeModel != null && treeModel.getRoot() instanceof DefaultMutableTreeNode root) {
			Enumeration<?> children = root.children();
			while (children.hasMoreElements()) {
				Object childObj = children.nextElement();
				if (childObj instanceof DefaultMutableTreeNode childNode) {
					response.getCategoria().add(convertirNodo(childNode, factory));
				}
			}
		}

		return response;
	}

	private CategoriaType convertirNodo(DefaultMutableTreeNode node, ObjectFactory factory) {
		CategoriaType categoriaType = factory.createCategoriaType();
		Object userObject = node.getUserObject();
		if (userObject != null) {
			categoriaType.setNombre(userObject.toString());
		}

		Enumeration<?> children = node.children();
		while (children.hasMoreElements()) {
			Object childObj = children.nextElement();
			if (childObj instanceof DefaultMutableTreeNode childNode) {
				categoriaType.getHijos().add(convertirNodo(childNode, factory));
			}
		}

		return categoriaType;
	}
}
