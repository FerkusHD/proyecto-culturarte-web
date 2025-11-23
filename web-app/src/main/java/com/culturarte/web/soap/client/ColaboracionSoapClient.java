package com.culturarte.web.soap.client;

import com.culturarte.logica.datatypes.DTColaboracion;
import com.culturarte.logica.datatypes.DTPago;
import com.culturarte.logica.enums.TipoRetorno;
import com.culturarte.soap.gen.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

@Service
public class ColaboracionSoapClient {

    private static final Logger logger = LoggerFactory.getLogger(ColaboracionSoapClient.class);

    private final WebServiceTemplate webServiceTemplate;

    @Value("${soap.service.url:}")
    private String soapServiceUrl;

    @Value("${soap.service.host:localhost}")
    private String soapServiceHost;

    @Value("${soap.service.port:8081}")
    private String soapServicePort;

    @Value("${soap.service.context-path:/soap/ws}")
    private String soapServiceContextPath;

    public ColaboracionSoapClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    private String getSoapServiceUrl() {
        if (soapServiceUrl != null && !soapServiceUrl.isEmpty() && !soapServiceUrl.startsWith("${")) {
            return soapServiceUrl;
        }
        return String.format("http://%s:%s%s/colaboraciones", soapServiceHost, soapServicePort, soapServiceContextPath);
    }

    public GetColaboracionResponse getColaboracion(String nickColaborador, String tituloPropuesta) {
        String endpoint = getSoapServiceUrl();
        logger.info("=== INICIO getColaboracion ===");
        logger.info("Obteniendo colaboración: colaborador={}, propuesta={}", nickColaborador, tituloPropuesta);
        logger.debug("Endpoint SOAP: {}", endpoint);
        try {
            GetColaboracionRequest request = new GetColaboracionRequest();
            request.setNickColaborador(nickColaborador);
            request.setTituloPropuesta(tituloPropuesta);

            long startTime = System.currentTimeMillis();
            GetColaboracionResponse response = (GetColaboracionResponse) webServiceTemplate
                    .marshalSendAndReceive(endpoint, request);
            long duration = System.currentTimeMillis() - startTime;

            logger.info("Respuesta SOAP recibida en {} ms", duration);
            if (response == null) {
                logger.warn("Respuesta SOAP null para colaboración");
            } else if (response.getColaboracion() == null) {
                logger.warn("Colaboración null en respuesta");
            } else {
                logger.info("Colaboración obtenida exitosamente");
            }
            logger.debug("=== FIN getColaboracion ===");
            return response;
        } catch (Exception e) {
            logger.error("=== ERROR en getColaboracion ===", e);
            logger.error("Endpoint que falló: {}", endpoint);
            return null;
        }
    }

    /**
     * Obtiene las colaboraciones sin pago de un colaborador
     */
    public ArrayList<DTColaboracion> getColaboracionesSinPago(String nickColaborador) {
        String endpoint = getSoapServiceUrl();
        logger.info("=== INICIO getColaboracionesSinPago ===");
        logger.info("Obteniendo colaboraciones sin pago para: {}", nickColaborador);
        logger.debug("Endpoint SOAP: {}", endpoint);

        try {
            GetColaboracionesSinPagoRequest request = new GetColaboracionesSinPagoRequest();
            request.setNickColaborador(nickColaborador);

            long startTime = System.currentTimeMillis();
            GetColaboracionesSinPagoResponse response = (GetColaboracionesSinPagoResponse) webServiceTemplate
                    .marshalSendAndReceive(endpoint, request);
            long duration = System.currentTimeMillis() - startTime;

            logger.info("Respuesta SOAP recibida en {} ms", duration);

            ArrayList<DTColaboracion> result = new ArrayList<>();
            if (response != null && response.getColaboraciones() != null) {
                for (ColaboracionType ct : response.getColaboraciones()) {
                    result.add(convertColaboracionTypeToDT(ct));
                }
            }

            logger.info("Se obtuvieron {} colaboraciones sin pago", result.size());
            logger.debug("=== FIN getColaboracionesSinPago ===");
            return result;

        } catch (Exception e) {
            logger.error("=== ERROR en getColaboracionesSinPago ===", e);
            logger.error("Endpoint que falló: {}", endpoint);
            return new ArrayList<>();
        }
    }

    /**
     * Obtiene una colaboración específica de un colaborador para una propuesta
     */
    public DTColaboracion getDTColaboracionPropuesta(String nickColaborador, String tituloPropuesta) {
        String endpoint = getSoapServiceUrl();
        logger.info("=== INICIO getDTColaboracionPropuesta ===");
        logger.info("Obteniendo colaboración: colaborador={}, propuesta={}", nickColaborador, tituloPropuesta);

        try {
            GetColaboracionRequest request = new GetColaboracionRequest();
            request.setNickColaborador(nickColaborador);
            request.setTituloPropuesta(tituloPropuesta);

            GetColaboracionResponse response = (GetColaboracionResponse) webServiceTemplate
                    .marshalSendAndReceive(endpoint, request);

            if (response != null && response.getColaboracion() != null) {
                logger.info("Colaboración obtenida exitosamente");
                return convertColaboracionTypeToDT(response.getColaboracion());
            }

            logger.warn("Colaboración no encontrada");
            return null;

        } catch (Exception e) {
            logger.error("=== ERROR en getDTColaboracionPropuesta ===", e);
            return null;
        }
    }

    /**
     * Registra un pago para una colaboración
     */
    public void registrarPago(DTPago pago, String nickColaborador, String tituloPropuesta) throws Exception {
        String endpoint = getSoapServiceUrl();
        logger.info("=== INICIO registrarPago ===");
        logger.info("Registrando pago: colaborador={}, propuesta={}, monto={}",
                nickColaborador, tituloPropuesta, pago.getMonto());

        try {
            RegistrarPagoRequest request = new RegistrarPagoRequest();
            request.setNickColaborador(nickColaborador);
            request.setTituloPropuesta(tituloPropuesta);
            request.setPago(convertDTPagoToPagoType(pago));

            RegistrarPagoResponse response = (RegistrarPagoResponse) webServiceTemplate.marshalSendAndReceive(endpoint,
                    request);

            if (response != null) {
                if (response.isExito()) {
                    logger.info("Pago registrado exitosamente: {}", response.getMensaje());
                } else {
                    logger.error("Error al registrar pago: {}", response.getMensaje());
                    throw new Exception(response.getMensaje());
                }
            } else {
                throw new Exception("Respuesta SOAP nula");
            }

            logger.debug("=== FIN registrarPago ===");

        } catch (Exception e) {
            logger.error("=== ERROR en registrarPago ===", e);
            throw new Exception("Error al registrar pago vía SOAP: " + e.getMessage(), e);
        }
    }

    // Métodos auxiliares de conversión

    private DTColaboracion convertColaboracionTypeToDT(ColaboracionType ct) {
        if (ct == null)
            return null;

        LocalDate fecha = null;
        LocalTime hora = null;
        TipoRetorno tipoRetorno = null;

        if (ct.getFecha() != null) {
            fecha = LocalDate.parse(ct.getFecha());
        }
        if (ct.getHora() != null) {
            hora = LocalTime.parse(ct.getHora());
        }
        if (ct.getTipoRetorno() != null) {
            tipoRetorno = TipoRetorno.valueOf(ct.getTipoRetorno());
        }

        DTColaboracion dt = new DTColaboracion(
                ct.getNickColaborador(),
                ct.getPropuesta() != null ? ct.getPropuesta().getTitulo() : "",
                fecha,
                hora,
                ct.getMonto(),
                tipoRetorno);

        return dt;
    }

    private PagoType convertDTPagoToPagoType(DTPago pago) {
        PagoType pt = new PagoType();
        pt.setMonto(pago.getMonto());
        pt.setFechaPago(pago.getFechaPago() != null ? pago.getFechaPago().toString() : null);
        pt.setHoraPago(pago.getHoraPago() != null ? pago.getHoraPago().toString() : null);
        pt.setTipoPago(pago.getTipoPago() != null ? pago.getTipoPago().name() : null);
        pt.setNombreTitular(pago.getNombreTitular());

        // Campos específicos
        pt.setTipoTarjeta(pago.getTipoTarjeta() != null ? pago.getTipoTarjeta().name() : null);
        pt.setNumeroTarjeta(pago.getNumeroTarjeta());
        pt.setFechaVencimiento(pago.getFechaVencimiento());
        pt.setCvc(pago.getCvc());
        pt.setNombreBanco(pago.getNombreBanco());
        pt.setNumeroCuenta(pago.getNumeroCuenta());

        return pt;
    }
}
