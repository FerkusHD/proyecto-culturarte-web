//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v3.0.0 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.11.05 a las 03:36:28 PM GMT-03:00 
//


package com.culturarte.soap.gen;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.culturarte.soap.gen package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetCategoriasRequest_QNAME = new QName("http://www.culturarte.com/ws/categorias", "getCategoriasRequest");
    private final static QName _ListarPropuestasRequest_QNAME = new QName("http://www.culturarte.com/ws/propuestas", "listarPropuestasRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.culturarte.soap.gen
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetCategoriasResponse }
     * 
     */
    public GetCategoriasResponse createGetCategoriasResponse() {
        return new GetCategoriasResponse();
    }

    /**
     * Create an instance of {@link PingRequest }
     * 
     */
    public PingRequest createPingRequest() {
        return new PingRequest();
    }

    /**
     * Create an instance of {@link PingResponse }
     * 
     */
    public PingResponse createPingResponse() {
        return new PingResponse();
    }

    /**
     * Create an instance of {@link GetPropuestasRequest }
     * 
     */
    public GetPropuestasRequest createGetPropuestasRequest() {
        return new GetPropuestasRequest();
    }

    /**
     * Create an instance of {@link GetPropuestasResponse }
     * 
     */
    public GetPropuestasResponse createGetPropuestasResponse() {
        return new GetPropuestasResponse();
    }

    /**
     * Create an instance of {@link PropuestaType }
     * 
     */
    public PropuestaType createPropuestaType() {
        return new PropuestaType();
    }

    /**
     * Create an instance of {@link GetUsuariosRequest }
     * 
     */
    public GetUsuariosRequest createGetUsuariosRequest() {
        return new GetUsuariosRequest();
    }

    /**
     * Create an instance of {@link GetUsuariosResponse }
     * 
     */
    public GetUsuariosResponse createGetUsuariosResponse() {
        return new GetUsuariosResponse();
    }

    /**
     * Create an instance of {@link UsuarioType }
     * 
     */
    public UsuarioType createUsuarioType() {
        return new UsuarioType();
    }

    /**
     * Create an instance of {@link DtFecha }
     * 
     */
    public DtFecha createDtFecha() {
        return new DtFecha();
    }

    /**
     * Create an instance of {@link DtHora }
     * 
     */
    public DtHora createDtHora() {
        return new DtHora();
    }

    /**
     * Create an instance of {@link GetUsuarioRequest }
     * 
     */
    public GetUsuarioRequest createGetUsuarioRequest() {
        return new GetUsuarioRequest();
    }

    /**
     * Create an instance of {@link GetUsuarioResponse }
     * 
     */
    public GetUsuarioResponse createGetUsuarioResponse() {
        return new GetUsuarioResponse();
    }

    /**
     * Create an instance of {@link ListarPropuestasResponse }
     * 
     */
    public ListarPropuestasResponse createListarPropuestasResponse() {
        return new ListarPropuestasResponse();
    }

    /**
     * Create an instance of {@link GetPropuestaRequest }
     * 
     */
    public GetPropuestaRequest createGetPropuestaRequest() {
        return new GetPropuestaRequest();
    }

    /**
     * Create an instance of {@link GetPropuestaResponse }
     * 
     */
    public GetPropuestaResponse createGetPropuestaResponse() {
        return new GetPropuestaResponse();
    }

    /**
     * Create an instance of {@link GetEspectaculoRequest }
     * 
     */
    public GetEspectaculoRequest createGetEspectaculoRequest() {
        return new GetEspectaculoRequest();
    }

    /**
     * Create an instance of {@link GetEspectaculoResponse }
     * 
     */
    public GetEspectaculoResponse createGetEspectaculoResponse() {
        return new GetEspectaculoResponse();
    }

    /**
     * Create an instance of {@link EspectaculoType }
     * 
     */
    public EspectaculoType createEspectaculoType() {
        return new EspectaculoType();
    }

    /**
     * Create an instance of {@link FuncionType }
     * 
     */
    public FuncionType createFuncionType() {
        return new FuncionType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.culturarte.com/ws/categorias", name = "getCategoriasRequest")
    public JAXBElement<Object> createGetCategoriasRequest(Object value) {
        return new JAXBElement<Object>(_GetCategoriasRequest_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Object }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.culturarte.com/ws/propuestas", name = "listarPropuestasRequest")
    public JAXBElement<Object> createListarPropuestasRequest(Object value) {
        return new JAXBElement<Object>(_ListarPropuestasRequest_QNAME, Object.class, null, value);
    }

}
