package com.culturarte.soap.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import com.culturarte.logica.IControlador;
import com.culturarte.soap.gen.*;

@Endpoint
public class PropuestaEndpoint {

    private static final String NAMESPACE_URI = "http://culturarte.com/soap/gen";
    private final IControlador controlador;

    @Autowired
    public PropuestaEndpoint(IControlador controlador) {
        this.controlador = controlador;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getPropuestasRequest")
    @ResponsePayload
    public GetPropuestasResponse getPropuestas(@RequestPayload GetPropuestasRequest request) {
        GetPropuestasResponse response = new GetPropuestasResponse();
        
        // Aquí obtendremos las propuestas desde el controlador
        // y las convertiremos al formato SOAP
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "pingRequest")
    @ResponsePayload
    public PingResponse ping(@RequestPayload PingRequest request) {
        PingResponse response = new PingResponse();
        response.setMessage("Pong!");
        return response;
    }
}