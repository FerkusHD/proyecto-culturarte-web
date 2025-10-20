package com.culturarte.logica;
import com.culturarte.exepciones.CargaFallida;
import com.culturarte.exepciones.CategoriaYaExiste;
import com.culturarte.exepciones.DatosIncorrectos;
import com.culturarte.exepciones.UsuarioYaExiste;
import com.culturarte.exepciones.PropuestaYaExiste;
import com.culturarte.exepciones.UsuarioNoSeguido;
import com.culturarte.exepciones.UsuarioYaSeguido;
import com.culturarte.logica.clases.*;
import com.culturarte.logica.datatypes.*;
import com.culturarte.logica.enums.*;
import com.culturarte.logica.manejadores.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;



@Service
public class Controlador implements IControlador{
    
    private final ManejadorPropuesta mp;
    private final ManejadorUsuario mu;
    private final ManejadorCategoria mc;
    private final ManejadorColaboracion mcol;

    public Controlador(ManejadorPropuesta mp, ManejadorUsuario mu, ManejadorCategoria mc, ManejadorColaboracion mcol) {
        this.mp = mp;
        this.mu = mu;
        this.mc = mc;
        this.mcol = mcol;
    }
    
    @Override
    public void altaColaborador(String nickname, String password, String nombre, String apellido, String email, LocalDate fechaNacimiento, String imagen)
            throws UsuarioYaExiste {
        Usuario u = mu.buscarUsuario(nickname);
        if (u != null) {
            throw new UsuarioYaExiste("El usuario con nickname " + nickname + " ya está registrado");
        }
        mu.agregarUsuario(new Colaborador(nickname, password, nombre, apellido, email, fechaNacimiento, imagen));
    }
    
    @Override
    public void altaProponente(String nickname, String password, String nombre, String apellido, String email, LocalDate fechaNacimiento, String imagen, String direccion, String linkWeb, String bibliografia)
            throws UsuarioYaExiste {
        Usuario u = mu.buscarUsuario(nickname);
        if (u != null) {
            throw new UsuarioYaExiste("El usuario con nickname " + nickname + " ya está registrado");
        }
        mu.agregarUsuario(new Proponente(nickname, password, nombre, apellido, email, fechaNacimiento, imagen, direccion, linkWeb, bibliografia));
    }
        
    @Override
    public ArrayList<String> getNickColaboradores(){
        ArrayList<String> retorno = new ArrayList<>();
        
        for (Usuario usu : mu.listarUsuarios()) {
            if (usu instanceof Colaborador) {
                retorno.add(usu.getNickname());
            }
        }
        
        retorno.sort(String.CASE_INSENSITIVE_ORDER); // Ordena la lista
        return retorno;   
    }

    @Override
    public ArrayList<String> getNomProponentes(){
        ArrayList<String> retorno = new ArrayList<>();
        
        for (Usuario usu : mu.listarUsuarios()) {
            if (usu instanceof Proponente) {
                retorno.add(usu.getNickname());
            }
        }
        
        retorno.sort(String.CASE_INSENSITIVE_ORDER); // Ordena la lista
        return retorno;        
    }

    @Override
    public ArrayList<String> getNomColaboradores(){
        ArrayList<String> retorno = new ArrayList<>();
        for (Usuario usu : mu.listarUsuarios()) {
            if(usu instanceof Colaborador){
                retorno.add(usu.getNombre());
            }
        }
        retorno.sort(String.CASE_INSENSITIVE_ORDER);
        return retorno;
    }

    @Override
    public DTColaborador getDTColaborador(String nickname) {
        Colaborador c = (Colaborador) mu.buscarUsuario(nickname); 

        DTColaborador dtc = new DTColaborador(
                c.getNickname(),
                c.getPassword(),
                c.getNombre(),
                c.getApellido(),
                c.getEmail(),
                c.getFechaNacimiento(),
                c.getImagen()
        );

        ArrayList<DTColaboracion> colaboraciones = new ArrayList<>();
        for (Colaboracion colab : c.getColaboraciones()) {
            Propuesta prop = mp.getPropuesta(colab.getPropuesta().getTitulo());
            if (prop != null) {
                DTPropuesta dtp = new DTPropuesta(prop);
                colaboraciones.add(new DTColaboracion(colab.getMonto(), colab.getFechaAporte(), colab.getTipoRetorno(), dtp));
            }
        }

        dtc.setColaboraciones(colaboraciones);
        return dtc;
    }
    
    @Override
    public DTProponente getDTProponente(String nickname){
        // Datos usuario, datos proponente, Propuestas (nombre, estado, lista colaboradores, monto recaudado, monto necesario)
       
        Proponente p = mu.getProponenteConPropuestas(nickname);
        
        DTProponente dtp = new DTProponente(p.getNickname(), p.getPassword(), p.getNombre(), p.getApellido(), p.getEmail(), p.getFechaNacimiento(), p.getImagen(), p.getDireccion(), p.getLinkWeb(), p.getBiografia());
        
        for (Propuesta prop : p.getPropuestas()) {
            dtp.addPropuesta(new DTPropuesta(prop));
        }
       
        return dtp;
    }

    @Override
    public void altaCategoria(String nombre, String catPadre) throws CategoriaYaExiste {

        if (mc.buscar(nombre) != null) {
            throw new CategoriaYaExiste("La categoría ya existe");
        }
        if (catPadre == null) {
            mc.alta(new Categoria(nombre, null));
        } else {
            mc.agregarSubcategoria(nombre, catPadre);
        }
    }
    
    @Override
    public List<String> listarCategoriasWeb() {
        List<Categoria> categoriasRaiz = mc.getCategoriasRaizConSubcategorias();
        List<String> nombres = new ArrayList<>();
        for (Categoria cat : categoriasRaiz) {
            nombres.add(cat.getNombre());
        }
        return nombres;
    }

    @Override
    public List<String> listarCategoriasWebCompletas(){
        List<Categoria> categoriasRaiz = mc.getCategoriasRaizConSubcategorias();
        List<Categoria> subCategorias;
        List<String> nombres = new ArrayList<>();
        for (Categoria cat : categoriasRaiz) {
            nombres.add(cat.getNombre());
            subCategorias = cat.getSubCategorias();
                for (Categoria subCat : subCategorias) {
                    nombres.add(subCat.getNombre());
                    nombres.addAll(getSubCategorias(subCat));
                }
        }
        return nombres;
    }

    private List<String> getSubCategorias(Categoria cat) {
        List<String> nombres = new ArrayList<>();
        if (cat.getSubCategorias() != null) {
            for (Categoria subCat : cat.getSubCategorias()) {
                nombres.add(subCat.getNombre());
                nombres.addAll(getSubCategorias(subCat));
            }
        }
        return nombres;
    }

    @Override
    public DefaultTreeModel listarCategorias() {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Categorías");
        DefaultTreeModel model = new DefaultTreeModel(raiz);

        List<Categoria> categoriasRaiz = mc.getCategoriasRaizConSubcategorias();
        for (Categoria cat : categoriasRaiz) {
            raiz.add(crearNodo(cat));
        }   
        return model;
    }
    
    private DefaultMutableTreeNode crearNodo(Categoria cat) {
        DefaultMutableTreeNode nodo = new DefaultMutableTreeNode(cat.getNombre());

        // Inicializamos subcategorías mientras el EntityManager está abierto
        if (cat.getSubCategorias() != null) {
            cat.getSubCategorias().size(); // fuerza carga
            for (Categoria sub : cat.getSubCategorias()) {
                nodo.add(crearNodo(sub));
            }
        }
        return nodo;
    }

    @Override
    public void altaPropuesta(String titulo, String descripcion, String lugar, LocalDate fechaPrevista, Float precioEntrada, Float montoNecesario, EnumSet<TipoRetorno> tipoRetornos, String imagen, String proponente, String categoria, LocalDate fechaActual, LocalTime horaActual)
    throws PropuestaYaExiste {

        if (mp.getPropuesta(titulo) != null) {
            throw new PropuestaYaExiste("Ya existe esta Propuesta");
        }
        
        Proponente u = (Proponente) mu.buscarUsuario(proponente);
        
        Categoria c = mc.buscar(categoria);
        
        Propuesta p = new Propuesta(titulo,descripcion,lugar,fechaPrevista, precioEntrada, montoNecesario, tipoRetornos, imagen, u,c);

        // INGRESADA
        mp.agregarPropuesta(p);

        nuevoEstadoPropuesta(titulo, TipoEstado.INGRESADA, fechaActual, horaActual);
    }

    @Override
    public ArrayList<String> getTituloPropuestas(){
        
       ArrayList<String> retorno = new ArrayList<>();
       for (Propuesta p : mp.getPropuestas()){
        retorno.add(p.getTitulo());
    }
       retorno.sort(String.CASE_INSENSITIVE_ORDER);
       return retorno;
    }
     
    @Override
    public ArrayList<DTPropuesta> getDTPropuestas(){
        ArrayList<DTPropuesta> retorno = new ArrayList<>();
        for(Propuesta p : mp.getPropuestas()){
            DTPropuesta dtp = new DTPropuesta( p.getTitulo(),
                    p.getDescripcion(),
                    p.getLugar(),
                    p.getFechaPrevista(),
                    p.getPrecioEntrada(),
                    p.getMontoNecesario()
            );
            retorno.add(dtp);
        }
        return retorno;
    }

    @Override
    public ArrayList<DTPropuesta> getDTPropuestasWeb(){
        ArrayList<DTPropuesta> retorno = new ArrayList<>();
        for(Propuesta p : mp.getPropuestas()){
            if (p.getEstadoActual().getEstado() != TipoEstado.INGRESADA){
                DTPropuesta dtp = new DTPropuesta( p.getTitulo(),
                        p.getDescripcion(),
                        p.getEstadoActual().getEstado(),
                        p.getNicknameColaboradores().size(),
                        p.getMontoRecaudado(),
                        p.getMontoNecesario(),
                        p.getFechaPrevista(),
                        p.getImagen(),
                        p.getCategoria().getNombreCompleto(),
                        p.getProponenteNick()
                );
                retorno.add(dtp);
            }
        }
        return retorno;
    }


    @Override
    public DTPropuesta getDTPropuesta(String titulo){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        Propuesta p = mp.getPropuesta(titulo);
        String nombreCategoria = "Sin categoría";
        DTPropuesta dtp = new DTPropuesta();
        ArrayList<DTEstado> histEstado = new ArrayList<>();
        if(p != null){
            if (p.getCategoria() != null) {
                nombreCategoria = p.getCategoria().getNombreCompleto();
            }
            for (Estado est : p.getHistorialEstados()) {
                histEstado.add(new DTEstado(est.getEstado().toString(), est.getFecha().toString(), est.getHora().format(formatter)));
            }
            ArrayList<DTComentario> comentariosDT = new ArrayList<>();

            if (p.getComentarios() != null) {
                for (Comentario comentario : p.getComentarios()) {

                    Colaborador colab = comentario.getColaborador();
                    DTColaborador dtColaborador = new DTColaborador(
                            colab.getNickname(),
                            colab.getPassword(),
                            colab.getNombre(),
                            colab.getApellido(),
                            colab.getEmail(),
                            colab.getFechaNacimiento(),
                            colab.getImagen()
                    );

                    DTPropuesta dtPropSimple = new DTPropuesta(
                            p.getTitulo(),
                            p.getDescripcion(),
                            p.getLugar(),
                            p.getFechaPrevista(),
                            p.getPrecioEntrada(),
                            p.getMontoNecesario()
                    );

                    DTComentario dtComentario = new DTComentario(
                            comentario.getTexto(),
                            dtColaborador,
                            dtPropSimple,
                            comentario.getFecha()
                    );
                    comentariosDT.add(dtComentario);
                }
            }

            dtp = new DTPropuesta(p.getTitulo(), p.getDescripcion(), p.getLugar(), p.getFechaPrevista(), p.getPrecioEntrada(), p.getMontoNecesario(), p.getImagen(), p.getNicknameColaboradores(), p.getProponenteNick(), p.getEstadoActual().getEstado(), nombreCategoria, histEstado, p.getMontoRecaudado(), comentariosDT);
        }
        return dtp;
    }

    @Override
    public boolean colaboradorPuedeComentar(String colaborador, String tituloPropuesta) {
        Colaborador c = (Colaborador) mu.buscarUsuario(colaborador);
        Propuesta p = mp.getPropuesta(tituloPropuesta);
        if (p.getEstadoActual().getEstado() != TipoEstado.FINANCIADA) return false;

        for (Comentario comentario : p.getComentarios()) {
            if(comentario.getColaborador() == c) {
                return false;
            }
        }
        return true;
    }
        
    
    @Override
    public ArrayList<String> getTituloPropuestasPorEstado(TipoEstado estado){

        ArrayList<String> retorno = new ArrayList<>();
        
        for (Propuesta p : mp.getPropuestas()) {
            if (p.getEstadoActual().getEstado() == estado)
                retorno.add(p.getTitulo());
        }
        
        return retorno;
    }
    
    @Override 
    public void altaColaboracion( float monto, LocalDate fecha, LocalTime hora, TipoRetorno tipoRetorno, String tituloPropuesta, String nickColaborador){

        // 🔑 Cargar propuesta con sus colaboraciones
        Propuesta p = mp.getPropuesta(tituloPropuesta);
        if (p == null) throw new IllegalArgumentException("No existe la propuesta: " + tituloPropuesta);

        // 🔑 Cargar colaborador con sus colaboraciones
        Colaborador c = (Colaborador) mu.buscarUsuario(nickColaborador);
        if (c == null) throw new IllegalArgumentException("No existe el colaborador: " + nickColaborador);
        if (p.getEstadoActual().getEstado() == TipoEstado.PUBLICADA || p.getEstadoActual().getEstado() == TipoEstado.ENFINANCIACION ) {
            // Crear la colaboración
            Colaboracion colab = new Colaboracion(monto, fecha, hora, tipoRetorno, p, c);

            // Asociar bidireccionalmente
            p.addColaboracion(colab);
            c.addColaboracion(colab);

            // Persistir colaboración
            mcol.agregarColaboracion(colab);

            if (p.getEstadoActual().getEstado() == TipoEstado.PUBLICADA) {
                nuevoEstadoPropuesta(p.getTitulo(), TipoEstado.ENFINANCIACION, fecha, hora);
            }
            if (p.getMontoRecaudado() >= p.getMontoNecesario()) {
                nuevoEstadoPropuesta(p.getTitulo(), TipoEstado.FINANCIADA, fecha, hora);
            }
        } else {
            throw new IllegalArgumentException("El estado de esta propuesta no permite colaboraciones: " + p.getEstadoActual().getEstado());
        }

    }

    @Override 
    public  ArrayList<String> getNickUsuarios() {
        ArrayList<String> retorno = new ArrayList<>();
        for(Usuario u : mu.listarUsuarios()){
            retorno.add(u.getNickname());
        }
        retorno.sort(String.CASE_INSENSITIVE_ORDER);//Ordena la lista
        return retorno;
    }
    
    @Override 
    public  void seguirUsuario(String nickSeguidor, String nickSeguido) throws UsuarioYaSeguido {
        
        Usuario seguidor = mu.buscarUsuario(nickSeguidor);
        Usuario seguido = mu.buscarUsuario(nickSeguido);
        
        if (seguidor == null || seguido == null) {
            throw new UsuarioYaSeguido("Uno de los usuarios no existe.");
        }
        
        if(seguidor.getUsuariosSeguidos().contains(seguido)){
            throw new UsuarioYaSeguido("El usuario con nickname: " + nickSeguidor + "\nYa está siguiendo a usuario con nickname: " + nickSeguido);
        }
        
        seguidor.addUsuariosSeguidos(seguido);
        seguido.addUsuarioSeguidor(seguidor);
        mu.actualizarUsuario(seguidor);
        mu.actualizarUsuario(seguido);
    }
    
    @Override 
    public  void dejarDeSeguirUsuario(String nickSeguidor, String nickSeguido) throws UsuarioNoSeguido {
        Usuario seguidor = mu.buscarUsuario(nickSeguidor);
        Usuario seguido = mu.buscarUsuario(nickSeguido);
        if(seguidor.getUsuariosSeguidos().contains(seguido)){
            seguidor.getUsuariosSeguidos().remove(seguido);
            seguido.getUsuariosSeguidores().remove(seguidor);
            mu.actualizarUsuario(seguidor);
            mu.actualizarUsuario(seguido);
        }else{
            throw new UsuarioNoSeguido("El usuario con nickname: " + nickSeguidor + ", no sigue al usuario con nickname: " + nickSeguido);
        }
    }

    @Override
    public DTUsuario getDTUsuario(String nickname) {
        Usuario usu = mu.buscarUsuario(nickname);

        // if (usu == null) usu = mu.buscarUsuarioPorEmail(nickname);
        if (usu == null) return null;

        ArrayList<DTUsuario> usuariosSeguidos = new ArrayList<>();
        for(Usuario u : usu.getUsuariosSeguidos()){
            String tipo = null;
            if(u instanceof Colaborador){
                tipo = "colaborador";
            } else if (u instanceof  Proponente){
                tipo = "proponente";
            }
            usuariosSeguidos.add(new DTUsuario(u.getNickname(), tipo, u.getImagen()));
        }

        ArrayList<DTUsuario> usuariosSeguidores = new ArrayList<>();
        for(Usuario u : usu.getUsuariosSeguidores()){
            String tipo = null;
            if(u instanceof Colaborador){
                tipo = "colaborador";
            } else if (u instanceof  Proponente){
                tipo = "proponente";
            }
            usuariosSeguidores.add(new DTUsuario(u.getNickname(), tipo, u.getImagen()));
        }

        ArrayList<DTPropuesta> propuestasSeguidas = new ArrayList<>();
        for(Propuesta p : usu.getPropuestasSeguidas()) {
            propuestasSeguidas.add(getDTPropuesta(p.getTitulo()));
        }

        String tipo = null;
        if(usu instanceof Colaborador){
            tipo = "colaborador";
        } else if (usu instanceof  Proponente){
            tipo = "proponente";
        }

        DTUsuario dtu = new DTUsuario(usu.getNickname(), usu.getNombre(), usu.getApellido(), usu.getEmail(), usu.getFechaNacimiento(), usuariosSeguidos, usuariosSeguidores, tipo, usu.getImagen(), propuestasSeguidas);

        return dtu;
    }

    public boolean verificarPassword(String password, String nick) {
        Usuario usu = mu.buscarUsuario(nick);
        // if (usu == null) usu = mu.buscarUsuarioPorEmail(nick);
        if (usu == null) {
            return false;
        } else {
            return (usu.getPassword().equals(password));
        }

    }
    
    @Override
    public void cancelarColaboracionPropuesta(String tituloPropuesta, String nickColaborador){
        Propuesta p = mp.getPropuesta(tituloPropuesta);
        Colaborador c = (Colaborador) mu.buscarUsuario(nickColaborador);

        Colaboracion colabAEliminar = null;
        for (Colaboracion colab : p.getColaboraciones()) {
            if (colab.getColaborador().equals(c)) {
                colabAEliminar = colab;
                break;
            }
        }

        if (colabAEliminar != null) {
            p.getColaboraciones().remove(colabAEliminar);
            c.getColaboraciones().remove(colabAEliminar);
            mcol.eliminarColaboracion(colabAEliminar.getId());
        } else {
            throw new IllegalArgumentException("El colaborador no tiene colaboración en esta propuesta");
        }
    }

    @Override
    public ArrayList<DTColaboracion> getDTColaboracionesPropuestas(String nickColab){
        Colaborador c = (Colaborador) mu.buscarUsuario(nickColab);
        
        ArrayList<DTColaboracion> ret = new ArrayList<>();
        
        for (Colaboracion colab : c.getColaboraciones()) {
            ret.add(new DTColaboracion(colab.getColaborador().getNickname(),colab.getPropuesta().getTitulo(),colab.getFechaAporte(), colab.getHoraAporte(),colab.getMonto(),colab.getTipoRetorno()));
        }
        
        return ret;
    }
    
    @Override
    public DTColaboracion getDTColaboracionPropuesta(String nickColab, String tituloProp){
        
        for (DTColaboracion colab : this.getDTColaboracionesPropuestas(nickColab)) {
            if (colab.getPropuestaTitulo().equals(tituloProp)) {
                return colab;
            }
        }
        return null;
    }
    
    @Override
    public ArrayList<DTColaboracion> getDTColaboraciones(){
        
        ArrayList<DTColaboracion> ret = new ArrayList<>();
        
        for (Propuesta p : mp.getPropuestas()) {
            if (p.getColaboraciones() != null)
                for(Colaboracion c : p.getColaboraciones()){
                    ret.add(new DTColaboracion(c.getColaborador().getNickname(),c.getPropuesta().getTitulo(),c.getFechaAporte(), c.getHoraAporte(),c.getMonto(),c.getTipoRetorno()));
                }
        }
        return ret;
    }
    
    @Override
    public void nuevoEstadoPropuesta(String propuesta, TipoEstado estado, LocalDate fecha, LocalTime hora) {
        Estado nuevoEstado = new Estado(fecha, hora, estado);
        Propuesta p = mp.getPropuesta(propuesta);
        p.agregarEstado(nuevoEstado);
        mp.actualizarPropuesta(p);
    }
    
    @Override 
    public String getNickProponente(String tituloPropuesta){
        Propuesta p = mp.getPropuesta(tituloPropuesta);
        
        return p.getProponente().getNickname();
    }

    @Override
    public List<DTPropuesta> buscarPropuestas(String texto) {
        return mp.buscarPropuestas(texto)
                .stream()
                .filter(p -> p.getEstadoActual().getEstado() != TipoEstado.INGRESADA)
                .map(DTPropuesta::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<DTUsuario> buscarUsuarios(String nombre) {
        return mu.buscarUsuarios(nombre)
                .stream()
                .map(DTUsuario::new)
                .collect(Collectors.toList());
    }


    @Override
    public void agregarComentario(String texto, String nickColaborador, String tituloPropuesta) {
        Colaborador c = (Colaborador) mu.buscarUsuario(nickColaborador);
        Propuesta p = mp.getPropuesta(tituloPropuesta);
        if (c == null || p == null) {
            throw new IllegalArgumentException("Colaborador o Propuesta no encontrados");
        }
        Comentario comentario = new Comentario(texto, c, p, LocalDate.now());
        p.agregarComentario(comentario);
        c.agregarComentario(comentario);
        mp.actualizarPropuesta(p);
    }

    @Override
    public void modificarPropuesta(String titulo, String descripcion, String lugar, LocalDate fechaPrevista, Float precioEntrada, 
            Float montoNecesario, String imagen, String proponente, String categoria, String nuevoEstado) throws DatosIncorrectos {
        
        if(titulo.isEmpty()) throw new DatosIncorrectos("El titulo no puede ser vacío");
        if(lugar.isEmpty()) throw new DatosIncorrectos("El lugar no puede ser vacío");
        if(fechaPrevista == null) throw new DatosIncorrectos("La fecha no puede ser vacía");
        if(proponente.isEmpty()) throw new DatosIncorrectos("El proponente no puede ser vacío");
        if(categoria.isEmpty()) throw new DatosIncorrectos("La categoría no puede ser vacía");
        if(nuevoEstado.isEmpty()) throw new DatosIncorrectos("El estado no puede ser vacío");
        
        Propuesta p = mp.getPropuesta(titulo);
        if (p == null) {
            throw new DatosIncorrectos("La propuesta no existe");
        }
        
        //Datos básicos
        p.setDescripcion(descripcion);
        p.setLugar(lugar);
        p.setFechaPrevista(fechaPrevista);
        p.setPrecioEntrada(precioEntrada);
        p.setMontoNecesario(montoNecesario);
        p.setImagen(imagen);
        
        // Proponente
        Proponente viejoProponente = p.getProponente();
        if(!viejoProponente.getNickname().equals(proponente)) {
            Proponente nuevoProponente = mu.getProponenteConPropuestas(proponente);
            viejoProponente.removePropuesta(p);
            nuevoProponente.addPropuestas(p);
            p.setProponente(nuevoProponente);
        }
        
        // Categoria
        if(!p.getCategoria().getNombre().equals(categoria)) {
            Categoria nuevaCat = mc.buscar(categoria);
            p.setCategoria(nuevaCat);
        }
        
        // Estado
        if(!p.getEstadoActual().toString().equals(nuevoEstado)) {
            nuevoEstadoPropuesta(p.getTitulo(), TipoEstado.valueOf(nuevoEstado), LocalDate.now(), LocalTime.now());
        }
        
        mp.actualizarPropuesta(p);
    }

    @Override
    public String[] getTiposRetorno() {
        String[] retorno = new String[TipoRetorno.values().length];
        for (int i = 0; i < TipoRetorno.values().length; i++) {
            retorno[i] = TipoRetorno.values()[i].toString();
        }
        return retorno;
    }

    @Override
    public void extenderFinanciacion(String tituloPropuesta, LocalDate nuevaFecha){
        Propuesta p = mp.getPropuesta(tituloPropuesta);
        p.setFechaPrevista(nuevaFecha);
        mp.actualizarPropuesta(p);
    }


    @Override
    public void agregarPropuestaFavorita(String nickUsuario, String tituloPropuesta) {
        Usuario u = mu.buscarUsuario(nickUsuario);
        Propuesta p = mp.getPropuesta(tituloPropuesta);
        if (u == null || p == null) {
            throw new IllegalArgumentException("Usuario o Propuesta no encontrados");
        }
        u.agregarPropuestaFavorita(p);
        mu.actualizarUsuario(u);
    }

    @Override
    public void sacarPropuestaFavorita(String nickUsuario, String tituloPropuesta) {
        Usuario u = mu.buscarUsuario(nickUsuario);
        Propuesta p = mp.getPropuesta(tituloPropuesta);
        if (u == null || p == null) {
            throw new IllegalArgumentException("Usuario o Propuesta no encontrados");
        }
        u.sacarPropuestaFavorita(p);
        mu.actualizarUsuario(u);
    }

}
