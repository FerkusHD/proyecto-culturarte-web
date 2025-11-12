package com.culturarte.soap.endpoint;

import com.culturarte.logica.IControlador;
import com.culturarte.soap.gen.GetUsuarioRequest;
import com.culturarte.soap.gen.GetUsuarioResponse;
import com.culturarte.soap.gen.UsuarioType;
import com.culturarte.soap.gen.ObjectFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class UsuarioEndpoint {

    private static final String NAMESPACE = "http://www.culturarte.com/ws/usuarios";

    private final IControlador ctrl;

    public UsuarioEndpoint(IControlador ctrl) {
        this.ctrl = ctrl;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "getUsuarioRequest")
    @ResponsePayload
    public GetUsuarioResponse getUsuario(@RequestPayload GetUsuarioRequest request) throws Exception {
        GetUsuarioResponse resp = new GetUsuarioResponse();
        ObjectFactory of = new ObjectFactory();

        String nick = request.getNickname();
        com.culturarte.logica.datatypes.DTUsuario du = ctrl.getDTUsuario(nick);
        if (du != null) {
            UsuarioType ut = of.createUsuarioType();
            ut.setNickname(du.getNickname());
            ut.setNombre(du.getNombre());
            ut.setApellido(du.getApellido());
            ut.setEmail(du.getEmail());
            ut.setImagen(du.getImagen());
            if (du.getFechaNacimiento() != null) {
                javax.xml.datatype.XMLGregorianCalendar xgc = javax.xml.datatype.DatatypeFactory.newInstance()
                        .newXMLGregorianCalendarDate(du.getFechaNacimiento().getYear(), du.getFechaNacimiento().getMonthValue(), du.getFechaNacimiento().getDayOfMonth(), javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED);
                ut.setFechaNacimiento(xgc);
            }
            ut.setTipo(du.getTipo());
            resp.setUsuario(ut);
        }
        return resp;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "verificarNicknameRequest")
    @ResponsePayload
    public com.culturarte.soap.gen.VerificarNicknameResponse verificarNickname(
            @RequestPayload com.culturarte.soap.gen.VerificarNicknameRequest request) {
        
        com.culturarte.soap.gen.VerificarNicknameResponse response = 
            new com.culturarte.soap.gen.VerificarNicknameResponse();
        
        String nickname = request.getNickname();
        
        if (nickname == null || nickname.trim().isEmpty()) {
            response.setDisponible(false);
            response.setMensaje("El nickname no puede estar vacío");
            return response;
        }

        try {
            com.culturarte.logica.datatypes.DTUsuario usuario = ctrl.getDTUsuario(nickname.trim());
            if (usuario != null) {
                response.setDisponible(false);
                response.setMensaje("El nickname '" + nickname + "' ya está en uso");
            } else {
                response.setDisponible(true);
                response.setMensaje("El nickname '" + nickname + "' está disponible");
            }
        } catch (Exception e) {
            response.setDisponible(false);
            response.setMensaje("Error al verificar disponibilidad");
        }
        
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "verificarEmailRequest")
    @ResponsePayload
    public com.culturarte.soap.gen.VerificarEmailResponse verificarEmail(
            @RequestPayload com.culturarte.soap.gen.VerificarEmailRequest request) {
        
        com.culturarte.soap.gen.VerificarEmailResponse response = 
            new com.culturarte.soap.gen.VerificarEmailResponse();
        
        String email = request.getEmail();
        
        if (email == null || email.trim().isEmpty()) {
            response.setDisponible(false);
            response.setMensaje("El email no puede estar vacío");
            return response;
        }

        // Validar formato básico de email
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            response.setDisponible(false);
            response.setMensaje("El formato del email no es válido");
            return response;
        }

        try {
            // Buscar usuarios y verificar si alguno tiene ese email
            java.util.ArrayList<com.culturarte.logica.datatypes.DTUsuario> usuarios = ctrl.listarUsuarios();
            if (usuarios != null) {
                for (com.culturarte.logica.datatypes.DTUsuario usuario : usuarios) {
                    if (usuario.getEmail() != null && usuario.getEmail().equalsIgnoreCase(email.trim())) {
                        response.setDisponible(false);
                        response.setMensaje("El email '" + email + "' ya está en uso");
                        return response;
                    }
                }
            }
            response.setDisponible(true);
            response.setMensaje("El email '" + email + "' está disponible");
        } catch (Exception e) {
            response.setDisponible(false);
            response.setMensaje("Error al verificar disponibilidad");
        }
        
        return response;
    }
}

