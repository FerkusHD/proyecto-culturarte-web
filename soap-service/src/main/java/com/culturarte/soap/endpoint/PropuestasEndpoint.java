package com.culturarte.soap.endpoint;

import com.culturarte.exepciones.ColaboracionYaExiste;
import com.culturarte.exepciones.PropuestaYaExiste;
import com.culturarte.logica.IControlador;
import com.culturarte.logica.datatypes.DTColaboracion;
import com.culturarte.logica.datatypes.DTPropuesta;
import com.culturarte.logica.enums.TipoEstado;
import com.culturarte.logica.enums.TipoRetorno;
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
import com.culturarte.soap.gen.ComentarioType;
import com.culturarte.soap.gen.ExtenderFinanciacionRequest;
import com.culturarte.soap.gen.ExtenderFinanciacionResponse;
import com.culturarte.soap.gen.GetPropuestaRequest;
import com.culturarte.soap.gen.GetPropuestaResponse;
import com.culturarte.soap.gen.ListarPropuestasRequest;
import com.culturarte.soap.gen.ListarPropuestasResponse;
import com.culturarte.soap.gen.PropuestaType;
import com.culturarte.soap.gen.QuitarFavoritaRequest;
import com.culturarte.soap.gen.QuitarFavoritaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.server.endpoint.annotation.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Endpoint
public class PropuestasEndpoint {

    private static final String NAMESPACE_URI = "http://www.culturarte.com/ws/propuestas";
    private static final DatatypeFactory DATATYPE_FACTORY = createDatatypeFactory();

    private final IControlador ctrl;

    @Autowired
    public PropuestasEndpoint(IControlador ctrl) {
        this.ctrl = ctrl;
    }

    private static DatatypeFactory createDatatypeFactory() {
        try {
            return DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException("No se pudo inicializar DatatypeFactory", e);
        }
    }

    // ------------------ LISTAR PROPUESTAS -------------------
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "listarPropuestasRequest")
    @ResponsePayload
    public ListarPropuestasResponse listarPropuestas(@RequestPayload ListarPropuestasRequest request) {
        ListarPropuestasResponse response = new ListarPropuestasResponse();
        List<DTPropuesta> propuestas = ctrl.getDTPropuestasWeb();
        if (propuestas != null) {
            response.getPropuesta().addAll(propuestas.stream().map(this::mapToSoapPropuesta).collect(Collectors.toList()));
        }
        return response;
    }

    // ------------------ OBTENER PROPUESTA -------------------
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getPropuestaRequest")
    @ResponsePayload
    public GetPropuestaResponse getPropuesta(@RequestPayload GetPropuestaRequest request) {
        GetPropuestaResponse response = new GetPropuestaResponse();
        DTPropuesta dt = ctrl.getDTPropuesta(request.getTitulo());
        if (dt != null) {
            response.setPropuesta(mapToSoapPropuesta(dt));
        }
        return response;
    }

    // ------------------ ALTA DE PROPUESTA -------------------
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "altaPropuestaRequest")
    @ResponsePayload
    public AltaPropuestaResponse altaPropuesta(@RequestPayload AltaPropuestaRequest request) {
        AltaPropuestaResponse response = new AltaPropuestaResponse();
        try {
            EnumSet<TipoRetorno> tiposRet = request.getTiposRetorno().stream()
                    .map(TipoRetorno::valueOf)
                    .collect(Collectors.toCollection(() -> EnumSet.noneOf(TipoRetorno.class)));
            LocalDate fechaPrevista = toLocalDate(request.getFechaPrevista());

            ctrl.altaPropuesta(
                    request.getTitulo(),
                    request.getDescripcion(),
                    request.getLugar(),
                    fechaPrevista != null ? fechaPrevista : LocalDate.now(),
                    request.getMontoEntrada(),
                    request.getMontoNecesario(),
                    tiposRet,
                    request.getImagenBase64(),
                    request.getNickProponente(),
                    request.getCategoria(),
                    LocalDate.now(),
                    LocalTime.now()
            );

            response.setExito(true);
            response.setMensaje("Propuesta registrada con éxito");
        } catch (PropuestaYaExiste e) {
            response.setExito(false);
            response.setMensaje("La propuesta ya existe");
        } catch (Exception e) {
            response.setExito(false);
            response.setMensaje("Error al registrar la propuesta: " + e.getMessage());
        }
        return response;
    }

    // ------------------ ALTA DE COLABORACIÓN -------------------
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "altaColaboracionRequest")
    @ResponsePayload
    public AltaColaboracionResponse altaColaboracion(@RequestPayload AltaColaboracionRequest request) {
        AltaColaboracionResponse response = new AltaColaboracionResponse();
        try {
            DTColaboracion existe = ctrl.getDTColaboracionPropuesta(request.getNickColaborador(), request.getTituloPropuesta());
            if (existe != null) {
                response.setExito(false);
                response.setMensaje("Ya existe una colaboración para este usuario y propuesta");
                return response;
            }

            ctrl.altaColaboracion(
                    request.getMonto(),
                    LocalDate.now(),
                    LocalTime.now(),
                    TipoRetorno.valueOf(request.getTipoRetorno().toUpperCase()),
                    request.getTituloPropuesta(),
                    request.getNickColaborador()
            );
            response.setExito(true);
            response.setMensaje("Colaboración registrada correctamente");
        } catch (ColaboracionYaExiste e) {
            response.setExito(false);
            response.setMensaje("Colaboración ya existe");
        } catch (Exception e) {
            response.setExito(false);
            response.setMensaje("Error al registrar colaboración: " + e.getMessage());
        }
        return response;
    }

    // ------------------ CANCELAR PROPUESTA -------------------
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "cancelarPropuestaRequest")
    @ResponsePayload
    public CancelarPropuestaResponse cancelarPropuesta(@RequestPayload CancelarPropuestaRequest request) {
        CancelarPropuestaResponse response = new CancelarPropuestaResponse();
        try {
            ctrl.nuevoEstadoPropuesta(request.getTituloPropuesta(), TipoEstado.CANCELADA, LocalDate.now(), LocalTime.now());
            response.setExito(true);
            response.setMensaje("Propuesta cancelada con éxito");
        } catch (Exception e) {
            response.setExito(false);
            response.setMensaje("No se pudo cancelar la propuesta");
        }
        return response;
    }

    // ------------------ EXTENDER FINANCIACIÓN -------------------
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "extenderFinanciacionRequest")
    @ResponsePayload
    public ExtenderFinanciacionResponse extenderFinanciacion(@RequestPayload ExtenderFinanciacionRequest request) {
        ExtenderFinanciacionResponse response = new ExtenderFinanciacionResponse();
        try {
            LocalDate nuevaFecha = toLocalDate(request.getNuevaFecha());
            ctrl.extenderFinanciacion(request.getTituloPropuesta(), nuevaFecha);
            response.setExito(true);
            response.setMensaje("Financiación extendida con éxito");
        } catch (Exception e) {
            response.setExito(false);
            response.setMensaje("No se pudo extender financiación: " + e.getMessage());
        }
        return response;
    }

    // ------------------ AGREGAR COMENTARIO -------------------
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "agregarComentarioRequest")
    @ResponsePayload
    public AgregarComentarioResponse agregarComentario(@RequestPayload AgregarComentarioRequest request) {
        AgregarComentarioResponse response = new AgregarComentarioResponse();
        try {
            if (request.getTexto() == null || request.getTexto().trim().isEmpty()) {
                response.setExito(false);
                response.setMensaje("El comentario no puede estar vacío");
                return response;
            }

            ctrl.agregarComentario(request.getTexto(), request.getNickColaborador(), request.getTituloPropuesta());
            response.setExito(true);
            response.setMensaje("Comentario agregado correctamente");
        } catch (Exception e) {
            response.setExito(false);
            response.setMensaje("Error al agregar comentario: " + e.getMessage());
        }
        return response;
    }

    // ------------------ FAVORITAS -------------------
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "agregarFavoritaRequest")
    @ResponsePayload
    public AgregarFavoritaResponse agregarFavorita(@RequestPayload AgregarFavoritaRequest request) {
        AgregarFavoritaResponse response = new AgregarFavoritaResponse();
        try {
            ctrl.agregarPropuestaFavorita(request.getNickUsuario(), request.getTituloPropuesta());
            response.setExito(true);
            response.setMensaje("Propuesta agregada a favoritos correctamente");
        } catch (Exception e) {
            response.setExito(false);
            response.setMensaje("Error al agregar a favoritos: " + e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "quitarFavoritaRequest")
    @ResponsePayload
    public QuitarFavoritaResponse quitarFavorita(@RequestPayload QuitarFavoritaRequest request) {
        QuitarFavoritaResponse response = new QuitarFavoritaResponse();
        try {
            ctrl.sacarPropuestaFavorita(request.getNickUsuario(), request.getTituloPropuesta());
            response.setExito(true);
            response.setMensaje("Propuesta eliminada de favoritos correctamente");
        } catch (Exception e) {
            response.setExito(false);
            response.setMensaje("Error al quitar de favoritos: " + e.getMessage());
        }
        return response;
    }

    // ------------------ MAPEO DTPropuesta -> SOAP -------------------
    protected PropuestaType mapToSoapPropuesta(DTPropuesta dt) {
        PropuestaType p = new PropuestaType();
        p.setTitulo(dt.getTitulo());
        p.setDescripcion(dt.getDescripcion());
        p.setLugar(dt.getLugar());
        p.setProponente(dt.getProponente());
        p.setCategoria(dt.getCategoria());
        p.setEstadoActual(dt.getEstadoActual() != null ? dt.getEstadoActual().toString() : null);
        p.setImagen(dt.getImagen());
        p.setFechaPrevista(toXmlDate(dt.getFechaPrevista()));
        p.setMontoEntrada(dt.getPrecioEntrada());
        p.setMontoNecesario(dt.getMontoNecesario());
        p.setMontoRecaudado(dt.getMontoRecaudado());
        int cantColabs = dt.getCantColaboradores();
        if (cantColabs == 0 && dt.getColaboradores() != null) {
            cantColabs = dt.getColaboradores().size();
        }
        p.setCantColaboradores(cantColabs);
        if (dt.getColaboradores() != null) p.getColaboradores().addAll(dt.getColaboradores());
        if (dt.getComentarios() != null) {
            dt.getComentarios().forEach(c -> {
                ComentarioType ct = new ComentarioType();
                String nick = c.getColaborador() != null ? c.getColaborador().getNickname() : null;
                ct.setNickColaborador(nick);
                ct.setTexto(c.getTexto());
                ct.setFecha(toXmlDate(c.getFecha()));
                p.getComentarios().add(ct);
            });
        }
        return p;
    }

    private LocalDate toLocalDate(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }
        return calendar.toGregorianCalendar().toZonedDateTime().toLocalDate();
    }

    private XMLGregorianCalendar toXmlDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return DATATYPE_FACTORY.newXMLGregorianCalendarDate(
                date.getYear(),
                date.getMonthValue(),
                date.getDayOfMonth(),
                DatatypeConstants.FIELD_UNDEFINED
        );
    }
}
