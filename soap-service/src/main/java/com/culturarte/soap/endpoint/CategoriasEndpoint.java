package com.culturarte.soap.endpoints;

import java.util.List;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import com.culturarte.logica.IControlador;
import com.culturarte.soap.gen.*;

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

		List<String> categoriasRaiz = ctrl.listarCategoriasWebCompletas();
		for (String catNombre : categoriasRaiz) {
			CategoriaType catType = new CategoriaType();
			catType.setNombre(catNombre);
			response.getCategoria().add(catType);
		}

		return response;
	}
}
