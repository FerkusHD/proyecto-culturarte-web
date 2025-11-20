package com.culturarte.web.soap.client;

import com.culturarte.logica.datatypes.DTColaborador;
import com.culturarte.soap.gen.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Collections;
import java.util.List;

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

    public UsuarioType getUsuario(String nickname) {
        logger.debug("Obteniendo usuario vía SOAP: {}", nickname);
        try {
            GetUsuarioRequest request = new GetUsuarioRequest();
            request.setNickname(nickname);
            GetUsuarioResponse response = (GetUsuarioResponse) webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), request);
            if (response != null && response.getUsuario() != null) {
                logger.debug("Usuario obtenido exitosamente: {}", nickname);
            } else {
                logger.warn("Usuario no encontrado: {}", nickname);
            }
            return response.getUsuario();
        } catch (Exception e) {
            logger.error("Error al obtener usuario vía SOAP: {}", nickname, e);
            return null;
        }
    }

    public void agregarProponente(String nickname, String password, String nombre, String apellido, String email,
                                  XMLGregorianCalendar fechaNacimiento, String imagen, String direccion,
                                  String linkWeb, String bibliografia) {
        AgregarProponenteRequest request = new AgregarProponenteRequest();
        request.setNickname(nickname);
        request.setPassword(password);
        request.setNombre(nombre);
        request.setApellido(apellido);
        request.setEmail(email);
        request.setFechaNacimiento(fechaNacimiento);
        request.setImagen(imagen);
        request.setDireccion(direccion);
        request.setLinkWeb(linkWeb);
        request.setBibliografia(bibliografia);
        webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), request);
    }

    public void agregarColaborador(String nickname, String password, String nombre, String apellido, String email,
                                   XMLGregorianCalendar fechaNacimiento, String imagen) {
        AgregarColaboradorRequest request = new AgregarColaboradorRequest();
        request.setNickname(nickname);
        request.setPassword(password);
        request.setNombre(nombre);
        request.setApellido(apellido);
        request.setEmail(email);
        request.setFechaNacimiento(fechaNacimiento);
        request.setImagen(imagen);
        webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), request);
    }

    public VerificarPasswordResponse verificarPassword(String nickname, String password) {
        logger.debug("Verificando password vía SOAP para: {}", nickname);
        try {
            VerificarPasswordRequest req = new VerificarPasswordRequest();
            req.setNickname(nickname);
            req.setPassword(password);
            VerificarPasswordResponse response = (VerificarPasswordResponse) webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), req);
            if (response != null) {
                logger.debug("Verificación de password completada: exito={}", response.isExito());
            }
            return response;
        } catch (Exception e) {
            logger.error("Error al verificar password vía SOAP para: {}", nickname, e);
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

    public List<UsuarioType> listarUsuarios() {
        try {
            ListarUsuariosRequest req = new ListarUsuariosRequest();
            ListarUsuariosResponse response = (ListarUsuariosResponse)
                    webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), req);
            if (response == null || response.getUsuario() == null) {
                return Collections.emptyList();
            }
            return response.getUsuario();
        } catch (Exception e) {
            logger.error("Error al listar usuarios vía SOAP: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<UsuarioType> buscarUsuarios(String nombre) {
        try {
            BuscarUsuariosRequest req = new BuscarUsuariosRequest();
            req.setNombre(nombre);
            BuscarUsuariosResponse response = (BuscarUsuariosResponse)
                    webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), req);
            if (response == null || response.getUsuario() == null) {
                return Collections.emptyList();
            }
            return response.getUsuario();
        } catch (Exception e) {
            logger.error("Error al buscar usuarios vía SOAP: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public void seguirUsuario(String nickSeguidor, String nickSeguido) {
        SeguirUsuarioRequest req = new SeguirUsuarioRequest();
        req.setNickSeguidor(nickSeguidor);
        req.setNickSeguido(nickSeguido);
        webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), req);
    }

    public void dejarDeSeguirUsuario(String nickSeguidor, String nickSeguido) {
        DejarDeSeguirUsuarioRequest req = new DejarDeSeguirUsuarioRequest();
        req.setNickSeguidor(nickSeguidor);
        req.setNickSeguido(nickSeguido);
        webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), req);
    }

    public void agregarPropuestaFavorita(String nickname, String titulo) {
        AgregarPropuestaFavoritaRequest req = new AgregarPropuestaFavoritaRequest();
        req.setNickname(nickname);
        req.setTituloPropuesta(titulo);
        webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), req);
    }

    public void sacarPropuestaFavorita(String nickname, String titulo) {
        SacarPropuestaFavoritaRequest req = new SacarPropuestaFavoritaRequest();
        req.setNickname(nickname);
        req.setTituloPropuesta(titulo);
        webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), req);
    }

    public ColaboradorType getDTColaborador(String nick) {
        logger.debug("Obteniendo colaborador vía SOAP: {}", nick);
        try {
            GetColaboradorRequest request = new GetColaboradorRequest();
            request.setNickname(nick);
            GetColaboradorResponse response = (GetColaboradorResponse) webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), request);
            if (response != null && response.getColaborador() != null) {
                logger.debug("colaborador obtenido exitosamente: {}", nick);
            } else {
                logger.warn("colaborador no encontrado: {}", nick);
            }
            return response.getColaborador();
        } catch (Exception e) {
            logger.error("Error al obtener usuario vía SOAP: {}", nick, e);
            return null;
        }
    }

    public ProponenteType getProponente(String nick) {
        logger.debug("Obteniendo proponente vía SOAP: {}", nick);
        try {
            GetProponenteRequest request = new GetProponenteRequest();
            request.setNickname(nick);
            GetProponenteResponse response = (GetProponenteResponse) webServiceTemplate.marshalSendAndReceive(getSoapServiceUrl(), request);
            if (response != null && response.getProponente() != null) {
                logger.debug("proponente obtenido exitosamente: {}", nick);
            } else {
                logger.warn("proponente no encontrado: {}", nick);
            }
            return response.getProponente();
        } catch (Exception e) {
            logger.error("Error al obtener usuario vía SOAP: {}", nick, e);
            return null;
        }
    }

    public EliminarProponenteResponse eliminarProponente(String nickname) {
        logger.debug("Eliminando proponente vía SOAP: {}", nickname);

        try {
            EliminarProponenteRequest req = new EliminarProponenteRequest();
            req.setNickname(nickname);

            EliminarProponenteResponse resp =
                    (EliminarProponenteResponse) webServiceTemplate.marshalSendAndReceive(
                            getSoapServiceUrl(), req
                    );

            return resp;

        } catch (Exception e) {
            logger.error("Error al eliminar proponente vía SOAP: {}", nickname, e);

            EliminarProponenteResponse r = new EliminarProponenteResponse();
            r.setExito(false);
            r.setMensaje("Error de conexión SOAP");
            return r;
        }
    }



}
