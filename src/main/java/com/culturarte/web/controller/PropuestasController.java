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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
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
            @RequestParam(value = "orden", required = false) String orden,
            Model model) {

        List<DTPropuesta> resultados = ctrl.buscarPropuestas(query);
        if (resultados == null) resultados = List.of();

        // 🔹 Aplicar filtros secundarios
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

        model.addAttribute("resultados", resultados);
        model.addAttribute("query", query);
        model.addAttribute("estado", estado);
        model.addAttribute("orden", orden);

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
            @RequestParam(required = false) String imagen,
            HttpSession session,
            Model model
    ){
        try{
            //Convierte fecha de String a LocalDate
            LocalDate fecha = LocalDate.parse(fechaPrevista);

            //Obtengo el usuario
            DTUsuario usuario = (DTUsuario) session.getAttribute("usuarioLogueado");

            if (usuario == null || "visitante".equals(usuario.getTipo())) {
                model.addAttribute("mensaje", "⚠️ Debe estar logueado para crear una propuesta");
                return "redirect:/login";
            }

            String proponente = usuario.getNickname();

            //Convierte tipoRetorno de String a EnumSet
            EnumSet<TipoRetorno> tiposRet = Arrays.stream(tiposRetorno)
                    .map(TipoRetorno::valueOf)
                    .collect(Collectors.toCollection(() -> EnumSet.noneOf(TipoRetorno.class)));

            ctrl.altaPropuesta(titulo, descripcion, lugar, fecha, montoEntrada, montoNecesario, tiposRet, imagen, proponente, categoria);
            ctrl.nuevoEstadoPropuesta(titulo, TipoEstado.INGRESADA, LocalDate.now(), LocalTime.now());
            model.addAttribute("mensaje", "Propuesta registrada con éxito");
            return "exitoAltaPropuesta";

        }catch(PropuestaYaExiste e){
            model.addAttribute("mensaje", "⚠️ " + "La Propuesta ya existe");
            //Guardamos los datos para que vuelvan al JSP
            model.addAttribute("titulo", titulo);
            model.addAttribute("descripcion", descripcion);
            model.addAttribute("lugar", lugar);
            model.addAttribute("fechaPrevista", fechaPrevista);
            model.addAttribute("categorias", ctrl.listarCategoriasWebCompletas());
            model.addAttribute("tiposRetorno" , ctrl.getTiposRetorno());
            // TODO : Hacer que se seleccione aca el tipoRetorno y categoria
            model.addAttribute("montoEntrada", montoEntrada);
            model.addAttribute("montoNecesario", montoNecesario);
            model.addAttribute("imagen", imagen);
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


}