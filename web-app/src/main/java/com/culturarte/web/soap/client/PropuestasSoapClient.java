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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.Collections;
import java.util.List;

@Component
public class PropuestasSoapClient {

    private static final Logger logger = LoggerFactory.getLogger(PropuestasSoapClient.class);

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
            logger.debug("Usando URL SOAP configurada directamente: {}", soapServiceUrl);
            return soapServiceUrl;
        }
        String endpoint = String.format("http://%s:%s%s/propuestas", soapServiceHost, soapServicePort,
                soapServiceContextPath);
        logger.debug("Construyendo endpoint SOAP: {}", endpoint);
        return endpoint;
    }

    public List<PropuestaType> listarPropuestas() {
        String endpoint = getEndpoint();
        logger.info("=== INICIO listarPropuestas ===");
        logger.info("Endpoint SOAP: {}", endpoint);
        logger.info("Host: {}, Port: {}, ContextPath: {}", soapServiceHost, soapServicePort, soapServiceContextPath);

        try {
            ListarPropuestasRequest request = new ListarPropuestasRequest();
            logger.debug("Enviando request SOAP para listar propuestas");

            long startTime = System.currentTimeMillis();
            ListarPropuestasResponse response = (ListarPropuestasResponse) webServiceTemplate
                    .marshalSendAndReceive(endpoint, request);
            long duration = System.currentTimeMillis() - startTime;

            logger.info("Respuesta SOAP recibida en {} ms", duration);

            if (response == null) {
                logger.warn("La respuesta SOAP es null");
                return Collections.emptyList();
            }

            if (response.getPropuesta() == null) {
                logger.warn("La lista de propuestas en la respuesta es null");
                return Collections.emptyList();
            }

            int cantidad = response.getPropuesta().size();
            logger.info("Se obtuvieron {} propuestas exitosamente", cantidad);
            logger.debug("=== FIN listarPropuestas (exitoso) ===");
            return response.getPropuesta();

        } catch (Exception e) {
            logger.error("=== ERROR en listarPropuestas ===", e);
            logger.error("Tipo de excepción: {}", e.getClass().getName());
            logger.error("Mensaje de error: {}", e.getMessage());
            if (e.getCause() != null) {
                logger.error("Causa: {}", e.getCause().getMessage());
            }
            logger.error("Endpoint que falló: {}", endpoint);
            logger.error("Stack trace completo:", e);
            throw new RuntimeException("Error al invocar SOAP para listar propuestas", e);
        }
    }

    public PropuestaType getPropuesta(String titulo) {
        String endpoint = getEndpoint();
        logger.info("Obteniendo propuesta: {}", titulo);
        logger.debug("Endpoint: {}", endpoint);
        try {
            GetPropuestaRequest request = new GetPropuestaRequest();
            request.setTitulo(titulo);
            GetPropuestaResponse response = (GetPropuestaResponse) webServiceTemplate.marshalSendAndReceive(endpoint,
                    request);
            if (response == null) {
                logger.warn("Respuesta null para propuesta: {}", titulo);
                return null;
            }
            if (response.getPropuesta() == null) {
                logger.warn("Propuesta null en respuesta para: {}", titulo);
                return null;
            }
            logger.info("Propuesta obtenida exitosamente: {}", titulo);
            return response.getPropuesta();
        } catch (Exception e) {
            logger.error("Error al obtener propuesta '{}' desde SOAP", titulo, e);
            throw new RuntimeException("Error al obtener propuesta desde SOAP", e);
        }
    }

    public AltaColaboracionResponse altaColaboracion(AltaColaboracionRequest request) {
        logger.info("Alta colaboración - Propuesta: {}, Colaborador: {}, Monto: {}",
                request.getTituloPropuesta(), request.getNickColaborador(), request.getMonto());
        try {
            return (AltaColaboracionResponse) webServiceTemplate.marshalSendAndReceive(getEndpoint(), request);
        } catch (Exception e) {
            logger.error("Error al dar de alta colaboración", e);
            throw new RuntimeException("Error al dar de alta colaboración", e);
        }
    }

    public AgregarComentarioResponse agregarComentario(AgregarComentarioRequest request) {
        logger.info("Agregar comentario - Propuesta: {}, Colaborador: {}",
                request.getTituloPropuesta(), request.getNickColaborador());
        try {
            return (AgregarComentarioResponse) webServiceTemplate.marshalSendAndReceive(getEndpoint(), request);
        } catch (Exception e) {
            logger.error("Error al agregar comentario", e);
            throw new RuntimeException("Error al agregar comentario", e);
        }
    }

    public AgregarFavoritaResponse agregarFavorita(AgregarFavoritaRequest request) {
        logger.info("Agregar favorita - Propuesta: {}, Usuario: {}",
                request.getTituloPropuesta(), request.getNickUsuario());
        try {
            return (AgregarFavoritaResponse) webServiceTemplate.marshalSendAndReceive(getEndpoint(), request);
        } catch (Exception e) {
            logger.error("Error al agregar favorita", e);
            throw new RuntimeException("Error al agregar favorita", e);
        }
    }

    public QuitarFavoritaResponse quitarFavorita(QuitarFavoritaRequest request) {
        logger.info("Quitar favorita - Propuesta: {}, Usuario: {}",
                request.getTituloPropuesta(), request.getNickUsuario());
        try {
            return (QuitarFavoritaResponse) webServiceTemplate.marshalSendAndReceive(getEndpoint(), request);
        } catch (Exception e) {
            logger.error("Error al quitar favorita", e);
            throw new RuntimeException("Error al quitar favorita", e);
        }
    }

    public CancelarPropuestaResponse cancelarPropuesta(CancelarPropuestaRequest request) {
        logger.info("Cancelar propuesta: {}", request.getTituloPropuesta());
        try {
            return (CancelarPropuestaResponse) webServiceTemplate.marshalSendAndReceive(getEndpoint(), request);
        } catch (Exception e) {
            logger.error("Error al cancelar propuesta", e);
            throw new RuntimeException("Error al cancelar propuesta", e);
        }
    }

    public AltaPropuestaResponse altaPropuesta(AltaPropuestaRequest request) {
        logger.info("Alta propuesta: {}", request.getTitulo());
        try {
            return (AltaPropuestaResponse) webServiceTemplate.marshalSendAndReceive(getEndpoint(), request);
        } catch (Exception e) {
            logger.error("Error al dar de alta propuesta", e);
            throw new RuntimeException("Error al dar de alta propuesta", e);
        }
    }

    public ExtenderFinanciacionResponse extenderFinanciacion(ExtenderFinanciacionRequest request) {
        logger.info("Extender financiación - Propuesta: {}, Nueva fecha: {}",
                request.getTituloPropuesta(), request.getNuevaFecha());
        try {
            return (ExtenderFinanciacionResponse) webServiceTemplate.marshalSendAndReceive(getEndpoint(), request);
        } catch (Exception e) {
            logger.error("Error al extender financiación", e);
            throw new RuntimeException("Error al extender financiación", e);
        }
    }

    public List<PropuestaType> obtenerRecomendaciones(String nickColaborador) {
        String endpoint = getEndpoint();
        logger.info("=== INICIO obtenerRecomendaciones ===");
        logger.info("Obteniendo recomendaciones para colaborador: {}", nickColaborador);
        logger.info("Endpoint SOAP: {}", endpoint);

        try {
            com.culturarte.soap.gen.ObtenerRecomendacionesRequest request = new com.culturarte.soap.gen.ObtenerRecomendacionesRequest();
            request.setNickColaborador(nickColaborador);

            logger.debug("Enviando request SOAP para obtener recomendaciones");
            long startTime = System.currentTimeMillis();

            com.culturarte.soap.gen.ObtenerRecomendacionesResponse response = (com.culturarte.soap.gen.ObtenerRecomendacionesResponse) webServiceTemplate
                    .marshalSendAndReceive(endpoint, request);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("Respuesta SOAP recibida en {} ms", duration);

            if (response == null) {
                logger.warn("La respuesta SOAP es null");
                return Collections.emptyList();
            }

            if (response.getPropuesta() == null) {
                logger.warn("La lista de recomendaciones en la respuesta es null");
                return Collections.emptyList();
            }

            int cantidad = response.getPropuesta().size();
            logger.info("Se obtuvieron {} propuestas recomendadas exitosamente", cantidad);
            logger.debug("=== FIN obtenerRecomendaciones (exitoso) ===");
            return response.getPropuesta();

        } catch (Exception e) {
            logger.error("=== ERROR en obtenerRecomendaciones ===", e);
            logger.error("Tipo de excepción: {}", e.getClass().getName());
            logger.error("Mensaje de error: {}", e.getMessage());
            if (e.getCause() != null) {
                logger.error("Causa: {}", e.getCause().getMessage());
            }
            logger.error("Endpoint que falló: {}", endpoint);
            logger.error("Colaborador: {}", nickColaborador);
            // Retornar lista vacía en lugar de lanzar excepción
            logger.warn("Retornando lista vacía debido al error");
            return Collections.emptyList();
        }
    }
}
