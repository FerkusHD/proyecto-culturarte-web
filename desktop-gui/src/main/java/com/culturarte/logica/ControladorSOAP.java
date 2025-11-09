package com.culturarte.logica;

import com.culturarte.soapclient.CulturarteService;
import com.culturarte.soapclient.CulturartePort;

public class ControladorSOAP implements IControlador {

    private final CulturartePort port;

    public ControladorSOAP() {
        CulturarteService service = new CulturarteService();
        this.port = service.getCulturartePort();
    }

    @Override
    public void buscarUsuarios(String nombre) {
        System.out.println("Llamando al servicio SOAP para buscar usuario: " + nombre);
        var respuesta = port.buscarUsuarios(nombre);
        System.out.println("Respuesta SOAP: " + respuesta);
    }
}
