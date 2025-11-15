package com.culturarte.web.controller;

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
import com.culturarte.soap.gen.PropuestaType;
import com.culturarte.logica.datatypes.DTUsuario;
import com.culturarte.soap.gen.QuitarFavoritaRequest;
import com.culturarte.soap.gen.QuitarFavoritaResponse;
import com.culturarte.soap.gen.UsuarioType;
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

import javax.xml.datatype.DatatypeFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/propuestas")
public class PropuestasController {

    @Autowired
    private PropuestasSoapClient soapClient; // Cliente SOAP inyectado

    @Autowired
    private UsuarioSoapClient usuarioSoapClient; // Cliente SOAP de usuarios

    @Autowired
    private CategoriasSoapClient categoriasSoapClient; // Cliente SOAP de categorías

    // --- Listar todas las propuestas ---
    @GetMapping("/listar")
    @ResponseBody
    public List<PropuestaType> listarPropuestas() {
        try {
            return soapClient.listarPropuestas();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // --- Ver detalle de una propuesta ---
    @GetMapping("/{titulo}")
    public String mostrarPropuesta(
            @PathVariable String titulo,
            Model model,
            HttpSession session,
            HttpServletRequest request) {

        try {
            PropuestaType propuesta = soapClient.getPropuesta(titulo);

            if (propuesta == null) {
                model.addAttribute("mensajeError", "⚠️ La propuesta no existe");
                return "redirect:/propuestas/listar";
            }

            model.addAttribute("propuesta", propuesta);

            // Obtener proponente completo
            if (propuesta.getProponente() != null && !propuesta.getProponente().isEmpty()) {
                try {
                    UsuarioType proponente = usuarioSoapClient.getUsuario(propuesta.getProponente()).getUsuario();
                    model.addAttribute("proponente", proponente);
                } catch (Exception e) {
                    // Si falla, no se agrega proponente (el JSP manejará el caso null)
                }
            }

            // Obtener colaboradores completos
            List<UsuarioType> colaboradores = new ArrayList<>();
            if (propuesta.getColaboradores() != null && !propuesta.getColaboradores().isEmpty()) {
                for (String nickColaborador : propuesta.getColaboradores()) {
                    try {
                        UsuarioType colaborador = usuarioSoapClient.getUsuario(nickColaborador).getUsuario();
                        if (colaborador != null) {
                            colaboradores.add(colaborador);
                        }
                    } catch (Exception e) {
                        // Continuar con el siguiente colaborador si falla
                    }
                }
            }
            model.addAttribute("colaboradores", colaboradores);

            // Obtener usuario logueado completo
            Object usuarioLogueadoObj = session.getAttribute("usuarioLogueado");
            DTUsuario usuarioLogueado = null;
            String nickUsuarioFinal = null;
            
            if (usuarioLogueadoObj instanceof DTUsuario) {
                usuarioLogueado = (DTUsuario) usuarioLogueadoObj;
                nickUsuarioFinal = usuarioLogueado.getNickname();
            } else if (usuarioLogueadoObj instanceof com.culturarte.soap.gen.GetUsuarioResponse) {
                com.culturarte.soap.gen.GetUsuarioResponse resp = (com.culturarte.soap.gen.GetUsuarioResponse) usuarioLogueadoObj;
                if (resp.getUsuario() != null) {
                    usuarioLogueado = convertirDT(resp.getUsuario());
                    nickUsuarioFinal = resp.getUsuario().getNickname();
                }
            } else if (usuarioLogueadoObj instanceof UsuarioType) {
                UsuarioType u = (UsuarioType) usuarioLogueadoObj;
                usuarioLogueado = convertirDT(u);
                nickUsuarioFinal = u.getNickname();
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
                try {
                    List<PropuestaType> favoritas = usuarioSoapClient.getPropuestasFavoritas(nickUsuario);
                    final String tituloFinal = titulo;
                    esFavorita = favoritas != null && favoritas.stream()
                            .anyMatch(p -> p.getTitulo() != null && p.getTitulo().equals(tituloFinal));
                } catch (Exception e) {
                    // Si falla, se asume que no es favorita
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

            // Detección de dispositivo
            String userAgent = request.getHeader("User-Agent");
            boolean esMovil = userAgent != null && userAgent.toLowerCase().matches(".*(mobi|android|iphone|ipad).*");

            return esMovil ? "consultarPropuestaMovil" : "consultarPropuesta";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensajeError", "❌ Error al cargar la propuesta.");
            return "error";
        }
    }

    // Método auxiliar para convertir UsuarioType a DTUsuario
    private DTUsuario convertirDT(UsuarioType u) {
        if (u == null) return null;
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
        // Nota: UsuarioType no tiene getUsuariosSeguidos(), 
        // los usuarios seguidos se cargan cuando se necesita desde el servicio
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
            // Si no se proporciona nickColaborador, obtenerlo de la sesión
            if (nickColaborador == null || nickColaborador.trim().isEmpty()) {
                nickColaborador = obtenerNickUsuario(session);
                if (nickColaborador == null || "visitante".equals(nickColaborador)) {
                    redirectAttributes.addFlashAttribute("mensajeError", "❌ Debe iniciar sesión para colaborar.");
                    return "redirect:/propuestas/" + tituloPropuesta;
                }
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
            // Obtener nickColaborador de la sesión
            String nickColaborador = obtenerNickUsuario(session);
            if (nickColaborador == null || "visitante".equals(nickColaborador)) {
                redirectAttributes.addFlashAttribute("mensajeError", "❌ Debe iniciar sesión para comentar.");
                return "redirect:/propuestas/" + tituloPropuesta;
            }

            AgregarComentarioRequest request = new AgregarComentarioRequest();
            request.setTituloPropuesta(tituloPropuesta);
            request.setTexto(texto);
            request.setNickColaborador(nickColaborador);

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

    // --- Agregar a favoritas ---
    @PostMapping("/agregarFavorita")
    public String agregarFavorita(
            @RequestParam String tituloPropuesta,
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
            RedirectAttributes redirectAttributes) {

        try {
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
            List<String> categorias = categoriasSoapClient.obtenerCategorias(new com.culturarte.soap.gen.GetCategoriasRequest())
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
                    // Guardar imagen en el sistema de archivos
                    Path directorio = Paths.get(System.getProperty("user.dir"), "uploads", "imagenes");
                    if (!Files.exists(directorio)) {
                        Files.createDirectories(directorio);
                    }
                    String nombreArchivo = titulo.replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + "_" + imagenFile.getOriginalFilename();
                    Path rutaCompleta = directorio.resolve(nombreArchivo);
                    Files.copy(imagenFile.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);
                    
                    // Convertir a base64 para el SOAP
                    byte[] imagenBytes = Files.readAllBytes(rutaCompleta);
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
            request.setFechaPrevista(DatatypeFactory.newInstance().newXMLGregorianCalendar(LocalDate.parse(fechaPrevista).toString()));
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
                List<String> categorias = categoriasSoapClient.obtenerCategorias(new com.culturarte.soap.gen.GetCategoriasRequest())
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
        try {
            List<PropuestaType> todasLasPropuestas = soapClient.listarPropuestas();
            List<PropuestaType> resultados = new ArrayList<>();

            // Filtrar por query
            if (query != null && !query.trim().isEmpty()) {
                String queryLower = query.toLowerCase();
                for (PropuestaType p : todasLasPropuestas) {
                    if ((p.getTitulo() != null && p.getTitulo().toLowerCase().contains(queryLower)) ||
                        (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(queryLower))) {
                        resultados.add(p);
                    }
                }
            } else {
                resultados.addAll(todasLasPropuestas);
            }

            // Filtrar por categoría
            if (categoria != null && !categoria.trim().isEmpty()) {
                resultados.removeIf(p -> !categoria.equals(p.getCategoria()));
            }

            // Filtrar por estado
            if (estado != null && !estado.trim().isEmpty()) {
                resultados.removeIf(p -> !estado.equals(p.getEstado()));
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
            List<String> categorias = categoriasSoapClient.obtenerCategorias(new com.culturarte.soap.gen.GetCategoriasRequest())
                    .getCategoria().stream()
                    .map(com.culturarte.soap.gen.CategoriaType::getNombre)
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("categorias", categorias);

            return "busquedaPropuestas";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("resultados", new ArrayList<>());
            model.addAttribute("mensaje", "❌ Error al buscar propuestas.");
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
                redirectAttributes.addFlashAttribute("mensajeError", "❌ Solo el proponente puede extender la financiación.");
                return "redirect:/propuestas/" + titulo;
            }

            // Obtener la propuesta para verificar que el usuario es el proponente
            PropuestaType propuesta = soapClient.getPropuesta(titulo);
            if (propuesta == null || !nickUsuario.equals(propuesta.getProponente())) {
                redirectAttributes.addFlashAttribute("mensajeError", "❌ Solo el proponente de la propuesta puede extender la financiación.");
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
}
