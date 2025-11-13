package com.culturarte.web.soap.client;

import com.culturarte.soap.gen.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public class UsuarioSoapClient {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioSoapClient.class);

    private final WebServiceTemplate webServiceTemplate;

    @Value("${soap.service.url:}")
    private String soapServiceUrl;

    @Value("${soap.service.host:localhost}")
    private String soapServiceHost;

    @Value("${soap.service.port:8081}")
    private String soapServicePort;

    @Value("${soap.service.context-path:/soap/ws}")
    private String soapServiceContextPath;

    public UsuarioSoapClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    private String getSoapServiceUrl() {
        if (soapServiceUrl != null && !soapServiceUrl.isEmpty() && !soapServiceUrl.startsWith("${")) {
            return soapServiceUrl;
        }
        return String.format("http://%s:%s%s/usuarios", soapServiceHost, soapServicePort, soapServiceContextPath);
    }

    // --- 📦 Métodos SOAP disponibles ---

    public GetUsuarioResponse getUsuario(String nickname) {
        try {
            GetUsuarioRequest request = new GetUsuarioRequest();
            request.setNickname(nickname);
            return (GetUsuarioResponse) webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), request);
        } catch (Exception e) {
            logger.error("Error al obtener usuario vía SOAP: {}", e.getMessage());
            return new GetUsuarioResponse();
        }
    }

    public VerificarPasswordResponse verificarPassword(String password, String nickname) {
        try {
            VerificarPasswordRequest req = new VerificarPasswordRequest();
            req.setPassword(password);
            req.setNickname(nickname);
            return (VerificarPasswordResponse) webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), req);
        } catch (Exception e) {
            logger.error("Error al verificar password vía SOAP: {}", e.getMessage());
            VerificarPasswordResponse r = new VerificarPasswordResponse();
            r.setExito(false);
            r.setMensaje("Error de conexión SOAP");
            return r;
        }
    }

    public VerificarEmailResponse verificarEmail(String email) {
        try {
            VerificarEmailRequest req = new VerificarEmailRequest();
            req.setEmail(email);
            return (VerificarEmailResponse) webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), req);
        } catch (Exception e) {
            logger.error("Error al verificar email vía SOAP: {}", e.getMessage());
            VerificarEmailResponse r = new VerificarEmailResponse();
            r.setDisponible(false);
            r.setMensaje("Error de conexión SOAP");
            return r;
        }
    }

    public VerificarNicknameResponse verificarNickname(String nickname) {
        try {
            VerificarNicknameRequest req = new VerificarNicknameRequest();
            req.setNickname(nickname);
            return (VerificarNicknameResponse) webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), req);
        } catch (Exception e) {
            logger.error("Error al verificar nickname vía SOAP: {}", e.getMessage());
            VerificarNicknameResponse r = new VerificarNicknameResponse();
            r.setDisponible(false);
            r.setMensaje("Error de conexión SOAP");
            return r;
        }
    }

    public ListarUsuariosResponse listarUsuarios() {
        try {
            ListarUsuariosRequest req = new ListarUsuariosRequest();
            return (ListarUsuariosResponse) webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), req);
        } catch (Exception e) {
            logger.error("Error al listar usuarios vía SOAP: {}", e.getMessage());
            return new ListarUsuariosResponse();
        }
    }

    public BuscarUsuariosResponse buscarUsuarios(String nombre) {
        try {
            BuscarUsuariosRequest req = new BuscarUsuariosRequest();
            req.setNombre(nombre);
            return (BuscarUsuariosResponse) webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), req);
        } catch (Exception e) {
            logger.error("Error al buscar usuarios vía SOAP: {}", e.getMessage());
            return new BuscarUsuariosResponse();
        }
    }
}
