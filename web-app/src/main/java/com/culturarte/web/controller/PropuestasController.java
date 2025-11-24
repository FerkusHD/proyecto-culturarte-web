package com.culturarte.web.controller;

import com.culturarte.logica.datatypes.DTPropuesta;
import com.culturarte.soap.gen.AgregarComentarioRequest;
import com.culturarte.soap.gen.AgregarComentarioResponse;
import com.culturarte.soap.gen.AgregarFavoritaRequest;
import com.culturarte.soap.gen.AgregarFavoritaResponse;
import com.culturarte.soap.gen.AltaColaboracionRequest;
import com.culturarte.soap.gen.AltaColaboracionResponse;
import com.culturarte.soap.gen.AltaPropuestaRequest;
import com.culturarte.soap.gen.AltaPropuestaResponse;
import com.culturarte.soap.gen.CancelarPropuestaRequest;
import com.culturarte.soap.gen.CancelarPropuestaResponse;
import com.culturarte.soap.gen.ExtenderFinanciacionRequest;
import com.culturarte.soap.gen.ExtenderFinanciacionResponse;
import com.culturarte.soap.gen.GetCategoriasResponse;
import com.culturarte.soap.gen.PropuestaType;
import com.culturarte.logica.datatypes.DTUsuario;
import com.culturarte.soap.gen.QuitarFavoritaRequest;
import com.culturarte.soap.gen.QuitarFavoritaResponse;
import com.culturarte.soap.gen.UsuarioType;
import com.culturarte.web.dto.PropuestaDTO;
import com.culturarte.web.soap.client.CategoriasSoapClient;
import com.culturarte.web.soap.client.PropuestasSoapClient;
import com.culturarte.web.soap.client.UsuarioSoapClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/propuestas")
public class PropuestasController {

    private static final Logger logger = LoggerFactory.getLogger(PropuestasController.class);

    @Autowired
    private PropuestasSoapClient soapClient; // Cliente SOAP inyectado

    @Autowired
    private UsuarioSoapClient usuarioSoapClient; // Cliente SOAP de usuarios

    @Autowired
    private CategoriasSoapClient categoriasSoapClient; // Cliente SOAP de categorías

    // --- Listar todas las propuestas ---
    @GetMapping("/listar")
    @ResponseBody
    public List<PropuestaDTO> listarPropuestas() {
        logger.info("=== INICIO listarPropuestas  ===");
        try {
            List<PropuestaType> propuestas = soapClient.listarPropuestas();
            if (propuestas == null) {
                logger.warn("Lista de propuestas es null, retornando lista vacía");
                return new ArrayList<>();
            }

            // Convertir a DTOs para serialización JSON correcta
            List<PropuestaDTO> propuestasDTO = new ArrayList<>();
            for (PropuestaType p : propuestas) {
                if (p != null) {
                    PropuestaDTO dto = new PropuestaDTO(p);
                    propuestasDTO.add(dto);
                    logger.debug("Propuesta convertida: titulo={}, tieneImagen={}",
                            dto.getTitulo(), dto.getImagenBase64() != null && !dto.getImagenBase64().isEmpty());
                }
            }

            logger.info("Retornando {} propuestas al cliente", propuestasDTO.size());
            logger.debug("=== FIN listarPropuestas (exitoso) ===");
            return propuestasDTO;
        } catch (Exception e) {
            logger.error("=== ERROR en listarPropuestas (REST endpoint) ===", e);
            logger.error("Tipo de excepción: {}", e.getClass().getName());
            logger.error("Mensaje: {}", e.getMessage());
            if (e.getCause() != null) {
                logger.error("Causa: {}", e.getCause().getMessage());
            }
            // Retornar lista vacía en caso de error para evitar errores 500
            return new ArrayList<>();
        }
    }

    @GetMapping("/listar/{titulo}")
    @ResponseBody
    public PropuestaDTO obtenerPropuestaPorTitulo(@PathVariable String titulo) {
        logger.info("Obteniendo propuesta via REST: {}", titulo);
        try {
            // Decodificar el título de la URL
            String tituloDecodificado = java.net.URLDecoder.decode(titulo, "UTF-8");
            PropuestaType propuesta = soapClient.getPropuesta(tituloDecodificado);
            if (propuesta == null) {
                logger.warn("Propuesta {} no encontrada, retornando null", tituloDecodificado);
                return null;
            }
            return new PropuestaDTO(propuesta);
        } catch (Exception e) {
            logger.error("Error al obtener propuesta por título: {}", titulo, e);
            return null;
        }
    }

    // --- Ver detalle de una propuesta ---
    @GetMapping("/{titulo:.+}")
    public String mostrarPropuesta(
            @PathVariable String titulo,
            Model model,
            HttpSession session,
            HttpServletRequest request) {
        logger.info("=== INICIO mostrarPropuesta ===");
        logger.info("Mostrando propuesta (raw): {}", titulo);
        try {
            String tituloDecodificado = titulo;
            try {
                if (titulo.contains("%")) {
                    tituloDecodificado = java.net.URLDecoder.decode(titulo, "UTF-8");
                    logger.info("Título decodificado: {}", tituloDecodificado);
                } else {
                    logger.info("Título sin codificar: {}", tituloDecodificado);
                }
            } catch (Exception e) {
                logger.warn("Error al decodificar título, usando original: {}", titulo, e);
                tituloDecodificado = titulo;
            }

            PropuestaType propuesta = soapClient.getPropuesta(tituloDecodificado);

            if (propuesta == null && !tituloDecodificado.equals(titulo)) {
                logger.info("No se encontró con título decodificado, intentando con original: {}", titulo);
                propuesta = soapClient.getPropuesta(titulo);
            }

            if (propuesta == null) {
                logger.warn("Propuesta no encontrada con título: {} (decodificado: {})", titulo, tituloDecodificado);
                model.addAttribute("mensajeError", "⚠️ La propuesta no existe");
                return "redirect:/";
            }

            logger.debug("Propuesta obtenida: titulo={}, estado={}, categoria={}",
                    propuesta.getTitulo(), propuesta.getEstadoActual(), propuesta.getCategoria());

            model.addAttribute("propuesta", propuesta);

            // Obtener proponente completo
            if (propuesta.getProponente() != null && !propuesta.getProponente().isEmpty()) {
                try {
                    logger.debug("Obteniendo proponente: {}", propuesta.getProponente());
                    UsuarioType usuarioResp = usuarioSoapClient.getUsuario(propuesta.getProponente());
                    if (usuarioResp != null && usuarioResp != null) {
                        UsuarioType proponente = usuarioResp;
                        model.addAttribute("proponente", proponente);
                        logger.debug("Proponente obtenido exitosamente");
                    } else {
                        logger.warn("Proponente no encontrado: {}", propuesta.getProponente());
                    }
                } catch (Exception e) {
                    logger.error("Error al obtener proponente: {}", propuesta.getProponente(), e);
                    // Si falla, no se agrega proponente (el JSP manejará el caso null)
                }
            }

            // Obtener colaboradores completos
            List<UsuarioType> colaboradores = new ArrayList<>();
            if (propuesta.getColaboradores() != null && !propuesta.getColaboradores().isEmpty()) {
                logger.debug("Obteniendo {} colaboradores", propuesta.getColaboradores().size());
                for (String nickColaborador : propuesta.getColaboradores()) {
                    try {
                        UsuarioType usuarioResp = usuarioSoapClient.getUsuario(nickColaborador);
                        if (usuarioResp != null && usuarioResp != null) {
                            UsuarioType colaborador = usuarioResp;
                            colaboradores.add(colaborador);
                        }
                    } catch (Exception e) {
                        logger.warn("Error al obtener colaborador: {}", nickColaborador, e);
                        // Continuar con el siguiente colaborador si falla
                    }
                }
                logger.debug("Se obtuvieron {} colaboradores exitosamente", colaboradores.size());
            }
            model.addAttribute("colaboradores", colaboradores);

            // Obtener usuario logueado completo
            Object usuarioLogueadoObj = session.getAttribute("usuarioLogueado");
            DTUsuario usuarioLogueado = null;
            String nickUsuarioFinal = null;

            if (usuarioLogueadoObj instanceof DTUsuario) {
                usuarioLogueado = (DTUsuario) usuarioLogueadoObj;
                nickUsuarioFinal = usuarioLogueado.getNickname();
            }

            if (usuarioLogueado == null) {
                usuarioLogueado = new DTUsuario();
                usuarioLogueado.setTipo("visitante");
                usuarioLogueado.setNickname("visitante");
            }

            final String nickUsuario = nickUsuarioFinal != null ? nickUsuarioFinal : "visitante";
            model.addAttribute("usuarioLogueado", usuarioLogueado);
            model.addAttribute("nickUsuario", nickUsuario);

            // Verificar si es favorita
            boolean esFavorita = false;
            if (nickUsuario != null && !"visitante".equals(nickUsuario)) {
                for (DTPropuesta p : usuarioLogueado.getPropuestasSeguidas()) {
                    if (p.getTitulo().equals(propuesta.getTitulo())) {
                        esFavorita = true;
                        break;
                    }
                }
            }
            model.addAttribute("esFavorita", esFavorita);

            // Verificar si puede comentar (colaborador que ha colaborado)
            boolean puedeComentar = false;
            if (usuarioLogueado != null && "colaborador".equals(usuarioLogueado.getTipo()) && nickUsuario != null) {
                final String nickUsuarioParaLambda = nickUsuario;
                puedeComentar = colaboradores.stream()
                        .anyMatch(c -> c.getNickname() != null && c.getNickname().equals(nickUsuarioParaLambda));
            }
            model.addAttribute("puedeComentar", puedeComentar);

            logger.info("Propuesta mostrada exitosamente: {}", tituloDecodificado);
            logger.debug("=== FIN mostrarPropuesta (exitoso) ===");
            return "consultarPropuesta";

        } catch (Exception e) {
            logger.error("=== ERROR en mostrarPropuesta ===", e);
            logger.error("Error al mostrar propuesta: {}", titulo, e);
            model.addAttribute("mensajeError", "❌ Error al cargar la propuesta: " + e.getMessage());
            return "error";
        }
    }

    // Método auxiliar para convertir UsuarioType a DTUsuario
    private DTUsuario convertirDT(UsuarioType u) {
        if (u == null)
            return null;
        DTUsuario dt = new DTUsuario();
        dt.setNickname(u.getNickname());
        dt.setNombre(u.getNombre());
        dt.setApellido(u.getApellido());
        dt.setEmail(u.getEmail());
        dt.setImagen(u.getImagen());
        dt.setTipo(u.getTipo());
        if (u.getFechaNacimiento() != null) {
            dt.setFechaNacimiento(u.getFechaNacimiento().toGregorianCalendar().toZonedDateTime().toLocalDate());
        }

        // Convertir propuestas seguidas (favoritas)
        if (u.getPropuestasSeguidas() != null && !u.getPropuestasSeguidas().isEmpty()) {
            ArrayList<DTPropuesta> propuestasSeguidas = new ArrayList<>();
            for (PropuestaType p : u.getPropuestasSeguidas()) {
                if (p != null) {
                    DTPropuesta dtProp = new DTPropuesta();
                    dtProp.setTitulo(p.getTitulo());
                    dtProp.setImagen(p.getImagen());
                    dtProp.setProponente(p.getProponente());
                    propuestasSeguidas.add(dtProp);
                }
            }
            dt.setPropuestasSeguidas(propuestasSeguidas);
        }

        return dt;
    }

    // --- Alta de colaboración ---
    @PostMapping("/altaColaboracion")
    public String altaColaboracion(
            @RequestParam("monto") float monto,
            @RequestParam("tipoRetorno") String tipoRetorno,
            @RequestParam("tituloPropuesta") String tituloPropuesta,
            @RequestParam(value = "nickColaborador", required = false) String nickColaborador,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            DTUsuario usuario = obtenerUsuarioDesdeSesion(session);
            if (usuario == null || "visitante".equals(usuario.getTipo())) {
                redirectAttributes.addFlashAttribute("mensajeError", "❌ Debe iniciar sesión para colaborar.");
                return "redirect:/login";
            }
            if (!"colaborador".equalsIgnoreCase(usuario.getTipo())) {
                redirectAttributes.addFlashAttribute("mensajeError",
                        "❌ Solo los colaboradores pueden realizar aportes.");
                return "redirect:/propuestas/" + tituloPropuesta;
            }

            // Si no se proporciona nickColaborador, usar el del usuario logueado
            if (nickColaborador == null || nickColaborador.trim().isEmpty()) {
                nickColaborador = usuario.getNickname();
            }

            // Validar que no exista colaboración previa
            PropuestaType propuesta = soapClient.getPropuesta(tituloPropuesta);
            if (propuesta == null) {
                redirectAttributes.addFlashAttribute("mensajeError", "❌ La propuesta no existe.");
                return "redirect:/propuestas/buscar";
            }
            if (propuesta.getColaboradores() != null &&
                    propuesta.getColaboradores().stream().anyMatch(nickColaborador::equalsIgnoreCase)) {
                redirectAttributes.addFlashAttribute("mensajeError",
                        "Ya existe una colaboración para este usuario en la propuesta.");
                return "redirect:/propuestas/" + tituloPropuesta;
            }

            AltaColaboracionRequest request = new AltaColaboracionRequest();
            request.setMonto(monto);
            request.setTipoRetorno(tipoRetorno);
            request.setTituloPropuesta(tituloPropuesta);
            request.setNickColaborador(nickColaborador);

            AltaColaboracionResponse response = soapClient.altaColaboracion(request);

            if (response.isExito()) {
                redirectAttributes.addFlashAttribute("mensajeExito", response.getMensaje());
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", response.getMensaje());
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensajeError", "❌ Error al registrar la colaboración.");
        }

        return "redirect:/propuestas/" + tituloPropuesta;
    }

    // --- Agregar comentario ---
    @PostMapping("/agregarComentario")
    public String agregarComentario(
            @RequestParam String tituloPropuesta,
            @RequestParam String texto,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            DTUsuario usuario = obtenerUsuarioDesdeSesion(session);
            if (usuario == null || "visitante".equals(usuario.getTipo())) {
                redirectAttributes.addFlashAttribute("mensajeError", "❌ Debe iniciar sesión para comentar.");
                return "redirect:/propuestas/" + tituloPropuesta;
            }

            if (!"colaborador".equalsIgnoreCase(usuario.getTipo())) {
                redirectAttributes.addFlashAttribute("mensajeError", "❌ Solo los colaboradores pueden comentar.");
                return "redirect:/propuestas/" + tituloPropuesta;
            }

            if (texto == null || texto.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("mensajeError", "❌ El comentario no puede estar vacío.");
                return "redirect:/propuestas/" + tituloPropuesta;
            }

            PropuestaType propuesta = soapClient.getPropuesta(tituloPropuesta);
            if (propuesta == null || propuesta.getColaboradores() == null ||
                    propuesta.getColaboradores().stream()
                            .noneMatch(nick -> nick.equalsIgnoreCase(usuario.getNickname()))) {
                redirectAttributes.addFlashAttribute("mensajeError",
                        "❌ Solo los colaboradores que apoyaron la propuesta pueden comentar.");
                return "redirect:/propuestas/" + tituloPropuesta;
            }

            AgregarComentarioRequest request = new AgregarComentarioRequest();
            request.setTituloPropuesta(tituloPropuesta);
            request.setTexto(texto);
            request.setNickColaborador(usuario.getNickname());

            AgregarComentarioResponse response = soapClient.agregarComentario(request);

            if (response.isExito()) {
                redirectAttributes.addFlashAttribute("mensajeExito", response.getMensaje());
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", response.getMensaje());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "❌ Error al agregar comentario.");
        }

        return "redirect:/propuestas/" + tituloPropuesta;
    }

    // --- Sugerencias para autocompletado ---
    @GetMapping("/buscar/sugerencias")
    @ResponseBody
    public List<String> sugerencias(@RequestParam("q") String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        String queryLower = query.toLowerCase();
        try {
            List<PropuestaType> propuestas = soapClient.listarPropuestas();
            if (propuestas == null) {
                return List.of();
            }
            return propuestas.stream()
                    .map(PropuestaType::getTitulo)
                    .filter(titulo -> titulo != null && titulo.toLowerCase().contains(queryLower))
                    .distinct()
                    .limit(10)
                    .toList();
        } catch (Exception e) {
            logger.error("Error al obtener sugerencias para '{}'", query, e);
            return List.of();
        }
    }

    // --- Agregar a favoritas ---
    @PostMapping("/agregarFavorita")
    public String agregarFavorita(
            @RequestParam String tituloPropuesta,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            // Obtener nickUsuario de la sesión
            String nickUsuario = obtenerNickUsuario(session);
            if (nickUsuario == null || "visitante".equals(nickUsuario)) {
                redirectAttributes.addFlashAttribute("mensajeError", "❌ Debe iniciar sesión para agregar a favoritos.");
                return "redirect:/propuestas/" + tituloPropuesta;
            }

            AgregarFavoritaRequest request = new AgregarFavoritaRequest();
            request.setTituloPropuesta(tituloPropuesta);
            request.setNickUsuario(nickUsuario);

            AgregarFavoritaResponse response = soapClient.agregarFavorita(request);

            if (response.isExito()) {
                redirectAttributes.addFlashAttribute("mensajeExito", response.getMensaje());
                DTUsuario u = convertirDT(usuarioSoapClient.getUsuario(nickUsuario));
                session.setAttribute("usuarioLogueado", u);
                model.addAttribute("usuarioLogueado", u);
                model.addAttribute("esFavorita", true);
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", response.getMensaje());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "❌ Error al agregar favorita.");
        }

        return "redirect:/propuestas/" + tituloPropuesta;
    }

    // --- Quitar de favoritas ---
    @PostMapping("/quitarFavorita")
    public String quitarFavorita(
            @RequestParam String tituloPropuesta,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            // Obtener nickUsuario de la sesión
            String nickUsuario = obtenerNickUsuario(session);
            if (nickUsuario == null || "visitante".equals(nickUsuario)) {
                redirectAttributes.addFlashAttribute("mensajeError", "❌ Debe iniciar sesión.");
                return "redirect:/propuestas/" + tituloPropuesta;
            }

            QuitarFavoritaRequest request = new QuitarFavoritaRequest();
            request.setTituloPropuesta(tituloPropuesta);
            request.setNickUsuario(nickUsuario);

            QuitarFavoritaResponse response = soapClient.quitarFavorita(request);

            if (response.isExito()) {
                redirectAttributes.addFlashAttribute("mensajeExito", response.getMensaje());
                DTUsuario u = convertirDT(usuarioSoapClient.getUsuario(nickUsuario));
                session.setAttribute("usuarioLogueado", u);
                model.addAttribute("usuarioLogueado", u);
                model.addAttribute("esFavorita", false);
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", response.getMensaje());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "❌ Error al quitar favorita.");
        }

        return "redirect:/propuestas/" + tituloPropuesta;
    }

    // --- Cancelar propuesta ---
    @PostMapping("/cancelar/{titulo}")
    public String cancelarPropuesta(
            @PathVariable String titulo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            DTUsuario usuario = obtenerUsuarioDesdeSesion(session);
            if (usuario == null || "visitante".equals(usuario.getTipo())) {
                redirectAttributes.addFlashAttribute("mensajeError", "❌ Debe iniciar sesión para cancelar propuestas.");
                return "redirect:/login";
            }

            CancelarPropuestaRequest request = new CancelarPropuestaRequest();
            request.setTituloPropuesta(titulo);

            CancelarPropuestaResponse response = soapClient.cancelarPropuesta(request);

            if (response.isExito()) {
                redirectAttributes.addFlashAttribute("mensajeExito", response.getMensaje());
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", response.getMensaje());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "❌ Error al cancelar la propuesta.");
        }

        return "redirect:/propuestas/" + titulo;
    }

    // --- Alta de propuesta (GET) ---
    @GetMapping("/alta")
    public String mostrarAltaPropuesta(Model model, HttpSession session) {
        try {
            // Verificar que el usuario sea proponente
            Object usuarioLogueadoObj = session.getAttribute("usuarioLogueado");
            DTUsuario usuarioLogueado = null;

            if (usuarioLogueadoObj instanceof DTUsuario) {
                usuarioLogueado = (DTUsuario) usuarioLogueadoObj;
            } else if (usuarioLogueadoObj instanceof UsuarioType) {
                usuarioLogueado = convertirDT((UsuarioType) usuarioLogueadoObj);
            }

            if (usuarioLogueado == null || !"proponente".equals(usuarioLogueado.getTipo())) {
                return "redirect:/login";
            }

            // Obtener categorías
            List<String> categorias = categoriasSoapClient
                    .obtenerCategorias(new com.culturarte.soap.gen.GetCategoriasRequest())
                    .getCategoria().stream()
                    .map(com.culturarte.soap.gen.CategoriaType::getNombre)
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("categorias", categorias);

            // Obtener tipos de retorno
            List<String> tiposRetorno = new ArrayList<>();
            tiposRetorno.add("ENTRADAGRATIS");
            tiposRetorno.add("PORCENTAJEGANANCIA");
            model.addAttribute("tiposRetorno", tiposRetorno);

            return "altaPropuesta";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensaje", "❌ Error al cargar el formulario.");
            return "altaPropuesta";
        }
    }

    // --- Alta de propuesta (POST) ---
    @PostMapping("/alta")
    public String procesarAltaPropuesta(
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String lugar,
            @RequestParam String fechaPrevista,
            @RequestParam String categoria,
            @RequestParam("tiposRetorno[]") String[] tiposRetorno,
            @RequestParam float montoEntrada,
            @RequestParam float montoNecesario,
            @RequestParam(required = false) MultipartFile imagenFile,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // Obtener usuario logueado
            Object usuarioLogueadoObj = session.getAttribute("usuarioLogueado");
            DTUsuario usuarioLogueado = null;
            String nickProponente = null;

            if (usuarioLogueadoObj instanceof DTUsuario) {
                usuarioLogueado = (DTUsuario) usuarioLogueadoObj;
                nickProponente = usuarioLogueado.getNickname();
            } else if (usuarioLogueadoObj instanceof UsuarioType) {
                usuarioLogueado = convertirDT((UsuarioType) usuarioLogueadoObj);
                nickProponente = usuarioLogueado.getNickname();
            }

            if (nickProponente == null || !"proponente".equals(usuarioLogueado.getTipo())) {
                redirectAttributes.addFlashAttribute("mensaje", "❌ Solo los proponentes pueden crear propuestas.");
                return "redirect:/propuestas/alta";
            }

            // Procesar imagen
            String imagenBase64 = null;
            if (imagenFile != null && !imagenFile.isEmpty()) {
                try {
                    // Convertir a base64 para el SOAP
                    byte[] imagenBytes = imagenFile.getBytes();
                    imagenBase64 = Base64.getEncoder().encodeToString(imagenBytes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Crear request SOAP
            AltaPropuestaRequest request = new AltaPropuestaRequest();
            request.setTitulo(titulo);
            request.setDescripcion(descripcion);
            request.setLugar(lugar);
            request.setFechaPrevista(
                    DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse(fechaPrevista).toString()));
            request.setCategoria(categoria);
            request.setMontoEntrada(montoEntrada);
            request.setMontoNecesario(montoNecesario);
            request.setNickProponente(nickProponente);
            if (imagenBase64 != null) {
                request.setImagenBase64(imagenBase64);
            }

            // Agregar tipos de retorno
            for (String tipo : tiposRetorno) {
                request.getTiposRetorno().add(tipo);
            }

            AltaPropuestaResponse response = soapClient.altaPropuesta(request);

            if (response.isExito()) {
                redirectAttributes.addFlashAttribute("mensaje", response.getMensaje());
                return "redirect:/exitoAltaPropuesta";
            } else {
                model.addAttribute("mensaje", response.getMensaje());
                model.addAttribute("titulo", titulo);
                model.addAttribute("descripcion", descripcion);
                model.addAttribute("lugar", lugar);
                model.addAttribute("fechaPrevista", fechaPrevista);
                model.addAttribute("categoria", categoria);
                model.addAttribute("montoEntrada", montoEntrada);
                model.addAttribute("montoNecesario", montoNecesario);

                // Recargar categorías y tipos de retorno
                List<String> categorias = categoriasSoapClient
                        .obtenerCategorias(new com.culturarte.soap.gen.GetCategoriasRequest())
                        .getCategoria().stream()
                        .map(com.culturarte.soap.gen.CategoriaType::getNombre)
                        .collect(java.util.stream.Collectors.toList());
                model.addAttribute("categorias", categorias);
                List<String> tiposRetornoList = new ArrayList<>();
                tiposRetornoList.add("ENTRADAGRATIS");
                tiposRetornoList.add("PORCENTAJEGANANCIA");
                model.addAttribute("tiposRetorno", tiposRetornoList);

                return "altaPropuesta";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensaje", "❌ Error al crear la propuesta: " + e.getMessage());
            return "altaPropuesta";
        }
    }

    // --- Buscar propuestas ---
    @GetMapping("/buscar")
    public String buscarPropuestas(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String orden,
            Model model) {
        logger.info("=== INICIO buscarPropuestas ===");
        logger.info("Búsqueda: query={}, categoria={}, estado={}, orden={}", query, categoria, estado, orden);
        try {
            List<PropuestaType> todasLasPropuestas = null;
            try {
                todasLasPropuestas = soapClient.listarPropuestas();
            } catch (Exception e) {
                logger.error("Error al obtener propuestas desde SOAP", e);
                todasLasPropuestas = new ArrayList<>();
            }

            if (todasLasPropuestas == null) {
                logger.warn("Lista de propuestas es null, usando lista vacía");
                todasLasPropuestas = new ArrayList<>();
            }

            logger.debug("Total de propuestas obtenidas: {}", todasLasPropuestas.size());
            List<PropuestaType> resultados = new ArrayList<>();

            // Filtrar por query
            if (query != null && !query.trim().isEmpty()) {
                String queryLower = query.toLowerCase().trim();
                for (PropuestaType p : todasLasPropuestas) {
                    if ((p.getTitulo() != null && p.getTitulo().toLowerCase().contains(queryLower)) ||
                            (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(queryLower))) {
                        resultados.add(p);
                    }
                }
            } else {
                // Si no hay query, mostrar todas las propuestas
                resultados.addAll(todasLasPropuestas);
            }

            // Filtrar por categoría
            if (categoria != null && !categoria.trim().isEmpty()) {
                resultados.removeIf(p -> !categoria.equals(p.getCategoria()));
            }

            // Filtrar por estado
            if (estado != null && !estado.trim().isEmpty()) {
                resultados.removeIf(p -> !estado.equals(p.getEstadoActual()));
            }

            // Ordenar
            if ("tituloAsc".equals(orden)) {
                resultados.sort((p1, p2) -> {
                    String t1 = p1.getTitulo() != null ? p1.getTitulo() : "";
                    String t2 = p2.getTitulo() != null ? p2.getTitulo() : "";
                    return t1.compareToIgnoreCase(t2);
                });
            } else if ("fechaDesc".equals(orden)) {
                // Ordenar por fecha descendente (más recientes primero)
                resultados.sort((p1, p2) -> {
                    if (p1.getFechaPrevista() != null && p2.getFechaPrevista() != null) {
                        return p2.getFechaPrevista().compare(p1.getFechaPrevista());
                    }
                    return 0;
                });
            }

            model.addAttribute("resultados", resultados);
            model.addAttribute("query", query);
            model.addAttribute("categoria", categoria);
            model.addAttribute("estado", estado);
            model.addAttribute("orden", orden);

            // Cargar categorías para el filtro
            try {
                com.culturarte.soap.gen.GetCategoriasRequest categoriasRequest = new com.culturarte.soap.gen.GetCategoriasRequest();
                GetCategoriasResponse categoriasResponse = categoriasSoapClient.obtenerCategorias(categoriasRequest);
                if (categoriasResponse != null && categoriasResponse.getCategoria() != null
                        && !categoriasResponse.getCategoria().isEmpty()) {
                    List<String> categorias = categoriasResponse.getCategoria().stream()
                            .filter(cat -> cat != null && cat.getNombre() != null)
                            .map(com.culturarte.soap.gen.CategoriaType::getNombre)
                            .collect(java.util.stream.Collectors.toList());
                    model.addAttribute("categorias", categorias);
                    logger.debug("Categorías cargadas para filtro: {}", categorias.size());
                } else {
                    logger.warn("No se pudieron obtener categorías para el filtro (respuesta null o vacía)");
                    model.addAttribute("categorias", new ArrayList<>());
                }
            } catch (Exception e) {
                logger.error("Error al cargar categorías para filtro", e);
                logger.error("Stack trace completo:", e);
                model.addAttribute("categorias", new ArrayList<>());
            }

            logger.info("Búsqueda completada: {} resultados", resultados.size());
            logger.debug("=== FIN buscarPropuestas (exitoso) ===");
            return "busquedaPropuestas";
        } catch (Exception e) {
            logger.error("=== ERROR en buscarPropuestas ===", e);
            logger.error("Tipo de excepción: {}", e.getClass().getName());
            logger.error("Mensaje: {}", e.getMessage());
            if (e.getCause() != null) {
                logger.error("Causa: {}", e.getCause().getMessage());
            }
            logger.error("Stack trace completo:", e);
            model.addAttribute("resultados", new ArrayList<>());
            model.addAttribute("categorias", new ArrayList<>());
            model.addAttribute("query", query);
            model.addAttribute("categoria", categoria);
            model.addAttribute("estado", estado);
            model.addAttribute("orden", orden);
            model.addAttribute("mensaje", "❌ Error al buscar propuestas: " + e.getMessage());
            return "busquedaPropuestas";
        }
    }

    // --- Registrar colaboración (sin título) ---
    @GetMapping("/registrarColaboracionProp")
    public String mostrarRegistroColaboracionProp(Model model, HttpSession session) {
        try {
            Object usuarioLogueadoObj = session.getAttribute("usuarioLogueado");
            DTUsuario usuarioLogueado = null;

            if (usuarioLogueadoObj instanceof DTUsuario) {
                usuarioLogueado = (DTUsuario) usuarioLogueadoObj;
            } else if (usuarioLogueadoObj instanceof UsuarioType) {
                usuarioLogueado = convertirDT((UsuarioType) usuarioLogueadoObj);
            }

            if (usuarioLogueado == null || !"colaborador".equals(usuarioLogueado.getTipo())) {
                return "redirect:/login";
            }

            // Obtener todas las propuestas disponibles
            List<PropuestaType> propuestas = soapClient.listarPropuestas();
            model.addAttribute("propuestas", propuestas);

            return "registroColaboracion";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensajeError", "❌ Error al cargar el formulario.");
            return "registroColaboracion";
        }
    }

    // --- Registrar colaboración (con título) ---
    @GetMapping("/registroColaboracion")
    public String mostrarRegistroColaboracion(
            @RequestParam(required = false) String titulo,
            Model model,
            HttpSession session) {
        try {
            Object usuarioLogueadoObj = session.getAttribute("usuarioLogueado");
            DTUsuario usuarioLogueado = null;

            if (usuarioLogueadoObj instanceof DTUsuario) {
                usuarioLogueado = (DTUsuario) usuarioLogueadoObj;
            } else if (usuarioLogueadoObj instanceof UsuarioType) {
                usuarioLogueado = convertirDT((UsuarioType) usuarioLogueadoObj);
            }

            if (usuarioLogueado == null || !"colaborador".equals(usuarioLogueado.getTipo())) {
                return "redirect:/login";
            }

            if (titulo != null && !titulo.trim().isEmpty()) {
                model.addAttribute("tituloPropuesta", titulo);
            }

            return "registroColaboracion";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensajeError", "❌ Error al cargar el formulario.");
            return "registroColaboracion";
        }
    }

    // --- Extender financiación ---
    @PostMapping("/extender/{titulo}")
    public String extenderFinanciacion(
            @PathVariable String titulo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            // Verificar que el usuario sea el proponente
            Object usuarioLogueadoObj = session.getAttribute("usuarioLogueado");
            DTUsuario usuarioLogueado = null;
            String nickUsuario = null;

            if (usuarioLogueadoObj instanceof DTUsuario) {
                usuarioLogueado = (DTUsuario) usuarioLogueadoObj;
                nickUsuario = usuarioLogueado.getNickname();
            } else if (usuarioLogueadoObj instanceof UsuarioType) {
                usuarioLogueado = convertirDT((UsuarioType) usuarioLogueadoObj);
                nickUsuario = usuarioLogueado.getNickname();
            }

            if (nickUsuario == null || !"proponente".equals(usuarioLogueado.getTipo())) {
                redirectAttributes.addFlashAttribute("mensajeError",
                        "❌ Solo el proponente puede extender la financiación.");
                return "redirect:/propuestas/" + titulo;
            }

            // Obtener la propuesta para verificar que el usuario es el proponente
            PropuestaType propuesta = soapClient.getPropuesta(titulo);
            if (propuesta == null || !nickUsuario.equals(propuesta.getProponente())) {
                redirectAttributes.addFlashAttribute("mensajeError",
                        "❌ Solo el proponente de la propuesta puede extender la financiación.");
                return "redirect:/propuestas/" + titulo;
            }

            // Extender 30 días desde la fecha actual
            LocalDate nuevaFecha = LocalDate.now().plusDays(30);

            ExtenderFinanciacionRequest request = new ExtenderFinanciacionRequest();
            request.setTituloPropuesta(titulo);
            request.setNuevaFecha(DatatypeFactory.newInstance().newXMLGregorianCalendar(nuevaFecha.toString()));

            ExtenderFinanciacionResponse response = soapClient.extenderFinanciacion(request);

            if (response.isExito()) {
                redirectAttributes.addFlashAttribute("mensajeExito", response.getMensaje());
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", response.getMensaje());
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensajeError", "❌ Error al extender la financiación.");
        }

        return "redirect:/propuestas/" + titulo;
    }

    // Método auxiliar para obtener el nickname del usuario de la sesión
    private String obtenerNickUsuario(HttpSession session) {
        Object usuarioLogueadoObj = session.getAttribute("usuarioLogueado");
        if (usuarioLogueadoObj instanceof DTUsuario) {
            return ((DTUsuario) usuarioLogueadoObj).getNickname();
        } else if (usuarioLogueadoObj instanceof UsuarioType) {
            return ((UsuarioType) usuarioLogueadoObj).getNickname();
        } else if (usuarioLogueadoObj instanceof com.culturarte.soap.gen.GetUsuarioResponse) {
            com.culturarte.soap.gen.GetUsuarioResponse resp = (com.culturarte.soap.gen.GetUsuarioResponse) usuarioLogueadoObj;
            if (resp.getUsuario() != null) {
                return resp.getUsuario().getNickname();
            }
        }
        return null;
    }

    private DTUsuario obtenerUsuarioDesdeSesion(HttpSession session) {
        Object usuarioLogueadoObj = session.getAttribute("usuarioLogueado");
        if (usuarioLogueadoObj instanceof DTUsuario) {
            return (DTUsuario) usuarioLogueadoObj;
        } else if (usuarioLogueadoObj instanceof UsuarioType) {
            return convertirDT((UsuarioType) usuarioLogueadoObj);
        } else if (usuarioLogueadoObj instanceof com.culturarte.soap.gen.GetUsuarioResponse response) {
            if (response.getUsuario() != null) {
                return convertirDT(response.getUsuario());
            }
        }
        return null;
    }

    // --- Recomendaciones para colaboradores ---
    @GetMapping("/recomendaciones")
    public String mostrarRecomendaciones(Model model, HttpSession session) {
        logger.info("=== INICIO mostrarRecomendaciones ===");
        try {
            // Verificar que el usuario sea colaborador
            DTUsuario usuarioLogueado = obtenerUsuarioDesdeSesion(session);

            if (usuarioLogueado == null || !"colaborador".equals(usuarioLogueado.getTipo())) {
                logger.warn("Usuario no es colaborador o no está logueado");
                model.addAttribute("mensajeError", "❌ Debe ser un colaborador para ver recomendaciones.");
                return "redirect:/login";
            }

            String nickColaborador = usuarioLogueado.getNickname();
            logger.info("Obteniendo recomendaciones para colaborador: {}", nickColaborador);

            // Obtener recomendaciones desde el cliente SOAP
            List<PropuestaType> recomendaciones = soapClient.obtenerRecomendaciones(nickColaborador);

            if (recomendaciones == null) {
                logger.warn("Lista de recomendaciones es null");
                recomendaciones = new ArrayList<>();
            }

            logger.info("Se obtuvieron {} propuestas recomendadas", recomendaciones.size());
            model.addAttribute("recomendaciones", recomendaciones);
            model.addAttribute("usuarioLogueado", usuarioLogueado);

            logger.debug("=== FIN mostrarRecomendaciones (exitoso) ===");
            return "recomendaciones";

        } catch (Exception e) {
            logger.error("=== ERROR en mostrarRecomendaciones ===", e);
            logger.error("Tipo de excepción: {}", e.getClass().getName());
            logger.error("Mensaje: {}", e.getMessage());
            model.addAttribute("recomendaciones", new ArrayList<>());
            model.addAttribute("mensajeError", "❌ Error al obtener recomendaciones: " + e.getMessage());
            return "recomendaciones";
        }
    }

    private void actualizarUsuarioEnSesion(HttpSession session, String nickname) {

        try {
            UsuarioType usuarioActualizado = usuarioSoapClient.getUsuario(nickname);
            if (usuarioActualizado != null) {
                session.setAttribute("usuarioLogueado", convertirDT(usuarioActualizado));
            }
        } catch (Exception e) {
            logger.warn("No se pudo refrescar el usuario en sesión: {}", nickname, e);
        }
    }
}
