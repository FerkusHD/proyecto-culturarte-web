package com.culturarte.soap.endpoint;

import com.culturarte.logica.IControlador;
import com.culturarte.logica.datatypes.DTColaboracion;
import com.culturarte.logica.datatypes.DTColaborador;
import com.culturarte.logica.datatypes.DTPago;
import com.culturarte.logica.enums.TipoPago;
import com.culturarte.logica.enums.TipoTarjeta;
import com.culturarte.soap.gen.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.server.endpoint.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

@Component
@Endpoint
public class ColaboracionEndpoint {

    private static final String NAMESPACE_URI = "http://www.culturarte.com/ws/colaboraciones";

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

        DTColaboracion colab = ctrl.getDTColaboracionPropuesta(request.getNickColaborador(),
                request.getTituloPropuesta());
        if (colab != null) {
            response.setColaboracion(mapColaboracion(colab));
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getColaboracionesSinPagoRequest")
    @ResponsePayload
    public GetColaboracionesSinPagoResponse getColaboracionesSinPago(
            @RequestPayload GetColaboracionesSinPagoRequest request) {

        GetColaboracionesSinPagoResponse response = new GetColaboracionesSinPagoResponse();

        try {
            ArrayList<DTColaboracion> colaboraciones = ctrl.getColaboracionesSinPago(request.getNickColaborador());

            if (colaboraciones != null) {
                colaboraciones.forEach(c -> response.getColaboraciones().add(mapColaboracion(c)));
            }
        } catch (Exception e) {
            // Log error but return empty list
            e.printStackTrace();
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "registrarPagoRequest")
    @ResponsePayload
    public RegistrarPagoResponse registrarPago(@RequestPayload RegistrarPagoRequest request) {
        RegistrarPagoResponse response = new RegistrarPagoResponse();

        try {
            // Convertir PagoType a DTPago
            DTPago pago = mapPagoTypeToDT(request.getPago());

            // Registrar el pago
            ctrl.registrarPago(pago, request.getNickColaborador(), request.getTituloPropuesta());

            response.setExito(true);
            response.setMensaje("Pago registrado exitosamente");
        } catch (Exception e) {
            response.setExito(false);
            response.setMensaje("Error al registrar pago: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "altaColaboracionRequest")
    @ResponsePayload
    public AltaColaboracionResponse altaColaboracion(@RequestPayload AltaColaboracionRequest request) {
        AltaColaboracionResponse response = new AltaColaboracionResponse();
        try {
            DTColaboracion existe = ctrl.getDTColaboracionPropuesta(request.getNickColaborador(),
                    request.getTituloPropuesta());
            if (existe != null) {
                response.setExito(false);
                response.setMensaje("Ya existe una colaboración para este usuario y propuesta");
                return response;
            }

            ctrl.altaColaboracion(
                    request.getMonto(),
                    LocalDate.now(),
                    LocalTime.now(),
                    com.culturarte.logica.enums.TipoRetorno.valueOf(request.getTipoRetorno().toUpperCase()),
                    request.getTituloPropuesta(),
                    request.getNickColaborador());
            response.setExito(true);
            response.setMensaje("Colaboración registrada correctamente");
        } catch (com.culturarte.exepciones.ColaboracionYaExiste e) {
            response.setExito(false);
            response.setMensaje("Colaboración ya existe");
        } catch (Exception e) {
            response.setExito(false);
            response.setMensaje("Error al registrar colaboración: " + e.getMessage());
        }
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
        // Asegurar que pagada nunca sea null
        dto.setPagada(Boolean.valueOf(source.isPagada()));
        return dto;
    }

    private DTPago mapPagoTypeToDT(PagoType pagoType) {
        DTPago pago = new DTPago();
        pago.setMonto(pagoType.getMonto());

        // Parsear fecha y hora
        if (pagoType.getFechaPago() != null) {
            pago.setFechaPago(LocalDate.parse(pagoType.getFechaPago()));
        }
        if (pagoType.getHoraPago() != null) {
            pago.setHoraPago(LocalTime.parse(pagoType.getHoraPago()));
        }

        // Tipo de pago
        if (pagoType.getTipoPago() != null) {
            pago.setTipoPago(TipoPago.valueOf(pagoType.getTipoPago()));
        }

        pago.setNombreTitular(pagoType.getNombreTitular());

        // Campos específicos según tipo de pago
        if (pagoType.getTipoTarjeta() != null) {
            pago.setTipoTarjeta(TipoTarjeta.valueOf(pagoType.getTipoTarjeta()));
        }
        pago.setNumeroTarjeta(pagoType.getNumeroTarjeta());
        pago.setFechaVencimiento(pagoType.getFechaVencimiento());
        pago.setCvc(pagoType.getCvc());
        pago.setNombreBanco(pagoType.getNombreBanco());
        pago.setNumeroCuenta(pagoType.getNumeroCuenta());

        return pago;
    }
}
