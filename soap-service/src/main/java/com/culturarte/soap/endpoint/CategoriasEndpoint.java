package com.culturarte.soap.endpoint;

import com.culturarte.logica.IControlador;
import java.util.List;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import com.culturarte.soap.gen.GetCategoriasResponse;
import com.culturarte.soap.gen.GetCategoriasRequest;

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

		// Delegar a la lógica de negocio
		List<String> categorias = ctrl.listarCategoriasWeb();
		response.getCategoria().addAll(categorias);

		return response;
	}
}
