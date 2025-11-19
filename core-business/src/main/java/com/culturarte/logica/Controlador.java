package com.culturarte.logica;
import com.culturarte.exepciones.CargaFallida;
import com.culturarte.exepciones.CategoriaYaExiste;
import com.culturarte.exepciones.DatosIncorrectos;
import com.culturarte.exepciones.EmailYaExiste;
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

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;



@Service
public class Controlador implements IControlador{
    
    private final ManejadorPropuesta mp;
    private final ManejadorUsuario mu;
    private final ManejadorCategoria mc;
    private final ManejadorColaboracion mcol;
    private final ManejadorAcceso ma;

    public Controlador(ManejadorPropuesta mp, ManejadorUsuario mu, ManejadorCategoria mc, ManejadorColaboracion mcol, ManejadorAcceso ma) {
        this.mp = mp;
        this.mu = mu;
        this.mc = mc;
        this.mcol = mcol;
        this.ma = ma;
        
    }

    @Override
    public List<DTAcceso> getAccesos() {
        return ma.obtenerAccesosOrdenados();
    }

    @Override
    public ArrayList<DTUsuario> listarUsuarios() {
        ArrayList<DTUsuario> u = new ArrayList<>();

        for (Usuario usuario : mu.listarUsuarios()) {
            DTUsuario dtUsuario = new DTUsuario(
                    usuario.getNickname(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getEmail(),
                    usuario.getFechaNacimiento(),
                    usuario.getImagen(),
                    usuario.getUsuariosSeguidores().size()
            );
            u.add(dtUsuario);
        }

        u.sort((a, b) -> Integer.compare(b.getUsuariosSeguidores().size(), a.getUsuariosSeguidores().size()));

        return u;
    }

    @Override
    public void altaColaborador(String nickname, String password, String nombre, String apellido, String email, LocalDate fechaNacimiento, String imagen)
            throws UsuarioYaExiste, EmailYaExiste {
        Usuario u = mu.buscarUsuario(nickname);
        if (u != null) {
            throw new UsuarioYaExiste("El usuario con nickname " + nickname + " ya está registrado");
        }

        u=mu.buscarUsuarioPorEmail(email);

        if (u!=null) {
            throw new EmailYaExiste("El usuario con email " + email + " ya está registrado");
        }

        mu.agregarUsuario(new Colaborador(nickname, password, nombre, apellido, email, fechaNacimiento, imagen));
    }
    
    @Override
    public void altaProponente(String nickname, String password, String nombre, String apellido, String email, LocalDate fechaNacimiento, String imagen, String direccion, String linkWeb, String bibliografia)
            throws UsuarioYaExiste,EmailYaExiste {
        Usuario u = mu.buscarUsuario(nickname);
        if (u != null) {
            throw new UsuarioYaExiste("El usuario con nickname " + nickname + " ya está registrado");
        }

         u=mu.buscarUsuarioPorEmail(email);

        if (u!=null) {
            throw new EmailYaExiste("El usuario con email " + email + " ya está registrado");
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

        if (usu == null) usu = mu.buscarUsuarioPorEmail(nickname);
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
    
     @Override
    public boolean verificarPassword(String password, String nick) {
        Usuario usu = mu.buscarUsuario(nick);
        
        if (usu == null) { usu = mu.buscarUsuarioPorEmail(nick); }
        if(usu == null) { return false;}
        return (usu.getPassword().equals(password));
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

    @Transactional
    public void cargarDatosPrueba() throws CargaFallida{
        System.out.println("Agregando datos de prueba: ...");
        try {
            // Proponentes
            this.altaProponente(
                    "hrubino","a", "Horacio", "Rubino",
                    "horacio.rubino@guambia.com.uy", LocalDate.of(1962, 2, 25),
                    "uploads/imagenes/HR.jpeg",
                    "18 de Julio 1234",
                    "https://twitter.com/horaciorubino",
                    "Actor y conductor"
            );
            this.altaProponente(
                    "mbusca", "a","Martín", "Buscaglia",
                    "martin.bus@agadu.org.uy", LocalDate.of(1972, 6, 14),
                    "uploads/imagenes/MB.jpg",
                    "Colonia 4321",
                    "http://www.martinbuscaglia.com/",
                    "Músico uruguayo"
            );
            this.altaProponente(
                    "hectorg", "a","Héctor", "Guido",
                    "hector.gui@elgalpon.org.uy", LocalDate.of(1954, 1, 7),
                    null,
                    "Gral. Flores 5645",
                    "",
                    "Actor de teatro"
            );
            this.altaProponente(
                    "tabarec", "a","Tabaré", "Cardozo",
                    "tabare.car@agadu.org.uy", LocalDate.of(1971, 7, 24),
                    null,
                    "Santiago Rivas 1212",
                    "https://www.facebook.com/Tabaré-Cardozo-55179094281/?ref=br_rs",
                    "Cantante murguista"
            );
            this.altaProponente(
                    "cachilas", "a","Waldemar \"Cachila\"", "Silva",
                    "cachila.sil@c1080.org.uy", LocalDate.of(1947, 1, 1),
                    null,
                    "Br. Artigas 4567",
                    "https://www.facebook.com/C1080?ref=br_rs",
                    "Director comparsa"
            );
            this.altaProponente(
                    "juliob", "a","Julio", "Bocca",
                    "juliobocca@sodre.com.uy", LocalDate.of(1967, 3, 16),
                    null,
                    "Benito Blanco 4321",
                    "",
                    "Bailarín"
            );
            this.altaProponente(
                    "diegop", "a","Diego", "Parodi",
                    "diego@efectocine.com", LocalDate.of(1975, 1, 1),
                    null,
                    "Emilio Frugoni 1138 Ap. 02",
                    "http://www.efectocine.com",
                    "Cineasta"
            );
            this.altaProponente(
                    "kairoh", "a","Kairo", "Herrera",
                    "kairoher@pilsenrock.com.uy", LocalDate.of(1840, 4, 25),
                    null,
                    "Paraguay 1423",
                    "",
                    "Organizador eventos"
            );
            this.altaProponente(
                    "losBardo", "a","Los", "Bardo",
                    "losbardo@bardocientifico.com", LocalDate.of(1980, 10, 31),
                    "uploads/imagenes/LB.jpg",
                    "8 de Octubre 1429",
                    "https://bardocientifico.com/",
                    "Divulgación científica"
            );
            // Colaboradores
            this.altaColaborador(
                    "robinh", "a","Robin", "Henderson",
                    "robin.h@tinglesa.com.uy", LocalDate.of(1940, 8, 3),
                    null
            );
            this.altaColaborador(
                    "marcelot", "a","Marcelo", "Tinelli",
                    "marcelot@ideasdelsur.com.ar", LocalDate.of(1960, 4, 1),
                    null
            );
            this.altaColaborador(
                    "novick", "a","Edgardo", "Novick",
                    "edgardo@novick.com.uy", LocalDate.of(1952, 7, 17),
                    null
            );
            this.altaColaborador(
                    "sergiop", "a","Sergio", "Puglia",
                    "puglia@alpanpan.com.uy", LocalDate.of(1950, 1, 28),
                    "uploads/imagenes/SP.jpg"
            );
            this.altaColaborador(
                    "chino", "a","Alvaro", "Recoba",
                    "chino@trico.org.uy", LocalDate.of(1976, 3, 17),
                    null
            );
            this.altaColaborador(
                    "tonyp", "a","Antonio", "Pacheco",
                    "tonyp@manya.org.uy", LocalDate.of(1955, 2, 14),
                    "uploads/imagenes/AP.jpg"
            );
            this.altaColaborador(
                    "nicoJ", "a","Nicolás", "Jodal",
                    "jodal@artech.com.uy", LocalDate.of(1960, 8, 9),
                    "uploads/imagenes/NJ.jpg"
            );
            this.altaColaborador(
                    "juanP", "a","Juan", "Perez",
                    "juanp@elpueblo.com", LocalDate.of(1970, 1, 1),
                    null
            );
            this.altaColaborador(
                    "Mengano", "a","Mengano", "Gómez",
                    "menganog@elpueblo.com", LocalDate.of(1982, 2, 2),
                    null
            );
            this.altaColaborador(
                    "Perengano", "a","Perengano", "López",
                    "pere@elpueblo.com", LocalDate.of(1985, 3, 3),
                    null
            );
            this.altaColaborador(
                    "Tiajaci", "a","Tía", "Jacinta",
                    "jacinta@elpueblo.com", LocalDate.of(1990, 4, 4),
                    null
            );

            // HR sigue a: MB, HG, TC
            this.seguirUsuario("hrubino", "mbusca");
            this.seguirUsuario("hrubino", "hectorg");
            this.seguirUsuario("hrubino", "tabarec");

            // MB sigue a: HR, HG, TC

            this.seguirUsuario("mbusca", "hrubino");
            this.seguirUsuario("mbusca", "hectorg");
            this.seguirUsuario("mbusca", "tabarec");

            // HG sigue a: HR, TC
            this.seguirUsuario("hectorg", "hrubino");
            this.seguirUsuario("hectorg", "tabarec");

            // TC sigue a: HR, HG
            this.seguirUsuario("tabarec", "hrubino");
            this.seguirUsuario("tabarec", "hectorg");

            // CS sigue a: HR
            this.seguirUsuario("cachilas", "hrubino");

            // JB sigue a: HR, TC
            this.seguirUsuario("juliob", "hrubino");
            this.seguirUsuario("juliob", "tabarec");

            // DP sigue a: HR, TC
            this.seguirUsuario("diegop", "hrubino");
            this.seguirUsuario("diegop", "tabarec");

            // KH sigue a: HR
            this.seguirUsuario("kairoh", "hrubino");

            // LB sigue a: HR, TC
            this.seguirUsuario("losBardo", "hrubino");
            this.seguirUsuario("losBardo", "tabarec");

            // RH sigue a: HR, MB, HG
            this.seguirUsuario("robinh", "hrubino");
            this.seguirUsuario("robinh", "mbusca");
            this.seguirUsuario("robinh", "hectorg");

            // MT sigue a: HR, MB, HG
            this.seguirUsuario("marcelot", "hrubino");
            this.seguirUsuario("marcelot", "mbusca");
            this.seguirUsuario("marcelot", "hectorg");

            // EN sigue a: HR, MB, HG
            this.seguirUsuario("novick", "hrubino");
            this.seguirUsuario("novick", "mbusca");
            this.seguirUsuario("novick", "hectorg");

            // SP sigue a: HR, MB, HG
            this.seguirUsuario("sergiop", "hrubino");
            this.seguirUsuario("sergiop", "mbusca");
            this.seguirUsuario("sergiop", "hectorg");

            // AR sigue a: HR
            this.seguirUsuario("chino", "hrubino");

            // AP sigue a: HR
            this.seguirUsuario("tonyp", "hrubino");

            // NJ sigue a: HR, MB
            this.seguirUsuario("nicoJ", "hrubino");
            this.seguirUsuario("nicoJ", "mbusca");

            // JP sigue a: HR, MB, HG
            this.seguirUsuario("juanP", "hrubino");
            this.seguirUsuario("juanP", "mbusca");
            this.seguirUsuario("juanP", "hectorg");

            // MG sigue a: HR, MB, HG
            this.seguirUsuario("Mengano", "hrubino");
            this.seguirUsuario("Mengano", "mbusca");
            this.seguirUsuario("Mengano", "hectorg");

            // PL sigue a: HR, MB
            this.seguirUsuario("Perengano", "hrubino");
            this.seguirUsuario("Perengano", "mbusca");

            // TJ sigue a: HR, MB, HG
            this.seguirUsuario("Tiajaci", "hrubino");
            this.seguirUsuario("Tiajaci", "mbusca");
            this.seguirUsuario("Tiajaci", "hectorg");


            // Categorias
            this.altaCategoria("Teatro", null);
            this.altaCategoria("Literatura", null);
            this.altaCategoria("Música", null);
            this.altaCategoria("Cine", null);
            this.altaCategoria("Danza", null);
            this.altaCategoria("Carnaval", null);

            // Subcategorías de Teatro
            this.altaCategoria("Teatro Dramático", "Teatro");
            this.altaCategoria("Teatro Musical", "Teatro");
            this.altaCategoria("Comedia", "Teatro");
            this.altaCategoria("Stand-up", "Comedia");

            // Subcategorías de Música
            this.altaCategoria("Festival", "Música");
            this.altaCategoria("Concierto", "Música");

            // Subcategorías de Cine
            this.altaCategoria("Cine al Aire Libre", "Cine");
            this.altaCategoria("Cine a Pedal", "Cine");

            // Subcategorías de Danza
            this.altaCategoria("Ballet", "Danza");
            this.altaCategoria("Flamenco", "Danza");

            // Subcategorías de Carnaval
            this.altaCategoria("Murga", "Carnaval");
            this.altaCategoria("Humoristas", "Carnaval");
            this.altaCategoria("Parodistas", "Carnaval");
            this.altaCategoria("Lubolos", "Carnaval");
            this.altaCategoria("Revista", "Carnaval");

            // Propuestas
            this.altaPropuesta("Cine en el Botánico",
                    "El 16 de Diciembre a la hora 20 se proyectará la película \"Clever\", en el Jardín Botánico (Av. 19 de Abril 1181). Entrada libre. Organiza: Intendencia de Montevideo.",
                    "Jardín Botánico",
                    LocalDate.of(2025, 9, 16),
                    200f,
                    150000f,
                    EnumSet.of(TipoRetorno.PORCENTAJEGANANCIA),
                    null,
                    "diegop",
                    "Cine",
                    LocalDate.of(2025, 5, 15),
                    LocalTime.of(15, 30)
            );

            this.altaPropuesta("Religiosamente",
                    "MOMOSAPIENS presenta \"Religiosamente\", en el Teatro de Verano. El espectáculo se realiza el 7 de Octubre, hora 21.",
                    "Teatro de Verano",
                    LocalDate.of(2025, 10, 7),
                    300f,
                    300000f,
                    EnumSet.of(TipoRetorno.ENTRADAGRATIS, TipoRetorno.PORCENTAJEGANANCIA),
                    null,
                    "hrubino",
                    "Parodistas",
                    LocalDate.of(2025, 6, 18),
                    LocalTime.of(4, 28)
            );

            this.altaPropuesta("El Pimiento Indomable",
                    "El Pimiento Indomable, formación compuesta por Kiko Veneno y Martín Buscaglia, se presentará el 19 de Octubre en el Teatro Solís. Entradas por Tickantel.",
                    "Teatro Solís",
                    LocalDate.of(2025, 10, 19),
                    400f,
                    400000f,
                    EnumSet.of(TipoRetorno.PORCENTAJEGANANCIA),
                    "uploads/imagenes/PIM.jpg",
                    "mbusca",
                    "Concierto",
                    LocalDate.of(2025, 7, 26),
                    LocalTime.of(15, 30)
            );

            this.altaPropuesta("Pilsen Rock",
                    "La edición 2025 del Pilsen Rock se celebrará el 21 de Octubre en la Rural del Prado. Entradas a la venta en RedTickets.",
                    "Rural del Prado",
                    LocalDate.of(2025, 10, 21),
                    1000f,
                    900000f,
                    EnumSet.of(TipoRetorno.ENTRADAGRATIS, TipoRetorno.PORCENTAJEGANANCIA),
                    "uploads/imagenes/PIL.jpg",
                    "kairoh",
                    "Festival",
                    LocalDate.of(2025, 7, 30),
                    LocalTime.of(15, 40)
            );

            this.altaPropuesta("Romeo y Julieta",
                    "Romeo y Julieta de Kenneth MacMillan se presentará nuevamente el 5 de Noviembre en el Auditorio Nacional del Sodre. Entradas en Tickantel.",
                    "Auditorio Nacional del Sodre",
                    LocalDate.of(2025, 11, 5),
                    800f,
                    750000f,
                    EnumSet.of(TipoRetorno.PORCENTAJEGANANCIA),
                    null,
                    "juliob",
                    "Ballet",
                    LocalDate.of(2025, 8, 4),
                    LocalTime.of(12, 20)
            );

            this.altaPropuesta("Un día de Julio",
                    "La Catalina presenta el espectáculo \"Un Día de Julio\" en Landia. Función el 16 de Noviembre a las 21 hs.",
                    "Landia",
                    LocalDate.of(2025, 11, 16),
                    650f,
                    300000f,
                    EnumSet.of(TipoRetorno.ENTRADAGRATIS, TipoRetorno.PORCENTAJEGANANCIA),
                    "uploads/imagenes/UDJ.jpg",
                    "tabarec",
                    "Murga",
                    LocalDate.of(2025, 8, 6),
                    LocalTime.of(2, 0)
            );

            this.altaPropuesta("El Lazarillo de Tormes",
                    "Vuelve una de las producciones de El Galpón más aclamadas por crítica y público. Funciones desde el 3 de Diciembre.",
                    "Teatro el Galpón",
                    LocalDate.of(2025, 12, 3),
                    350f,
                    175000f,
                    EnumSet.of(TipoRetorno.ENTRADAGRATIS),
                    null,
                    "hectorg",
                    "Teatro Dramático",
                    LocalDate.of(2025, 8, 18),
                    LocalTime.of(2, 40)
            );

            this.altaPropuesta("Bardo en la FING",
                    "El 10 de Diciembre se presentará Bardo Científico en la FING, Anfiteatro del Edificio José Luis Massera. Entrada libre.",
                    "Anfiteatro Edificio José Luis Massera",
                    LocalDate.of(2025, 12, 10),
                    200f,
                    100000f,
                    EnumSet.of(TipoRetorno.ENTRADAGRATIS),
                    null,
                    "losBardo",
                    "Stand-up",
                    LocalDate.of(2025, 8, 23),
                    LocalTime.of(2, 12)
            );

            // Propuestas publicadas
            this.nuevoEstadoPropuesta("Cine en el Botánico", TipoEstado.PUBLICADA, LocalDate.of(2025, 6, 1), LocalTime.of(10, 15));
            this.nuevoEstadoPropuesta("Religiosamente", TipoEstado.PUBLICADA, LocalDate.of(2025, 7, 5), LocalTime.of(11, 10));
            this.nuevoEstadoPropuesta("El Pimiento Indomable", TipoEstado.PUBLICADA, LocalDate.of(2025, 7, 27), LocalTime.of(9, 45));
            this.nuevoEstadoPropuesta("Pilsen Rock", TipoEstado.PUBLICADA, LocalDate.of(2025, 8, 2), LocalTime.of(16, 5));
            this.nuevoEstadoPropuesta("Romeo y Julieta", TipoEstado.PUBLICADA, LocalDate.of(2025, 8, 9), LocalTime.of(14, 50));
            this.nuevoEstadoPropuesta("Un día de Julio", TipoEstado.PUBLICADA, LocalDate.of(2025, 8, 14), LocalTime.of(13, 10));
            this.nuevoEstadoPropuesta("El Lazarillo de Tormes", TipoEstado.PUBLICADA, LocalDate.of(2025, 8, 20), LocalTime.of(17, 20));

            this.altaColaboracion(50000, LocalDate.of(2025, 5, 20), LocalTime.of(14,30), TipoRetorno.PORCENTAJEGANANCIA, "Cine en el Botánico", "novick");
            this.altaColaboracion(50000, LocalDate.of(2025, 5, 24), LocalTime.of(17,25), TipoRetorno.PORCENTAJEGANANCIA, "Cine en el Botánico", "robinh");
            this.altaColaboracion(50000, LocalDate.of(2025, 5, 30), LocalTime.of(18,30), TipoRetorno.PORCENTAJEGANANCIA, "Cine en el Botánico", "nicoJ");
            this.altaColaboracion(200000, LocalDate.of(2025, 6, 30), LocalTime.of(14,25), TipoRetorno.PORCENTAJEGANANCIA, "Religiosamente", "marcelot");
            this.altaColaboracion(500, LocalDate.of(2025, 7, 1), LocalTime.of(18,05), TipoRetorno.ENTRADAGRATIS, "Religiosamente", "Tiajaci");
            this.altaColaboracion(600, LocalDate.of(2025, 7, 7), LocalTime.of(17,45), TipoRetorno.ENTRADAGRATIS, "Religiosamente", "Mengano");
            this.altaColaboracion(50000, LocalDate.of(2025, 7, 10), LocalTime.of(14,35), TipoRetorno.PORCENTAJEGANANCIA, "Religiosamente", "novick");
            this.altaColaboracion(50000, LocalDate.of(2025, 7, 15), LocalTime.of(9,45), TipoRetorno.PORCENTAJEGANANCIA, "Religiosamente", "sergiop");
            this.altaColaboracion(200000, LocalDate.of(2025, 8, 1), LocalTime.of(7,40), TipoRetorno.PORCENTAJEGANANCIA, "El Pimiento Indomable", "marcelot");
            this.altaColaboracion(80000, LocalDate.of(2025, 8, 3), LocalTime.of(9,25), TipoRetorno.PORCENTAJEGANANCIA, "El Pimiento Indomable", "sergiop");
            this.altaColaboracion(50000, LocalDate.of(2025, 8, 5), LocalTime.of(16,50), TipoRetorno.ENTRADAGRATIS, "Pilsen Rock", "chino");
            this.altaColaboracion(120000, LocalDate.of(2025, 8, 10), LocalTime.of(15,50), TipoRetorno.PORCENTAJEGANANCIA, "Pilsen Rock", "novick");
            this.altaColaboracion(120000, LocalDate.of(2025, 8, 15), LocalTime.of(19,30), TipoRetorno.ENTRADAGRATIS, "Pilsen Rock", "tonyp");
            this.altaColaboracion(100000, LocalDate.of(2025, 8, 13), LocalTime.of(4,58), TipoRetorno.PORCENTAJEGANANCIA, "Romeo y Julieta", "sergiop");
            this.altaColaboracion(200000, LocalDate.of(2025, 8, 14), LocalTime.of(11,25), TipoRetorno.PORCENTAJEGANANCIA, "Romeo y Julieta", "marcelot");
            this.altaColaboracion(30000, LocalDate.of(2025, 8, 15), LocalTime.of(04,48), TipoRetorno.ENTRADAGRATIS, "Un día de Julio", "tonyp");
            this.altaColaboracion(150000, LocalDate.of(2025, 8, 17), LocalTime.of(15,30), TipoRetorno.PORCENTAJEGANANCIA, "Un día de Julio", "marcelot");



            // Propuestas Canceladas
            this.nuevoEstadoPropuesta("Cine en el Botánico", TipoEstado.CANCELADA, LocalDate.of(2025, 6, 15), LocalTime.of(14, 50));


            System.out.println("Datos de prueba cargados exitosamente.");
        } catch (Exception e) {
            throw new CargaFallida("Error al cargar datos de prueba: " + e.toString());
        }
    }
}