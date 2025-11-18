package com.culturarte.soap.endpoint;

import com.culturarte.exepciones.*;
import com.culturarte.logica.IControlador;
import com.culturarte.logica.datatypes.*;
import com.culturarte.soap.gen.*;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;

@Endpoint
public class UsuarioEndpoint {

    private static final String NAMESPACE = "http://www.culturarte.com/ws/usuarios";

    private final IControlador ctrl;
    private final PropuestasEndpoint propuestasEndpoint;
    private final ColaboracionEndpoint colaboracionEndpoint;

    public UsuarioEndpoint(IControlador ctrl, PropuestasEndpoint propuestasEndpoint, ColaboracionEndpoint colaboracionEndpoint) {
        this.ctrl = ctrl;
        this.propuestasEndpoint = propuestasEndpoint;
        this.colaboracionEndpoint = colaboracionEndpoint;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "getColaboradorRequest")
    @ResponsePayload
    public GetColaboradorResponse getColaborador(@RequestPayload GetColaboradorRequest request) throws Exception {

        GetColaboradorResponse resp = new GetColaboradorResponse();
        ObjectFactory of = new ObjectFactory();

        String nick = request.getNickname();

        DTColaborador dc = ctrl.getDTColaborador(nick);

        if (dc != null) {

            ColaboradorType cdt = of.createColaboradorType();

            cdt.setNickname(dc.getNickname());
            cdt.setNombre(dc.getNombre());
            cdt.setApellido(dc.getApellido());
            cdt.setEmail(dc.getEmail());
            cdt.setImagen(dc.getImagen());

            if (dc.getFechaNacimiento() != null) {
                javax.xml.datatype.XMLGregorianCalendar xgc = javax.xml.datatype.DatatypeFactory.newInstance()
                        .newXMLGregorianCalendarDate(dc.getFechaNacimiento().getYear(), dc.getFechaNacimiento().getMonthValue(), dc.getFechaNacimiento().getDayOfMonth(), javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED);
                cdt.setFechaNacimiento(xgc);
            }

            if (dc.getPropuestas() != null) {
                for (DTPropuesta prop : dc.getPropuestas()) {
                    cdt.getPropuestas().add(propuestasEndpoint.mapToSoapPropuesta(prop));
                }
            }

            if (dc.getColaboraciones() != null) {
                for (DTColaboracion colab : dc.getColaboraciones()) {
                    cdt.getColaboraciones().add(colaboracionEndpoint.mapColaboracion(colab));
                }
            }

            resp.setColaborador(cdt);
        }

        return resp;
    }


    @PayloadRoot(namespace = NAMESPACE, localPart = "getProponenteRequest")
    @ResponsePayload
    public GetProponenteResponse getProponente(@RequestPayload GetProponenteRequest request) throws Exception {

        GetProponenteResponse resp = new GetProponenteResponse();
        ObjectFactory of = new ObjectFactory();

        String nick = request.getNickname();

        DTProponente dt = ctrl.getDTProponente(nick);

        if (dt != null) {

            ProponenteType cdt = of.createProponenteType();

            cdt.setNickname(dt.getNickname());
            cdt.setNombre(dt.getNombre());
            cdt.setApellido(dt.getApellido());
            cdt.setEmail(dt.getEmail());
            cdt.setImagen(dt.getImagen());
            cdt.setDireccion(dt.getDireccion());
            cdt.setBiografia(dt.getBiografia());
            cdt.setLinkWeb(dt.getLinkWeb());

            if (dt.getFechaNacimiento() != null) {
                javax.xml.datatype.XMLGregorianCalendar xgc = javax.xml.datatype.DatatypeFactory.newInstance()
                        .newXMLGregorianCalendarDate(dt.getFechaNacimiento().getYear(), dt.getFechaNacimiento().getMonthValue(), dt.getFechaNacimiento().getDayOfMonth(), javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED);
                cdt.setFechaNacimiento(xgc);
            }

            if (dt.getPropuestas() != null) {
                for (DTPropuesta prop : dt.getPropuestas()) {
                    cdt.getPropuestas().add(propuestasEndpoint.mapToSoapPropuesta(prop));
                }
            }

            resp.setProponente(cdt); // Establecer el DTO en la respuesta
        }

        return resp;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "getUsuarioRequest")
    @ResponsePayload
    public GetUsuarioResponse getUsuario(@RequestPayload GetUsuarioRequest request) throws Exception {
        GetUsuarioResponse resp = new GetUsuarioResponse();
        ObjectFactory of = new ObjectFactory();

        String nick = request.getNickname();
        DTUsuario du = ctrl.getDTUsuario(nick);
        if (du != null) {
            UsuarioType ut = of.createUsuarioType();
            ut.setNickname(du.getNickname());
            ut.setNombre(du.getNombre());
            ut.setApellido(du.getApellido());
            ut.setEmail(du.getEmail());
            ut.setImagen(du.getImagen());
            ut.setTipo(du.getTipo());
            if (du.getFechaNacimiento() != null) {
                javax.xml.datatype.XMLGregorianCalendar xgc = javax.xml.datatype.DatatypeFactory.newInstance()
                        .newXMLGregorianCalendarDate(du.getFechaNacimiento().getYear(), du.getFechaNacimiento().getMonthValue(), du.getFechaNacimiento().getDayOfMonth(), javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED);
                ut.setFechaNacimiento(xgc);
            }

            for (DTUsuario u : du.getUsuariosSeguidos()) {
                UsuarioLightType sub = of.createUsuarioLightType();
                sub.setNickname(u.getNickname());
                sub.setImagen(u.getImagen());
                sub.setTipo(u.getTipo());
                ut.getUsuariosSeguidos().add(sub);
            }

            for (DTUsuario seg : du.getUsuariosSeguidores()) {
                UsuarioLightType sub = of.createUsuarioLightType();
                sub.setNickname(seg.getNickname());
                sub.setTipo(seg.getTipo());
                sub.setImagen(seg.getImagen());
                ut.getUsuariosSeguidores().add(sub);
            }

            for (DTPropuesta prop : du.getPropuestasSeguidas()) {
                ut.getPropuestasSeguidas().add(propuestasEndpoint.mapToSoapPropuesta(prop));
            }


            resp.setUsuario(ut);
        }
        return resp;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "agregarColaboradorRequest")
    @ResponsePayload
    public AgregarColaboradorResponse agregarColaborador(@RequestPayload AgregarColaboradorRequest request) {
        AgregarColaboradorResponse resp = new AgregarColaboradorResponse();
        try {
            ctrl.altaColaborador(request.getNickname(), request.getPassword(), request.getNombre(),
                    request.getApellido(), request.getEmail(),
                    request.getFechaNacimiento().toGregorianCalendar().toZonedDateTime().toLocalDate(),
                    request.getImagen());
            resp.setExito(true);
            resp.setMensaje("Colaborador agregado exitosamente");
        } catch (UsuarioYaExiste | EmailYaExiste e) {
            resp.setExito(false);
            resp.setMensaje(e.getMessage());
        } catch (Exception e) {
            resp.setExito(false);
            resp.setMensaje("Error inesperado");
        }
        return resp;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "agregarProponenteRequest")
    @ResponsePayload
    public AgregarProponenteResponse agregarProponente(@RequestPayload AgregarProponenteRequest request) {
        AgregarProponenteResponse resp = new AgregarProponenteResponse();
        try {
            ctrl.altaProponente(request.getNickname(), request.getPassword(), request.getNombre(),
                    request.getApellido(), request.getEmail(),
                    request.getFechaNacimiento().toGregorianCalendar().toZonedDateTime().toLocalDate(),
                    request.getImagen(), request.getDireccion(), request.getLinkWeb(), request.getBibliografia());
            resp.setExito(true);
            resp.setMensaje("Proponente agregado exitosamente");
        } catch (UsuarioYaExiste | EmailYaExiste e) {
            resp.setExito(false);
            resp.setMensaje(e.getMessage());
        } catch (Exception e) {
            resp.setExito(false);
            resp.setMensaje("Error inesperado");
        }
        return resp;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "seguirUsuarioRequest")
    @ResponsePayload
    public SeguirUsuarioResponse seguirUsuario(@RequestPayload SeguirUsuarioRequest request) {
        SeguirUsuarioResponse resp = new SeguirUsuarioResponse();
        try {
            ctrl.seguirUsuario(request.getNickSeguidor(), request.getNickSeguido());
            resp.setExito(true);
            resp.setMensaje("Usuario seguido correctamente");
        } catch (UsuarioYaSeguido e) {
            resp.setExito(false);
            resp.setMensaje(e.getMessage());
        } catch (Exception e) {
            resp.setExito(false);
            resp.setMensaje("Error inesperado");
        }
        return resp;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "dejarDeSeguirUsuarioRequest")
    @ResponsePayload
    public DejarDeSeguirUsuarioResponse dejarDeSeguirUsuario(@RequestPayload DejarDeSeguirUsuarioRequest request) {
        DejarDeSeguirUsuarioResponse resp = new DejarDeSeguirUsuarioResponse();
        try {
            ctrl.dejarDeSeguirUsuario(request.getNickSeguidor(), request.getNickSeguido());
            resp.setExito(true);
            resp.setMensaje("Usuario dejado de seguir correctamente");
        } catch (UsuarioNoSeguido e) {
            resp.setExito(false);
            resp.setMensaje(e.getMessage());
        } catch (Exception e) {
            resp.setExito(false);
            resp.setMensaje("Error inesperado");
        }
        return resp;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "agregarPropuestaFavoritaRequest")
    @ResponsePayload
    public AgregarPropuestaFavoritaResponse agregarPropuestaFavorita(@RequestPayload AgregarPropuestaFavoritaRequest request) {
        AgregarPropuestaFavoritaResponse resp = new AgregarPropuestaFavoritaResponse();
        try {
            ctrl.agregarPropuestaFavorita(request.getNickname(), request.getTituloPropuesta());
            resp.setExito(true);
            resp.setMensaje("Propuesta agregada a favoritos");
        } catch (Exception e) {
            resp.setExito(false);
            resp.setMensaje(e.getMessage());
        }
        return resp;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "sacarPropuestaFavoritaRequest")
    @ResponsePayload
    public SacarPropuestaFavoritaResponse sacarPropuestaFavorita(@RequestPayload SacarPropuestaFavoritaRequest request) {
        SacarPropuestaFavoritaResponse resp = new SacarPropuestaFavoritaResponse();
        try {
            ctrl.sacarPropuestaFavorita(request.getNickname(), request.getTituloPropuesta());
            resp.setExito(true);
            resp.setMensaje("Propuesta removida de favoritos");
        } catch (Exception e) {
            resp.setExito(false);
            resp.setMensaje(e.getMessage());
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

        // Validar formato de email
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            response.setDisponible(false);
            response.setMensaje("El formato del email no es válido");
            return response;
        }

        try {
            List<com.culturarte.logica.datatypes.DTUsuario> usuarios = ctrl.listarUsuarios();

            if (usuarios == null || usuarios.isEmpty()) {
                response.setDisponible(true);
                response.setMensaje("El email está disponible (no hay usuarios registrados)");
                return response;
            }

            boolean emailOcupado = usuarios.stream()
                    .anyMatch(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email.trim()));

            if (emailOcupado) {
                response.setDisponible(false);
                response.setMensaje("El email '" + email + "' ya está en uso");
            } else {
                response.setDisponible(true);
                response.setMensaje("El email '" + email + "' está disponible");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setDisponible(false);
            response.setMensaje("Error interno al verificar disponibilidad: " + e.getMessage());
        }

        return response;
    }


    @PayloadRoot(namespace = NAMESPACE, localPart = "listarUsuariosRequest")
    @ResponsePayload
    public ListarUsuariosResponse listarUsuarios(@RequestPayload ListarUsuariosRequest request) throws Exception {
        ListarUsuariosResponse resp = new ListarUsuariosResponse();
        ObjectFactory of = new ObjectFactory();

        java.util.List<com.culturarte.logica.datatypes.DTUsuario> usuarios = ctrl.listarUsuarios();
        for (com.culturarte.logica.datatypes.DTUsuario du : usuarios) {
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
            resp.getUsuario().add(ut);
        }

        return resp;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "buscarUsuariosRequest")
    @ResponsePayload
    public BuscarUsuariosResponse buscarUsuarios(@RequestPayload BuscarUsuariosRequest request) throws Exception {
        BuscarUsuariosResponse resp = new BuscarUsuariosResponse();
        ObjectFactory of = new ObjectFactory();

        String nombre = request.getNombre();
        java.util.List<com.culturarte.logica.datatypes.DTUsuario> usuarios = ctrl.buscarUsuarios(nombre);
        for (com.culturarte.logica.datatypes.DTUsuario du : usuarios) {
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
            resp.getUsuario().add(ut);
        }

        return resp;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "verificarPasswordRequest")
    @ResponsePayload
    public VerificarPasswordResponse verificarPassword(@RequestPayload VerificarPasswordRequest request) {
        VerificarPasswordResponse resp = new VerificarPasswordResponse();
        try {
            boolean valido = ctrl.verificarPassword(request.getPassword(), request.getNickname());
            resp.setExito(valido);
            resp.setMensaje(valido ? "Password correcta" : "Password incorrecta");
        } catch (Exception e) {
            resp.setExito(false);
            resp.setMensaje("Error inesperado");
        }
        return resp;
    }
}

