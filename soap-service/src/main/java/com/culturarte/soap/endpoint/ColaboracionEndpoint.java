package com.culturarte.soap.endpoint;

import com.culturarte.logica.IControlador;
import com.culturarte.logica.datatypes.DTColaborador;
import com.culturarte.soap.gen.ColaboracionType;
import com.culturarte.soap.gen.PropuestaType;
import com.culturarte.soap.gen.GetColaboracionRequest;
import com.culturarte.soap.gen.GetColaboracionResponse;
import com.culturarte.soap.gen.GetColaboracionesPorUsuarioRequest;
import com.culturarte.soap.gen.GetColaboracionesPorUsuarioResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.*;

@Endpoint
public class ColaboracionEndpoint {

    private static final String NAMESPACE_URI = "http://culturarte.com/ws/colaboraciones";

    @Autowired
    private IControlador ctrl;
    @Autowired
    private PropuestasEndpoint propuestasEndpoint;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getColaboracionesPorUsuarioRequest")
    @ResponsePayload
    public GetColaboracionesPorUsuarioResponse getColaboracionesPorUsuario(
            @RequestPayload GetColaboracionesPorUsuarioRequest request) {

        GetColaboracionesPorUsuarioResponse response = new GetColaboracionesPorUsuarioResponse();
        DTColaborador colab = ctrl.getDTColaborador(request.getNickname());

        if (colab != null && colab.getColaboraciones() != null) {
            colab.getColaboraciones().forEach(c -> response.getColaboraciones().add(mapColaboracion(c)));
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getColaboracionRequest")
    @ResponsePayload
    public GetColaboracionResponse getColaboracion(@RequestPayload GetColaboracionRequest request) {
        GetColaboracionResponse response = new GetColaboracionResponse();

//        DTColaborador colab = ctrl.getDTColaborador(request.getNickColaborador());
//        if (colab != null && colab.getColaboraciones() != null) {
//            for (com.culturarte.logica.datatypes.DTColaboracion c : colab.getColaboraciones()) {
//                if (c.getTituloPropuesta().equals(request.getTituloPropuesta())) {
//                    response.setColaboracion(mapColaboracion(c));
//                    com.culturarte.logica.datatypes.DTPropuesta propuesta = c.getPropuesta() != null ? c.getPropuesta() : ctrl.getDTPropuesta(c.getTituloPropuesta());
//                    if (propuesta != null) {
//                        response.setPropuesta(mapPropuesta(propuesta));
//                    }
//                    break;
//                }
//            }
//        }

        return response;
    }

    protected ColaboracionType mapColaboracion(com.culturarte.logica.datatypes.DTColaboracion source) {
        ColaboracionType dto = new ColaboracionType();
        dto.setNickColaborador(source.getNickColaborador());
        dto.setPropuesta(propuestasEndpoint.mapToSoapPropuesta(source.getPropuesta()));
        dto.setFecha(source.getFecha() != null ? source.getFecha().toString() : null);
        dto.setHora(source.getHora() != null ? source.getHora().toString() : null);
        dto.setMonto(source.getMonto());
        dto.setTipoRetorno(source.getTipoRetorno() != null ? source.getTipoRetorno().name() : null);
        return dto;
    }
}
