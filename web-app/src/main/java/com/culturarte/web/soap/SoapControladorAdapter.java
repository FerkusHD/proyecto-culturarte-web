package com.culturarte.web.soap;

import com.culturarte.exepciones.*;
import com.culturarte.logica.IControlador;
import com.culturarte.logica.datatypes.*;
import com.culturarte.logica.enums.*;
import com.culturarte.soap.gen.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.swing.tree.DefaultTreeModel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptador SOAP que implementa IControlador usando el servicio SOAP.
 * Los métodos disponibles en SOAP se delegan al servicio, los demás lanzan UnsupportedOperationException.
 */
@Service
public class SoapControladorAdapter implements IControlador {

    private final WebServiceTemplate webServiceTemplate;
    
    @Value("${soap.service.url:http://localhost:8081/soap/ws}")
    private String soapServiceUrl;

    public SoapControladorAdapter(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    // ========== Métodos implementados vía SOAP ==========

    @Override
    public List<String> listarCategoriasWeb() {
        try {
            ObjectFactory of = new ObjectFactory();
            jakarta.xml.bind.JAXBElement<Object> request = of.createGetCategoriasRequest(new Object());
            GetCategoriasResponse response = (GetCategoriasResponse) webServiceTemplate.marshalSendAndReceive(
                soapServiceUrl + "/categorias", request);
            return response != null ? response.getCategoria() : new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener categorías desde SOAP", e);
        }
    }

    @Override
    public List<String> listarCategoriasWebCompletas() {
        return listarCategoriasWeb();
    }

    @Override
    public ArrayList<DTPropuesta> getDTPropuestasWeb() {
        try {
            ObjectFactory of = new ObjectFactory();
            jakarta.xml.bind.JAXBElement<Object> request = of.createListarPropuestasRequest(new Object());
            ListarPropuestasResponse response = (ListarPropuestasResponse) webServiceTemplate.marshalSendAndReceive(
                soapServiceUrl + "/propuestas", request);
            
            ArrayList<DTPropuesta> result = new ArrayList<>();
            if (response != null && response.getPropuesta() != null) {
                for (PropuestaType pt : response.getPropuesta()) {
                    DTPropuesta dtp = convertPropuestaTypeToDT(pt);
                    result.add(dtp);
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener propuestas desde SOAP", e);
        }
    }

    @Override
    public DTPropuesta getDTPropuesta(String titulo) {
        try {
            GetPropuestaRequest request = new GetPropuestaRequest();
            request.setTitulo(titulo);
            GetPropuestaResponse response = (GetPropuestaResponse) webServiceTemplate.marshalSendAndReceive(
                soapServiceUrl + "/propuestas", request);
            
            if (response.getPropuesta() != null) {
                return convertPropuestaTypeToDT(response.getPropuesta());
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener propuesta desde SOAP", e);
        }
    }

    @Override
    public DTUsuario getDTUsuario(String nickname) {
        try {
            GetUsuarioRequest request = new GetUsuarioRequest();
            request.setNickname(nickname);
            GetUsuarioResponse response = (GetUsuarioResponse) webServiceTemplate.marshalSendAndReceive(
                soapServiceUrl + "/usuarios", request);
            
            if (response.getUsuario() != null) {
                return convertUsuarioTypeToDT(response.getUsuario());
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener usuario desde SOAP", e);
        }
    }

    // ========== Métodos no implementados en SOAP (lanzan excepción) ==========

    @Override
    public void cargarDatosPrueba() throws CargaFallida {
        throw new UnsupportedOperationException("cargarDatosPrueba no está disponible vía SOAP");
    }

    @Override
    public void altaColaborador(String nickname, String password, String nombre, String apellido, String email, LocalDate fechaNacimiento, String imagen) throws UsuarioYaExiste, EmailYaExiste {
        throw new UnsupportedOperationException("altaColaborador no está disponible vía SOAP");
    }

    @Override
    public void altaProponente(String nickname, String password, String nombre, String apellido, String email, LocalDate fechaNacimiento, String imagen, String direccion, String linkWeb, String bibliografia) throws UsuarioYaExiste, EmailYaExiste {
        throw new UnsupportedOperationException("altaProponente no está disponible vía SOAP");
    }

    @Override
    public ArrayList<String> getNomProponentes() {
        throw new UnsupportedOperationException("getNomProponentes no está disponible vía SOAP");
    }

    @Override
    public DTProponente getDTProponente(String nickname) {
        throw new UnsupportedOperationException("getDTProponente no está disponible vía SOAP");
    }

    @Override
    public void altaCategoria(String nombre, String catPadre) throws CategoriaYaExiste {
        throw new UnsupportedOperationException("altaCategoria no está disponible vía SOAP");
    }

    @Override
    public DefaultTreeModel listarCategorias() {
        throw new UnsupportedOperationException("listarCategorias no está disponible vía SOAP");
    }

    @Override
    public void altaPropuesta(String titulo, String descripcion, String lugar, LocalDate fechaPrevista, Float precioEntrada, Float montoNecesario, EnumSet<TipoRetorno> tipoRetornos, String imagen, String proponente, String categoria, LocalDate fechaActual, LocalTime horaActual) throws PropuestaYaExiste {
        throw new UnsupportedOperationException("altaPropuesta no está disponible vía SOAP");
    }

    @Override
    public ArrayList<String> getNickColaboradores() {
        throw new UnsupportedOperationException("getNickColaboradores no está disponible vía SOAP");
    }

    @Override
    public ArrayList<String> getNomColaboradores() {
        throw new UnsupportedOperationException("getNomColaboradores no está disponible vía SOAP");
    }

    @Override
    public DTColaborador getDTColaborador(String nickname) {
        throw new UnsupportedOperationException("getDTColaborador no está disponible vía SOAP");
    }

    @Override
    public ArrayList<DTPropuesta> getDTPropuestas() {
        return getDTPropuestasWeb();
    }

    @Override
    public ArrayList<String> getTituloPropuestas() {
        ArrayList<DTPropuesta> propuestas = getDTPropuestasWeb();
        return propuestas.stream()
            .map(DTPropuesta::getTitulo)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<String> getTituloPropuestasPorEstado(TipoEstado estado) {
        ArrayList<DTPropuesta> propuestas = getDTPropuestasWeb();
        return propuestas.stream()
            .filter(p -> p.getEstadoActual() != null && p.getEstadoActual().equals(estado))
            .map(DTPropuesta::getTitulo)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void altaColaboracion(float monto, LocalDate fecha, LocalTime hora, TipoRetorno tipoRetorno, String tituloPropuesta, String nickColaborador) throws ColaboracionYaExiste {
        throw new UnsupportedOperationException("altaColaboracion no está disponible vía SOAP");
    }

    @Override
    public String getNickProponente(String tituloPropuesta) {
        DTPropuesta propuesta = getDTPropuesta(tituloPropuesta);
        return propuesta != null ? propuesta.getProponente() : null;
    }

    @Override
    public ArrayList<String> getNickUsuarios() {
        throw new UnsupportedOperationException("getNickUsuarios no está disponible vía SOAP");
    }

    @Override
    public void seguirUsuario(String nickSeguidor, String nickSeguido) throws UsuarioYaSeguido {
        throw new UnsupportedOperationException("seguirUsuario no está disponible vía SOAP");
    }

    @Override
    public void dejarDeSeguirUsuario(String nickSeguidor, String nickSeguido) throws UsuarioNoSeguido {
        throw new UnsupportedOperationException("dejarDeSeguirUsuario no está disponible vía SOAP");
    }

    @Override
    public ArrayList<DTColaboracion> getDTColaboracionesPropuestas(String nickColab) {
        throw new UnsupportedOperationException("getDTColaboracionesPropuestas no está disponible vía SOAP");
    }

    @Override
    public DTColaboracion getDTColaboracionPropuesta(String nickColab, String tituloProp) {
        throw new UnsupportedOperationException("getDTColaboracionPropuesta no está disponible vía SOAP");
    }

    @Override
    public void cancelarColaboracionPropuesta(String tituloPropuesta, String nickColaborador) {
        throw new UnsupportedOperationException("cancelarColaboracionPropuesta no está disponible vía SOAP");
    }

    @Override
    public ArrayList<DTColaboracion> getDTColaboraciones() {
        throw new UnsupportedOperationException("getDTColaboraciones no está disponible vía SOAP");
    }

    @Override
    public void nuevoEstadoPropuesta(String propuesta, TipoEstado estado, LocalDate fecha, LocalTime hora) {
        throw new UnsupportedOperationException("nuevoEstadoPropuesta no está disponible vía SOAP");
    }

    @Override
    public void modificarPropuesta(String titulo, String descripcion, String lugar, LocalDate fechaPrevista, Float precioEntrada, Float montoNecesario, String imagen, String proponente, String categoria, String nuevoEstado) throws DatosIncorrectos {
        throw new UnsupportedOperationException("modificarPropuesta no está disponible vía SOAP");
    }

    @Override
    public List<DTPropuesta> buscarPropuestas(String texto) {
        ArrayList<DTPropuesta> todas = getDTPropuestasWeb();
        String textoLower = texto.toLowerCase();
        return todas.stream()
            .filter(p -> p.getTitulo().toLowerCase().contains(textoLower) ||
                        (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(textoLower)))
            .collect(Collectors.toList());
    }

    @Override
    public String[] getTiposRetorno() {
        throw new UnsupportedOperationException("getTiposRetorno no está disponible vía SOAP");
    }

    @Override
    public void extenderFinanciacion(String tituloPropuesta, LocalDate nuevaFecha) {
        throw new UnsupportedOperationException("extenderFinanciacion no está disponible vía SOAP");
    }

    @Override
    public void agregarComentario(String texto, String nickColaborador, String tituloPropuesta) {
        throw new UnsupportedOperationException("agregarComentario no está disponible vía SOAP");
    }

    @Override
    public List<DTUsuario> buscarUsuarios(String nombre) {
        throw new UnsupportedOperationException("buscarUsuarios no está disponible vía SOAP");
    }

    @Override
    public void agregarPropuestaFavorita(String nickname, String tituloPropuesta) {
        throw new UnsupportedOperationException("agregarPropuestaFavorita no está disponible vía SOAP");
    }

    @Override
    public void sacarPropuestaFavorita(String nickname, String tituloPropuesta) {
        throw new UnsupportedOperationException("sacarPropuestaFavorita no está disponible vía SOAP");
    }

    @Override
    public boolean colaboradorPuedeComentar(String colaborador, String tituloPropuesta) {
        throw new UnsupportedOperationException("colaboradorPuedeComentar no está disponible vía SOAP");
    }

    @Override
    public boolean verificarPassword(String password, String nick) {
        throw new UnsupportedOperationException("verificarPassword no está disponible vía SOAP");
    }

    @Override
    public ArrayList<DTUsuario> listarUsuarios() {
        throw new UnsupportedOperationException("listarUsuarios no está disponible vía SOAP");
    }

    // ========== Métodos auxiliares de conversión ==========

    private DTPropuesta convertPropuestaTypeToDT(PropuestaType pt) {
        TipoEstado estado = null;
        if (pt.getEstado() != null) {
            try {
                estado = TipoEstado.valueOf(pt.getEstado());
            } catch (IllegalArgumentException e) {
                // Si no se puede convertir, se deja null
            }
        }
        
        LocalDate fechaPrevista = null;
        if (pt.getFechaPrevista() != null) {
            fechaPrevista = LocalDate.of(
                pt.getFechaPrevista().getYear(),
                pt.getFechaPrevista().getMonth(),
                pt.getFechaPrevista().getDay()
            );
        }
        
        float montoNecesario = pt.getMontoNecesario() != null ? pt.getMontoNecesario() : 0.0f;
        float montoRecaudado = pt.getMontoRecaudado() != null ? pt.getMontoRecaudado() : 0.0f;
        int cantColaboradores = pt.getCantColaboradores() != null ? pt.getCantColaboradores() : 0;
        
        DTPropuesta dtp = new DTPropuesta(
            pt.getTitulo(),
            pt.getDescripcion() != null ? pt.getDescripcion() : "",
            estado,
            cantColaboradores,
            montoRecaudado,
            montoNecesario,
            fechaPrevista,
            pt.getImagen() != null ? pt.getImagen() : "",
            pt.getCategoria() != null ? pt.getCategoria() : "",
            pt.getProponente() != null ? pt.getProponente() : ""
        );
        return dtp;
    }

    private DTUsuario convertUsuarioTypeToDT(UsuarioType ut) {
        DTUsuario dtu = new DTUsuario();
        dtu.setNickname(ut.getNickname());
        if (ut.getNombre() != null) {
            dtu.setNombre(ut.getNombre());
        }
        if (ut.getApellido() != null) {
            dtu.setApellido(ut.getApellido());
        }
        if (ut.getEmail() != null) {
            dtu.setEmail(ut.getEmail());
        }
        if (ut.getImagen() != null) {
            dtu.setImagen(ut.getImagen());
        }
        if (ut.getFechaNacimiento() != null) {
            dtu.setFechaNacimiento(LocalDate.of(
                ut.getFechaNacimiento().getYear(),
                ut.getFechaNacimiento().getMonth(),
                ut.getFechaNacimiento().getDay()
            ));
        }
        if (ut.getTipo() != null) {
            dtu.setTipo(ut.getTipo());
        }
        return dtu;
    }
}

