package com.culturarte.web.controller;

import com.culturarte.exepciones.PropuestaYaExiste;
import com.culturarte.logica.IControlador;
import com.culturarte.logica.datatypes.DTPropuesta;
import com.culturarte.logica.datatypes.DTUsuario;
import com.culturarte.logica.enums.TipoEstado;
import com.culturarte.logica.enums.TipoRetorno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/registrarColaboracionProp")
    public String registrarColaboracionProp() {
        return "registrarColaboracionProp";
    }

     @GetMapping("/registroColaboracion")
    public String registroCol() {
        return "registroColaboracion";
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
                    .map(TipoRetorno::valueOf) // convierte String → Enum
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

    @GetMapping("/{titulo}")
    public String mostrarPropuesta(Model model, @PathVariable String titulo){
        // Verifiacar que existe
        model.addAttribute("propuesta", ctrl.getDTPropuesta(titulo));
        return  "consultarPropuesta";
    }

}