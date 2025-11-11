package com.culturarte.soap.endpoint;

import com.culturarte.logica.IControlador;
import com.culturarte.soap.gen.GetUsuarioRequest;
import com.culturarte.soap.gen.GetUsuarioResponse;
import com.culturarte.soap.gen.UsuarioType;
import com.culturarte.soap.gen.ObjectFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class UsuarioEndpoint {

    private static final String NAMESPACE = "http://www.culturarte.com/ws/usuarios";

    private final IControlador ctrl;

    public UsuarioEndpoint(IControlador ctrl) {
        this.ctrl = ctrl;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "getUsuarioRequest")
    @ResponsePayload
    public GetUsuarioResponse getUsuario(@RequestPayload GetUsuarioRequest request) throws Exception {
        GetUsuarioResponse resp = new GetUsuarioResponse();
        ObjectFactory of = new ObjectFactory();

        String nick = request.getNickname();
        com.culturarte.logica.datatypes.DTUsuario du = ctrl.getDTUsuario(nick);
        if (du != null) {
            UsuarioType ut = of.createUsuarioType();
            ut.setNickname(du.getNickname());
            ut.setNombre(du.getNombre());
            ut.setApellido(du.getApellido());
            ut.setEmail(du.getEmail());
            ut.setImagen(du.getImagen());
            if (du.getFechaNacimiento() != null) {
                javax.xml.datatype.XMLGregorianCalendar xgc = javax.xml.datatype.DatatypeFactory.newInstance()
                        .newXMLGregorianCalendarDate(du.getFechaNacimiento().getYear(), du.getFechaNacimiento().getMonthValue(), du.getFechaNacimiento().getDayOfMonth(), javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED);
                ut.setFechaNacimiento(xgc);
            }
            ut.setTipo(du.getTipo());
            resp.setUsuario(ut);
        }
        return resp;
    }
}

