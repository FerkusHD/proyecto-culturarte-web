package com.culturarte.soap.endpoints;

import com.culturarte.logica.IControlador;
import com.culturarte.logica.datatypes.DTColaboracion;
import com.culturarte.logica.datatypes.DTColaborador;
import com.culturarte.logica.datatypes.DTPropuesta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.*;
import java.util.List;

@Endpoint
public class ColaboracionEndpoint {

    private static final String NAMESPACE_URI = "http://culturarte.com/ws/colaboraciones";

    @Autowired
    private IControlador ctrl;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getColaboracionesPorUsuarioRequest")
    @ResponsePayload
    public GetColaboracionesPorUsuarioResponse getColaboracionesPorUsuario(
            @RequestPayload GetColaboracionesPorUsuarioRequest request) {

        GetColaboracionesPorUsuarioResponse response = new GetColaboracionesPorUsuarioResponse();
        DTColaborador colab = ctrl.getDTColaborador(request.getNickname());

        if (colab != null && colab.getColaboraciones() != null) {
            response.getColaboraciones().addAll(colab.getColaboraciones());
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getColaboracionRequest")
    @ResponsePayload
    public GetColaboracionResponse getColaboracion(@RequestPayload GetColaboracionRequest request) {
        GetColaboracionResponse response = new GetColaboracionResponse();

        DTColaborador colab = ctrl.getDTColaborador(request.getNickColaborador());
        if (colab != null && colab.getColaboraciones() != null) {
            for (DTColaboracion c : colab.getColaboraciones()) {
                if (c.getTituloPropuesta().equals(request.getTituloPropuesta())) {
                    response.setColaboracion(c);
                    response.setPropuesta(c.getPropuesta() != null ? c.getPropuesta() : ctrl.getDTPropuesta(c.getTituloPropuesta()));
                    break;
                }
            }
        }

        return response;
    }
}
