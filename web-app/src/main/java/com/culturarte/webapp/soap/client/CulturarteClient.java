package com.culturarte.webapp.soap.client;

import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.stereotype.Service;
import com.culturarte.soap.gen.*;
import java.util.List;
import java.util.ArrayList;
import com.culturarte.logica.datatypes.DTPropuesta;
import java.time.LocalDate;
import javax.xml.datatype.XMLGregorianCalendar;

@Service
public class CulturarteClient {

    private final WebServiceTemplate webServiceTemplate;

    public CulturarteClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    public List<DTPropuesta> getPropuestas() {
        GetPropuestasRequest request = new GetPropuestasRequest();
        GetPropuestasResponse response = (GetPropuestasResponse) webServiceTemplate.marshalSendAndReceive(request);
        List<DTPropuesta> propuestas = new ArrayList<>();
        for (PropuestaType p : response.getPropuestas()) {
            // Convertir PropuestaType a DTPropuesta usando el constructor mínimo
            DTPropuesta prop = new DTPropuesta(
                p.getTitulo(),
                p.getDescripcion(),
                "", // lugar (no disponible en PropuestaType)
                p.getFechaPrevista() != null ? convertToLocalDate(p.getFechaPrevista()) : null,
                0f, // precioEntrada (no disponible en PropuestaType)
                p.getMontoNecesario() != null ? p.getMontoNecesario() : 0f
            );
            propuestas.add(prop);
        }
        return propuestas;
    }

    private LocalDate convertToLocalDate(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }
        return LocalDate.of(
            calendar.getYear(),
            calendar.getMonth(),
            calendar.getDay()
        );
    }
}