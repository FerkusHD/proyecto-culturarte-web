package com.culturarte.web.controller;

import com.culturarte.exepciones.ColaboracionYaExiste;
import com.culturarte.exepciones.PropuestaYaExiste;
import com.culturarte.logica.IControlador;
import com.culturarte.logica.datatypes.DTColaboracion;
import com.culturarte.logica.datatypes.DTPropuesta;
import com.culturarte.logica.datatypes.DTUsuario;
import com.culturarte.logica.enums.TipoEstado;
import com.culturarte.logica.enums.TipoRetorno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;



@Controller
@RequestMapping("/propuestas")
public class PropuestasController {

    @Autowired
    private IControlador ctrl;

    @ResponseBody
    @GetMapping("/listar")
    List<DTPropuesta> propuestas() {
        return ctrl.getDTPropuestasWeb();
    }

    @ResponseBody
    @GetMapping("/listar/{titulo}")
    DTPropuesta propuestas(@PathVariable String titulo) {
        return ctrl.getDTPropuesta(titulo);
    }

    @GetMapping("/registroColaboracion")
    public String registroCol(
        @RequestParam("titulo") String tituloPropuesta,
        Model model) {

    model.addAttribute("tituloPropuesta", tituloPropuesta);
    return "registroColaboracion";
    }


    @PostMapping("/altaColaboracion")
    public String altaColaboracion(
        @RequestParam("monto") float monto,
        @RequestParam("tipoRetorno") String retorno,
        @RequestParam("tituloPropuesta") String tituloPropuesta,
        @RequestParam("nickColaborador") String nickColaborador,
        RedirectAttributes redirectAttributes) throws ColaboracionYaExiste {

            DTColaboracion existeColab = ctrl.getDTColaboracionPropuesta(nickColaborador, tituloPropuesta);
            DTPropuesta propuesta = ctrl.getDTPropuesta(tituloPropuesta);

            if (existeColab != null) {
                redirectAttributes.addFlashAttribute("mensajeError",
                "Ya existe una colaboración para este usuario y propuesta (" + nickColaborador + ", " + tituloPropuesta + ")");
                return "redirect:/"; 
            }

    LocalDate fecha = LocalDate.now();
    LocalTime hora = LocalTime.now();

    TipoRetorno tipoRetorno = TipoRetorno.valueOf(retorno.toUpperCase());
      if (TipoEstado.INGRESADA.equals(propuesta.getEstadoActual())){
            ctrl.nuevoEstadoPropuesta(tituloPropuesta, TipoEstado.ENFINANCIACION, fecha, hora);
        }

    ctrl.altaColaboracion(monto, fecha, hora, tipoRetorno, tituloPropuesta, nickColaborador);
    redirectAttributes.addFlashAttribute("mensajeExito", "Colaboración registrada correctamente!");
    return "redirect:/";
    }


    @GetMapping("/buscar")
    public String buscar(
            @RequestParam(value = "query", required = false, defaultValue = "") String query,
            @RequestParam(value = "estado", required = false) String estado,
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "orden", required = false) String orden,
            Model model) {

        List<DTPropuesta> resultados = ctrl.buscarPropuestas(query);
        if (resultados == null) resultados = List.of();

        // Aplicar filtros secundarios
        if (estado != null && !estado.isBlank()) {
            resultados = resultados.stream()
                    .filter(p -> p.getEstadoActual().toString().equalsIgnoreCase(estado))
                    .toList();
        }

        if ("tituloAsc".equals(orden)) {
            resultados = resultados.stream()
                    .sorted((p1, p2) -> p1.getTitulo().compareToIgnoreCase(p2.getTitulo()))
                    .toList();
        } else if ("fechaDesc".equals(orden)) {
            resultados = resultados.stream()
                    .sorted((p1, p2) -> p2.getFechaPrevista().compareTo(p1.getFechaPrevista()))
                    .toList();
        }
        if (categoria != null && !categoria.isBlank()) {
            resultados = resultados.stream()
                    .filter(p -> p.getCategoria().equalsIgnoreCase(categoria))
                    .toList();
        }
        model.addAttribute("resultados", resultados);
        model.addAttribute("query", query);
        model.addAttribute("estado", estado);
        model.addAttribute("orden", orden);
        model.addAttribute("categoria", categoria);
        model.addAttribute("categorias", ctrl.listarCategoriasWebCompletas());

        return "busquedaPropuestas";
    }

    @GetMapping("/alta")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("categorias", ctrl.listarCategoriasWebCompletas());
        model.addAttribute("tiposRetorno" , ctrl.getTiposRetorno());
        return "altaPropuesta";
    }

    @PostMapping("/alta")
    public String altaPropuesta(
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String lugar,
            @RequestParam String fechaPrevista,
            @RequestParam String categoria,
            @RequestParam String[] tiposRetorno,
            @RequestParam Float montoEntrada,
            @RequestParam Float montoNecesario,
            @RequestParam(required = false) MultipartFile imagenFile,
            HttpSession session,
            Model model
    ) {
        try {
            // Convertir fecha de String a LocalDate
            LocalDate fecha = LocalDate.parse(fechaPrevista);

            // Verificar sesión
            DTUsuario usuario = (DTUsuario) session.getAttribute("usuarioLogueado");
            if (usuario == null || "visitante".equals(usuario.getTipo())) {
                model.addAttribute("mensaje", "⚠️ Debe estar logueado para crear una propuesta");
                return "redirect:/login";
            }

            String proponente = usuario.getNickname();
            String imagen = null;

            // 📸 Guardar la imagen si fue subida
            if (imagenFile != null && !imagenFile.isEmpty()) {
                try {
                    // Carpeta donde se guardan las imágenes
                    Path directorio = Paths.get(System.getProperty("user.dir"), "uploads", "imagenes");
                    if (!Files.exists(directorio)) {
                        Files.createDirectories(directorio);
                    }

                    // Nombre único del archivo
                    String nombreArchivo = "propuesta_" +
                            titulo.replaceAll("\\s+", "_") + "_" +
                            System.currentTimeMillis() + "_" +
                            imagenFile.getOriginalFilename();

                    // Ruta absoluta donde se guarda el archivo
                    Path rutaCompleta = directorio.resolve(nombreArchivo);

                    // Copiar archivo al directorio
                    Files.copy(imagenFile.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);

                    // Guardar solo la ruta relativa (para BD)
                    imagen = "uploads/imagenes/" + nombreArchivo;

                    System.out.println("✅ Imagen guardada en: " + rutaCompleta.toAbsolutePath());
                } catch (IOException e) {
                    model.addAttribute("mensaje", "⚠️ Error al guardar la imagen");
                    e.printStackTrace();
                    return "altaPropuesta";
                }
            }

            // Convertir los tipos de retorno a EnumSet
            EnumSet<TipoRetorno> tiposRet = Arrays.stream(tiposRetorno)
                    .map(TipoRetorno::valueOf)
                    .collect(Collectors.toCollection(() -> EnumSet.noneOf(TipoRetorno.class)));

            // Llamadas al controlador lógico
            ctrl.altaPropuesta(
                    titulo, descripcion, lugar, fecha,
                    montoEntrada, montoNecesario,
                    tiposRet, imagen, proponente, categoria
            );
            ctrl.nuevoEstadoPropuesta(titulo, TipoEstado.INGRESADA, LocalDate.now(), LocalTime.now());

            model.addAttribute("mensaje", "✅ Propuesta registrada con éxito");
            return "exitoAltaPropuesta";

        } catch (PropuestaYaExiste e) {
            model.addAttribute("mensaje", "⚠️ La propuesta ya existe");

            model.addAttribute("titulo", titulo);
            model.addAttribute("descripcion", descripcion);
            model.addAttribute("lugar", lugar);
            model.addAttribute("fechaPrevista", fechaPrevista);
            model.addAttribute("categorias", ctrl.listarCategoriasWebCompletas());
            model.addAttribute("tiposRetorno", ctrl.getTiposRetorno());
            model.addAttribute("montoEntrada", montoEntrada);
            model.addAttribute("montoNecesario", montoNecesario);
            return "altaPropuesta";

        } catch (Exception e) {
            model.addAttribute("mensaje", "⚠️ Error inesperado al registrar la propuesta");
            e.printStackTrace();
            return "altaPropuesta";
        }
    }


    @PostMapping("/cancelar/{titulo}")
    public String cancelarPropuesta(Model model, @PathVariable String titulo, HttpSession session) {
        DTUsuario usuario = (DTUsuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || "visitante".equals(usuario.getTipo())) {
            model.addAttribute("mensaje", "⚠️ Debe estar logueado para cancelar una propuesta");
            return "redirect:/login";
        }
        try{
            ctrl.nuevoEstadoPropuesta(titulo, TipoEstado.CANCELADA, LocalDate.now(), LocalTime.now());
            model.addAttribute("mensaje", "Propuesta cancelada con éxito");
            return "exitoCancelarPropuesta";
        }
        catch(Exception e){
            model.addAttribute("mensaje", "⚠️ No se pudo cancelar la propuesta");
            return "redirect:/propuestas/" + titulo;
        }
    }

    @GetMapping("/{titulo}")
    public String mostrarPropuesta(Model model, @PathVariable String titulo){
        if(ctrl.getDTPropuesta(titulo) == null){
            model.addAttribute("mensaje", "⚠️ La propuesta no existe");
            return "redirect:/propuestas/buscar";
        }
        model.addAttribute("propuesta", ctrl.getDTPropuesta(titulo));
        return  "consultarPropuesta";
    }

    @GetMapping("/buscar/sugerencias")
    @ResponseBody
    public List<String> sugerencias(@RequestParam("q") String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        List<DTPropuesta> resultados = ctrl.buscarPropuestas(query);
        if (resultados == null) return List.of();

        // Filtrar: solo coincidencias en el título
        String qLower = query.toLowerCase();

        return resultados.stream()
                .map(DTPropuesta::getTitulo)
                .filter(titulo -> titulo != null && titulo.toLowerCase().contains(qLower))
                .distinct()
                .limit(10)
                .toList();
    }

    @PostMapping("/extender/{titulo}")
    public String extenderFinanciacion(Model model, @PathVariable String titulo, HttpSession session) {
        DTUsuario usuario = (DTUsuario) session.getAttribute("usuarioLogueado");
        if (usuario == null || "visitante".equals(usuario.getTipo())) {
            model.addAttribute("mensaje", "⚠️ Debe estar logueado para extender la financiación de una propuesta");
            return "redirect:/login";
        }
        try {
            // Calcular la fecha 30 días desde hoy
            LocalDate fecha = LocalDate.now().plusDays(30);
            ctrl.extenderFinanciacion(titulo, fecha);

            return "redirect:/propuestas/" + titulo + "?mensaje=Financiacion extendida con exito hasta " + fecha;

        } catch (Exception e) {
            return "redirect:/propuestas/" + titulo + "?error=No se pudo extender la financiacion: " + e.getMessage();
        }
    }

    @PostMapping("/agregarComentario")
    public String agregarComentario(
            @RequestParam String tituloPropuesta,
            @RequestParam(required = false) String texto,  // ← Hacerlo opcional
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        System.out.println("=== DEBUG AGREGAR COMENTARIO ===");
        System.out.println("tituloPropuesta: " + tituloPropuesta);
        System.out.println("texto: " + texto);

        try {
            DTUsuario usuario = (DTUsuario) session.getAttribute("usuarioLogueado");

            // Validaciones
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("mensajeError", "Debes estar logueado para comentar");
                return "redirect:/propuestas/" + tituloPropuesta;
            }

            if (!"colaborador".equals(usuario.getTipo())) {
                redirectAttributes.addFlashAttribute("mensajeError", "Solo los colaboradores pueden comentar");
                return "redirect:/propuestas/" + tituloPropuesta;
            }

            // Esta validación ahora va después porque texto puede ser null
            if (texto == null || texto.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("mensajeError", "El comentario no puede estar vacío");
                return "redirect:/propuestas/" + tituloPropuesta;
            }

            // Verificar que el colaborador haya colaborado con esta propuesta
            DTPropuesta propuesta = ctrl.getDTPropuesta(tituloPropuesta);
            if (propuesta == null) {
                redirectAttributes.addFlashAttribute("mensajeError", "La propuesta no existe");
                return "redirect:/propuestas/buscar";
            }

            if (!propuesta.getColaboradores().contains(usuario.getNickname())) {
                redirectAttributes.addFlashAttribute("mensajeError", "Debes ser colaborador de esta propuesta para comentar");
                return "redirect:/propuestas/" + tituloPropuesta;
            }

            // Llamar a tu método ya implementado
            ctrl.agregarComentario(texto, usuario.getNickname(), tituloPropuesta);

            redirectAttributes.addFlashAttribute("mensajeExito", "Comentario agregado correctamente");
            return "redirect:/propuestas/" + tituloPropuesta;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al agregar comentario: " + e.getMessage());
            return "redirect:/propuestas/" + tituloPropuesta;
        }
    }

}