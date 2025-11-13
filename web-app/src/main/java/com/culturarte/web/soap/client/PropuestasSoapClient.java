package com.culturarte.web.soap.client;

import com.culturarte.web.soap.*;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.time.LocalDate;
import java.util.List;

@Component
public class PropuestasSoapClient {

    private final WebServiceTemplate webServiceTemplate;
    private static final String URI = "http://localhost:8080/ws/propuestas";

    public PropuestasSoapClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    // ------------------ LISTAR PROPUESTAS -------------------
    public List<PropuestaType> listarPropuestas() {
        ListarPropuestasRequest request = new ListarPropuestasRequest();
        ListarPropuestasResponse response = (ListarPropuestasResponse)
                webServiceTemplate.marshalSendAndReceive(URI, request);
        return response.getPropuesta();
    }

    // ------------------ OBTENER PROPUESTA -------------------
    public PropuestaType getPropuesta(String titulo) {
        GetPropuestaRequest request = new GetPropuestaRequest();
        request.setTitulo(titulo);
        GetPropuestaResponse response = (GetPropuestaResponse)
                webServiceTemplate.marshalSendAndReceive(URI, request);
        return response.getPropuesta();
    }

    // ------------------ ALTA DE PROPUESTA -------------------
    public boolean altaPropuesta(String titulo, String descripcion, String lugar,
                                 LocalDate fechaPrevista, String categoria, List<String> tiposRetorno,
                                 Float montoEntrada, Float montoNecesario, String imagenBase64,
                                 String nickProponente) {
        AltaPropuestaRequest request = new AltaPropuestaRequest();
        request.setTitulo(titulo);
        request.setDescripcion(descripcion);
        request.setLugar(lugar);
        request.setFechaPrevista(fechaPrevista);
        request.setCategoria(categoria);
        request.getTiposRetorno().addAll(tiposRetorno);
        request.setMontoEntrada(montoEntrada);
        request.setMontoNecesario(montoNecesario);
        request.setImagenBase64(imagenBase64);
        request.setNickProponente(nickProponente);

        AltaPropuestaResponse response = (AltaPropuestaResponse)
                webServiceTemplate.marshalSendAndReceive(URI, request);
        System.out.println(response.getMensaje());
        return response.isExito();
    }

    // ------------------ ALTA DE COLABORACIÓN -------------------
    public boolean altaColaboracion(String nickColaborador, String tituloPropuesta,
                                    float monto, String tipoRetorno) {
        AltaColaboracionRequest request = new AltaColaboracionRequest();
        request.setNickColaborador(nickColaborador);
        request.setTituloPropuesta(tituloPropuesta);
        request.setMonto(monto);
        request.setTipoRetorno(tipoRetorno);

        AltaColaboracionResponse response = (AltaColaboracionResponse)
                webServiceTemplate.marshalSendAndReceive(URI, request);
        System.out.println(response.getMensaje());
        return response.isExito();
    }

    // ------------------ CANCELAR PROPUESTA -------------------
    public boolean cancelarPropuesta(String tituloPropuesta) {
        CancelarPropuestaRequest request = new CancelarPropuestaRequest();
        request.setTituloPropuesta(tituloPropuesta);
        CancelarPropuestaResponse response = (CancelarPropuestaResponse)
                webServiceTemplate.marshalSendAndReceive(URI, request);
        System.out.println(response.getMensaje());
        return response.isExito();
    }

    // ------------------ EXTENDER FINANCIACIÓN -------------------
    public boolean extenderFinanciacion(String tituloPropuesta, LocalDate nuevaFecha) {
        ExtenderFinanciacionRequest request = new ExtenderFinanciacionRequest();
        request.setTituloPropuesta(tituloPropuesta);
        request.setNuevaFecha(nuevaFecha);
        ExtenderFinanciacionResponse response = (ExtenderFinanciacionResponse)
                webServiceTemplate.marshalSendAndReceive(URI, request);
        System.out.println(response.getMensaje());
        return response.isExito();
    }

    // ------------------ AGREGAR COMENTARIO -------------------
    public boolean agregarComentario(String nickColaborador, String tituloPropuesta, String texto) {
        AgregarComentarioRequest request = new AgregarComentarioRequest();
        request.setNickColaborador(nickColaborador);
        request.setTituloPropuesta(tituloPropuesta);
        request.setTexto(texto);

        AgregarComentarioResponse response = (AgregarComentarioResponse)
                webServiceTemplate.marshalSendAndReceive(URI, request);
        System.out.println(response.getMensaje());
        return response.isExito();
    }

    // ------------------ FAVORITAS -------------------
    public boolean agregarFavorita(String nickUsuario, String tituloPropuesta) {
        AgregarFavoritaRequest request = new AgregarFavoritaRequest();
        request.setNickUsuario(nickUsuario);
        request.setTituloPropuesta(tituloPropuesta);

        AgregarFavoritaResponse response = (AgregarFavoritaResponse)
                webServiceTemplate.marshalSendAndReceive(URI, request);
        System.out.println(response.getMensaje());
        return response.isExito();
    }

    public boolean quitarFavorita(String nickUsuario, String tituloPropuesta) {
        QuitarFavoritaRequest request = new QuitarFavoritaRequest();
        request.setNickUsuario(nickUsuario);
        request.setTituloPropuesta(tituloPropuesta);

        QuitarFavoritaResponse response = (QuitarFavoritaResponse)
                webServiceTemplate.marshalSendAndReceive(URI, request);
        System.out.println(response.getMensaje());
        return response.isExito();
    }
}
