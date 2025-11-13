package com.culturarte.soap.endpoint;

import com.culturarte.logica.IControlador;
import com.culturarte.soap.gen.ListarPropuestasResponse;
import com.culturarte.soap.gen.ListarPropuestasRequest;
import com.culturarte.soap.gen.PropuestaType;
import com.culturarte.soap.gen.GetPropuestaResponse;
import com.culturarte.soap.gen.GetPropuestaRequest;
import com.culturarte.soap.gen.ObjectFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class PropuestasEndpoint {

    private static final String NAMESPACE = "http://www.culturarte.com/ws/propuestas";

    private final IControlador ctrl;

    public PropuestasEndpoint(IControlador ctrl) {
        this.ctrl = ctrl;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "listarPropuestasRequest")
    @ResponsePayload
    public ListarPropuestasResponse listarPropuestas(@RequestPayload ListarPropuestasRequest request) throws Exception {
        ListarPropuestasResponse resp = new ListarPropuestasResponse();
        ObjectFactory of = new ObjectFactory();

        // Obtener propuestas desde la capa de negocio
        java.util.List<com.culturarte.logica.datatypes.DTPropuesta> dtList = ctrl.getDTPropuestasWeb();
        for (com.culturarte.logica.datatypes.DTPropuesta dp : dtList) {
            PropuestaType p = of.createPropuestaType();
            p.setTitulo(dp.getTitulo());
            p.setDescripcion(dp.getDescripcion());
            p.setProponente(dp.getProponente());
            p.setCategoria(dp.getCategoria());
            p.setEstado(dp.getEstadoActual() != null ? dp.getEstadoActual().toString() : null);
            p.setImagen(dp.getImagen());
            if (dp.getFechaPrevista() != null) {
                javax.xml.datatype.XMLGregorianCalendar xgc = javax.xml.datatype.DatatypeFactory.newInstance()
                        .newXMLGregorianCalendarDate(dp.getFechaPrevista().getYear(), dp.getFechaPrevista().getMonthValue(), dp.getFechaPrevista().getDayOfMonth(), javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED);
                p.setFechaPrevista(xgc);
            }
            p.setMontoNecesario(dp.getMontoNecesario());
            p.setMontoRecaudado(dp.getMontoRecaudado());
            p.setCantColaboradores(dp.getCantColaboradores());
            resp.getPropuesta().add(p);
        }

        return resp;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "getPropuestaRequest")
    @ResponsePayload
    public GetPropuestaResponse getPropuesta(@RequestPayload GetPropuestaRequest request) throws Exception {
        GetPropuestaResponse resp = new GetPropuestaResponse();
        ObjectFactory of = new ObjectFactory();

        String titulo = request.getTitulo();
        com.culturarte.logica.datatypes.DTPropuesta dp = ctrl.getDTPropuesta(titulo);
        if (dp != null) {
            PropuestaType p = of.createPropuestaType();
            p.setTitulo(dp.getTitulo());
            p.setDescripcion(dp.getDescripcion());
            p.setProponente(dp.getProponente());
            p.setCategoria(dp.getCategoria());
            p.setEstado(dp.getEstadoActual() != null ? dp.getEstadoActual().toString() : null);
            p.setImagen(dp.getImagen());
            if (dp.getFechaPrevista() != null) {
                javax.xml.datatype.XMLGregorianCalendar xgc = javax.xml.datatype.DatatypeFactory.newInstance()
                        .newXMLGregorianCalendarDate(dp.getFechaPrevista().getYear(), dp.getFechaPrevista().getMonthValue(), dp.getFechaPrevista().getDayOfMonth(), javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED);
                p.setFechaPrevista(xgc);
            }
            p.setMontoNecesario(dp.getMontoNecesario());
            p.setMontoRecaudado(dp.getMontoRecaudado());
            p.setCantColaboradores(dp.getCantColaboradores());
            resp.setPropuesta(p);
        }
        return resp;
    }
}

