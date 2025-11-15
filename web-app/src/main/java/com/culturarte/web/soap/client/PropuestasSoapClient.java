package com.culturarte.web.soap.client;

import com.culturarte.soap.gen.AgregarComentarioRequest;
import com.culturarte.soap.gen.AgregarComentarioResponse;
import com.culturarte.soap.gen.AgregarFavoritaRequest;
import com.culturarte.soap.gen.AgregarFavoritaResponse;
import com.culturarte.soap.gen.AltaColaboracionRequest;
import com.culturarte.soap.gen.AltaColaboracionResponse;
import com.culturarte.soap.gen.AltaPropuestaRequest;
import com.culturarte.soap.gen.AltaPropuestaResponse;
import com.culturarte.soap.gen.CancelarPropuestaRequest;
import com.culturarte.soap.gen.CancelarPropuestaResponse;
import com.culturarte.soap.gen.ExtenderFinanciacionRequest;
import com.culturarte.soap.gen.ExtenderFinanciacionResponse;
import com.culturarte.soap.gen.GetPropuestaRequest;
import com.culturarte.soap.gen.GetPropuestaResponse;
import com.culturarte.soap.gen.ListarPropuestasRequest;
import com.culturarte.soap.gen.ListarPropuestasResponse;
import com.culturarte.soap.gen.PropuestaType;
import com.culturarte.soap.gen.QuitarFavoritaRequest;
import com.culturarte.soap.gen.QuitarFavoritaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.Collections;
import java.util.List;

@Component
public class PropuestasSoapClient {

    private final WebServiceTemplate webServiceTemplate;

    @Value("${soap.service.url:}")
    private String soapServiceUrl;

    @Value("${soap.service.host:localhost}")
    private String soapServiceHost;

    @Value("${soap.service.port:8081}")
    private String soapServicePort;

    @Value("${soap.service.context-path:/soap/ws}")
    private String soapServiceContextPath;

    public PropuestasSoapClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    private String getEndpoint() {
        if (soapServiceUrl != null && !soapServiceUrl.isEmpty() && !soapServiceUrl.startsWith("${")) {
            return soapServiceUrl;
        }
        return String.format("http://%s:%s%s/propuestas", soapServiceHost, soapServicePort, soapServiceContextPath);
    }

    public List<PropuestaType> listarPropuestas() {
        ListarPropuestasRequest request = new ListarPropuestasRequest();
        ListarPropuestasResponse response = (ListarPropuestasResponse)
                webServiceTemplate.marshalSendAndReceive(getEndpoint(), request);
        if (response == null || response.getPropuesta() == null) {
            return Collections.emptyList();
        }
        return response.getPropuesta();
    }

    public PropuestaType getPropuesta(String titulo) {
        GetPropuestaRequest request = new GetPropuestaRequest();
        request.setTitulo(titulo);
        GetPropuestaResponse response = (GetPropuestaResponse)
                webServiceTemplate.marshalSendAndReceive(getEndpoint(), request);
        return response != null ? response.getPropuesta() : null;
    }

    public AltaColaboracionResponse altaColaboracion(AltaColaboracionRequest request) {
        return (AltaColaboracionResponse) webServiceTemplate.marshalSendAndReceive(getEndpoint(), request);
    }

    public AgregarComentarioResponse agregarComentario(AgregarComentarioRequest request) {
        return (AgregarComentarioResponse) webServiceTemplate.marshalSendAndReceive(getEndpoint(), request);
    }

    public AgregarFavoritaResponse agregarFavorita(AgregarFavoritaRequest request) {
        return (AgregarFavoritaResponse) webServiceTemplate.marshalSendAndReceive(getEndpoint(), request);
    }

    public QuitarFavoritaResponse quitarFavorita(QuitarFavoritaRequest request) {
        return (QuitarFavoritaResponse) webServiceTemplate.marshalSendAndReceive(getEndpoint(), request);
    }

    public CancelarPropuestaResponse cancelarPropuesta(CancelarPropuestaRequest request) {
        return (CancelarPropuestaResponse) webServiceTemplate.marshalSendAndReceive(getEndpoint(), request);
    }

    public AltaPropuestaResponse altaPropuesta(AltaPropuestaRequest request) {
        return (AltaPropuestaResponse) webServiceTemplate.marshalSendAndReceive(getEndpoint(), request);
    }

    public ExtenderFinanciacionResponse extenderFinanciacion(ExtenderFinanciacionRequest request) {
        return (ExtenderFinanciacionResponse) webServiceTemplate.marshalSendAndReceive(getEndpoint(), request);
    }
}
