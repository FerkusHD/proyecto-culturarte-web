package com.culturarte.web.soap;

import com.culturarte.exepciones.CargaFallida;
import com.culturarte.exepciones.CategoriaYaExiste;
import com.culturarte.exepciones.ColaboracionYaExiste;
import com.culturarte.exepciones.DatosIncorrectos;
import com.culturarte.exepciones.EmailYaExiste;
import com.culturarte.exepciones.PropuestaYaExiste;
import com.culturarte.exepciones.UsuarioNoSeguido;
import com.culturarte.exepciones.UsuarioYaExiste;
import com.culturarte.exepciones.UsuarioYaSeguido;
import com.culturarte.logica.IControlador;
import com.culturarte.logica.datatypes.*;
import com.culturarte.logica.enums.TipoEstado;
import com.culturarte.logica.enums.TipoRetorno;
import com.culturarte.soap.gen.BuscarUsuariosRequest;
import com.culturarte.soap.gen.BuscarUsuariosResponse;
import com.culturarte.soap.gen.CategoriaType;
import com.culturarte.soap.gen.GetCategoriasRequest;
import com.culturarte.soap.gen.GetCategoriasResponse;
import com.culturarte.soap.gen.GetPropuestaRequest;
import com.culturarte.soap.gen.GetPropuestaResponse;
import com.culturarte.soap.gen.GetUsuarioRequest;
import com.culturarte.soap.gen.GetUsuarioResponse;
import com.culturarte.soap.gen.ListarPropuestasRequest;
import com.culturarte.soap.gen.ListarPropuestasResponse;
import com.culturarte.soap.gen.ListarUsuariosRequest;
import com.culturarte.soap.gen.ListarUsuariosResponse;
import com.culturarte.soap.gen.PropuestaType;
import com.culturarte.soap.gen.UsuarioType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class SoapControladorAdapter implements IControlador {

    private static final Logger logger = LoggerFactory.getLogger(SoapControladorAdapter.class);

    private final WebServiceTemplate webServiceTemplate;
    
    @Value("${soap.service.url:}")
    private String soapServiceUrl;
    
    @Value("${soap.service.host:localhost}")
    private String soapServiceHost;
    
    @Value("${soap.service.port:8081}")
    private String soapServicePort;
    
    @Value("${soap.service.context-path:/soap/ws}")
    private String soapServiceContextPath;
    
    /**
     * Obtiene la URL base del servicio SOAP.
     * Si no está definida directamente, se construye desde los componentes.
     */
    private String getSoapServiceUrl() {
        if (soapServiceUrl != null && !soapServiceUrl.isEmpty() && !soapServiceUrl.startsWith("${")) {
            return soapServiceUrl;
        }
        // Construir URL desde componentes
        return String.format("http://%s:%s%s", soapServiceHost, soapServicePort, soapServiceContextPath);
    }

    public SoapControladorAdapter(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    @Override
    public List<String> listarCategoriasWeb() {
        String endpoint = getSoapServiceUrl() + "/categorias";
        logger.info("=== INICIO listarCategoriasWeb ===");
        logger.info("Endpoint: {}", endpoint);
        try {
            GetCategoriasRequest request = new GetCategoriasRequest();
            logger.debug("Enviando request SOAP para listar categorías web");
            GetCategoriasResponse response = (GetCategoriasResponse) webServiceTemplate.marshalSendAndReceive(endpoint, request);
            
            if (response == null) {
                logger.warn("Respuesta SOAP null en listarCategoriasWeb");
                return new ArrayList<>();
            }
            
            if (response.getCategoria() == null) {
                logger.warn("Lista de categorías null en respuesta");
                return new ArrayList<>();
            }
            
            List<String> resultado = mapCategoriaResponse(response.getCategoria());
            logger.info("Se obtuvieron {} categorías web exitosamente", resultado.size());
            logger.debug("=== FIN listarCategoriasWeb (exitoso) ===");
            return resultado;
        } catch (Exception e) {
            logger.error("=== ERROR en listarCategoriasWeb ===", e);
            logger.error("Endpoint que falló: {}", endpoint);
            throw new RuntimeException("Error al obtener categorías desde SOAP", e);
        }
    }

    @Override
    public List<String> listarCategoriasWebCompletas() {
        return listarCategoriasWeb();
    }

    @Override
    public ArrayList<DTPropuesta> getDTPropuestasWeb() {
        String endpoint = getSoapServiceUrl() + "/propuestas";
        logger.info("=== INICIO getDTPropuestasWeb ===");
        logger.info("Endpoint: {}", endpoint);
        try {
            ListarPropuestasRequest request = new ListarPropuestasRequest();
            logger.debug("Enviando request SOAP para obtener propuestas web");
            ListarPropuestasResponse response = (ListarPropuestasResponse) webServiceTemplate.marshalSendAndReceive(endpoint, request);
            
            ArrayList<DTPropuesta> result = new ArrayList<>();
            if (response == null) {
                logger.warn("Respuesta SOAP null en getDTPropuestasWeb");
                return result;
            }
            
            if (response.getPropuesta() == null) {
                logger.warn("Lista de propuestas null en respuesta");
                return result;
            }
            
            logger.debug("Convirtiendo {} propuestas de PropuestaType a DTPropuesta", response.getPropuesta().size());
            for (PropuestaType pt : response.getPropuesta()) {
                try {
                    DTPropuesta dtp = convertPropuestaTypeToDT(pt);
                    result.add(dtp);
                } catch (Exception e) {
                    logger.error("Error al convertir propuesta '{}' a DTPropuesta", pt.getTitulo(), e);
                }
            }
            
            logger.info("Se obtuvieron {} propuestas web exitosamente", result.size());
            logger.debug("=== FIN getDTPropuestasWeb (exitoso) ===");
            return result;
        } catch (Exception e) {
            logger.error("=== ERROR en getDTPropuestasWeb ===", e);
            logger.error("Endpoint que falló: {}", endpoint);
            throw new RuntimeException("Error al obtener propuestas desde SOAP", e);
        }
    }

    @Override
    public DTPropuesta getDTPropuesta(String titulo) {
        try {
            GetPropuestaRequest request = new GetPropuestaRequest();
            request.setTitulo(titulo);
            GetPropuestaResponse response = (GetPropuestaResponse) webServiceTemplate.marshalSendAndReceive(
                getSoapServiceUrl() + "/propuestas", request);
            
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
                getSoapServiceUrl() + "/usuarios", request);
            
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
        try {
            BuscarUsuariosRequest request = new BuscarUsuariosRequest();
            request.setNombre(nombre != null ? nombre : "");
            BuscarUsuariosResponse response = (BuscarUsuariosResponse) webServiceTemplate.marshalSendAndReceive(
                getSoapServiceUrl() + "/usuarios", request);
            
            ArrayList<DTUsuario> result = new ArrayList<>();
            if (response != null && response.getUsuario() != null) {
                for (UsuarioType ut : response.getUsuario()) {
                    DTUsuario dtu = convertUsuarioTypeToDT(ut);
                    result.add(dtu);
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar usuarios desde SOAP", e);
        }
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
        try {
            ListarUsuariosRequest request = new ListarUsuariosRequest();
            ListarUsuariosResponse response = (ListarUsuariosResponse) webServiceTemplate.marshalSendAndReceive(
                getSoapServiceUrl() + "/usuarios", request);
            
            ArrayList<DTUsuario> result = new ArrayList<>();
            if (response != null && response.getUsuario() != null) {
                for (UsuarioType ut : response.getUsuario()) {
                    DTUsuario dtu = convertUsuarioTypeToDT(ut);
                    result.add(dtu);
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error al listar usuarios desde SOAP", e);
        }
    }

    @Override
    public List<DTAcceso> getAccesos() {
        return List.of();
    }

    // ========== Métodos auxiliares de conversión ==========

    private DTPropuesta convertPropuestaTypeToDT(PropuestaType pt) {
        TipoEstado estado = null;
        if (pt.getEstadoActual() != null) {
            try {
                estado = TipoEstado.valueOf(pt.getEstadoActual());
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

    private ArrayList<String> mapCategoriaResponse(List<CategoriaType> categorias) {
        return categorias.stream()
                .map(CategoriaType::getNombre)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}

