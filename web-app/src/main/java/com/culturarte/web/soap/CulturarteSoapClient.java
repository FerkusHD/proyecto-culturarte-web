package com.culturarte.web.soap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.xml.transform.StringSource;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

@Service
public class CulturarteSoapClient {

    private final WebServiceTemplate webServiceTemplate;

    @Value("${soap.service.url:http://localhost:8081/ws}")
    private String soapServiceUrl;

    public CulturarteSoapClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    /**
     * Llama al endpoint ListarPropuestas y devuelve la respuesta como XML String.
     * Este cliente construye la petición como XML crudo, evitando depender de clases JAXB generadas.
     */
    public String listarPropuestas() {
        String requestXml = "<ListarPropuestasRequest xmlns=\"http://culturarte.com/\"/>";
        Source request = new StringSource(requestXml);

        return webServiceTemplate.sendSourceAndReceive(soapServiceUrl, request, source -> {
            try {
                StringWriter sw = new StringWriter();
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.transform(source, new StreamResult(sw));
                return sw.toString();
            } catch (Exception e) {
                throw new RuntimeException("Error al transformar la respuesta SOAP", e);
            }
        });
    }

    /**
     * Petición ejemplo para obtener un usuario por nickname (getUsuario)
     */
    public String getUsuario(String nickname) {
        String requestXml = String.format(
                "<GetUsuarioRequest xmlns=\"http://culturarte.com/\">%n  <nick>%s</nick>%n</GetUsuarioRequest>",
                escapeXml(nickname)
        );
        Source request = new StringSource(requestXml);

        return webServiceTemplate.sendSourceAndReceive(soapServiceUrl, request, source -> {
            try {
                StringWriter sw = new StringWriter();
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.transform(source, new StreamResult(sw));
                return sw.toString();
            } catch (Exception e) {
                throw new RuntimeException("Error al transformar la respuesta SOAP", e);
            }
        });
    }

    private String escapeXml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
