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
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;
/**
 *
 * @author maicol
 */
public class Controlador implements IControlador{
    
    private final ManejadorPropuesta mp;
    private final ManejadorUsuario mu;
    private final ManejadorCategoria mc;
    private final ManejadorColaboracion mcol;
    
    
    public Controlador() {
        this.mp = ManejadorPropuesta.getInstancia();
        this.mc = ManejadorCategoria.getInstancia();
        this.mcol = ManejadorColaboracion.getInstancia();
        this.mu = ManejadorUsuario.getInstance();
    }
    

    
    @Override
    public void altaColaborador(String nickname, String nombre, String apellido, String email, LocalDate fechaNacimiento, File imagen)
            throws UsuarioYaExiste {
        Usuario u = mu.buscarUsuario(nickname);
        if (u != null) {
            throw new UsuarioYaExiste("El usuario con nickname " + nickname + " ya está registrado");
        }
        mu.agregarUsuario(new Colaborador(nickname, nombre, apellido, email, fechaNacimiento, imagen));
    }
    
    @Override
    public void altaProponente(String nickname, String nombre, String apellido, String email, LocalDate fechaNacimiento, File imagen, String direccion, String linkWeb, String bibliografia)
            throws UsuarioYaExiste {
        Usuario u = mu.buscarUsuario(nickname);
        if (u != null) {
            throw new UsuarioYaExiste("El usuario con nickname " + nickname + " ya está registrado");
        }
        mu.agregarUsuario(new Proponente(nickname, nombre, apellido, email, fechaNacimiento, imagen, direccion, linkWeb, bibliografia));
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
                c.getNombre(),
                c.getApellido(),
                c.getEmail(),
                c.getFechaNacimiento(),
                c.getImagen()
        );

        for (Colaboracion colab : c.getColaboraciones()) {
            Propuesta prop = mp.getPropuesta(colab.getPropuesta().getTitulo());
            if (prop != null) {
                DTPropuesta dtp = new DTPropuesta(
                        prop.getTitulo(),
                        prop.getEstadoActual().getEstado(),
                        prop.getProponente().getNickname(),
                        prop.getMontoRecaudado(),
                        prop.getMontoNecesario()
                );
                dtc.addPropuesta(dtp);
            }
        }

        return dtc;
    }




    
    @Override
    public DTProponente getDTProponente(String nickname){
        // Datos usuario, datos proponente, Propuestas (nombre, estado, lista colaboradores, monto recaudado, monto necesario)
       
        Proponente p = mu.getProponenteConPropuestas(nickname);
        
        DTProponente dtp = new DTProponente(p.getNickname(), p.getNombre(), p.getApellido(), p.getEmail(), p.getFechaNacimiento(), p.getImagen(), p.getDireccion(), p.getLinkWeb(), p.getBiografia());
        
        for (Propuesta prop : p.getPropuestas()) {
            dtp.addPropuesta(new DTPropuesta(prop.getTitulo(), prop.getEstadoActual().getEstado() , prop.getNicknameColaboradores(), prop.getMontoRecaudado(), prop.getMontoNecesario()));
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
    public void altaPropuesta(String titulo, String descripcion, String lugar, LocalDate fechaPrevista, Float precioEntrada, Float montoNecesario, EnumSet<TipoRetorno> tipoRetornos, File imagen, String proponente, String categoria)
    throws PropuestaYaExiste {

        if (mp.getPropuesta(titulo) != null) {
            throw new PropuestaYaExiste("Ya existe esta Propuesta");
        }
        
        Proponente u = (Proponente) mu.buscarUsuario(proponente);
        
        Categoria c = mc.buscar(categoria);
        
        Propuesta p = new Propuesta(titulo,descripcion,lugar,fechaPrevista, precioEntrada, montoNecesario, tipoRetornos, imagen, u,c);
        
        mp.agregarPropuesta(p);
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
    public DTPropuesta getDTPropuesta(String titulo){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        Propuesta p = mp.getPropuesta(titulo);
        String nombreCategoria = "Sin categoría"; 
        DTPropuesta dtp = new DTPropuesta();
        ArrayList histEstado = new ArrayList();
        if(p != null){
            if (p.getCategoria() != null) {
                nombreCategoria = p.getCategoria().getNombreCompleto();
            }
            for (Estado est : p.getHistorialEstados()) {
                histEstado.add(new DTEstado(est.getEstado().toString(), est.getFecha().toString(), est.getHora().format(formatter)));
            }
    
            dtp = new DTPropuesta(p.getTitulo(), p.getDescripcion(), p.getLugar(), p.getFechaPrevista(), p.getPrecioEntrada(), p.getMontoNecesario(), p.getImagen(), p.getNicknameColaboradores(), p.getProponenteNick(), p.getEstadoActual().getEstado(), nombreCategoria, histEstado, p.getMontoRecaudado());
        }
        return dtp;
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

        // Crear la colaboración
        Colaboracion colab = new Colaboracion(monto, fecha, hora, tipoRetorno, p, c);

        // Asociar bidireccionalmente
        p.addColaboracion(colab);
        c.addColaboracion(colab);

        // Persistir colaboración
        mcol.agregarColaboracion(colab);
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
        mu.actualizarUsuario(seguidor);
    }
    
    @Override 
    public  void dejarDeSeguirUsuario(String nickSeguidor, String nickSeguido) throws UsuarioNoSeguido {
        Usuario seguidor = mu.buscarUsuario(nickSeguidor);
        Usuario seguido = mu.buscarUsuario(nickSeguido);
        if(seguidor.getUsuariosSeguidos().contains(seguido)){
            seguidor.getUsuariosSeguidos().remove(seguido);
            mu.actualizarUsuario(seguidor);
        }else{
            throw new UsuarioNoSeguido("El usuario con nickname: " + nickSeguidor + ", no sigue al usuario con nickname: " + nickSeguido);
        }
    }

    @Override
    public DTUsuario getDTUsuario(String nickname) {
        Usuario usu = mu.buscarUsuario(nickname);
        
        ArrayList<String> nickSeguidos = new ArrayList<>();
        for(Usuario u : usu.getUsuariosSeguidos()){
            nickSeguidos.add(u.getNickname());
        }
        
        DTUsuario dtu = new DTUsuario(usu.getNickname(), usu.getNombre(), nickSeguidos);
        
        return dtu;
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
    
    public void modificarPropuesta(String titulo, String descripcion, String lugar, LocalDate fechaPrevista, Float precioEntrada, 
            Float montoNecesario, File imagen, String proponente, String categoria, String nuevoEstado) throws DatosIncorrectos {
        
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
  
    
    public void cargarDatosPrueba() throws CargaFallida{
        System.out.println("Agregando datos de prueba: ...");
        try {
            // Proponentes
            this.altaProponente(
                    "hrubino", "Horacio", "Rubino",
                    "horacio.rubino@guambia.com.uy", LocalDate.of(1962, 2, 25),
                    new File(getClass().getClassLoader().getResource("Imagenes/HR.jpeg").toURI()),
                    "18 de Julio 1234",
                    "https://twitter.com/horaciorubino",
                    "Actor y conductor"
            );
            this.altaProponente(
                    "mbusca", "Martín", "Buscaglia",
                    "martin.bus@agadu.org.uy", LocalDate.of(1972, 6, 14),
                    new File(getClass().getClassLoader().getResource("Imagenes/MB.jpg").toURI()),
                    "Colonia 4321",
                    "http://www.martinbuscaglia.com/",
                    "Músico uruguayo"
            );
            this.altaProponente(
                    "hectorg", "Héctor", "Guido",
                    "hector.gui@elgalpon.org.uy", LocalDate.of(1954, 1, 7),
                    new File("imagenes/hg.png"),
                    "Gral. Flores 5645",
                    "",
                    "Actor de teatro"
            );
            this.altaProponente(
                    "tabarec", "Tabaré", "Cardozo",
                    "tabare.car@agadu.org.uy", LocalDate.of(1971, 7, 24),
                    new File("imagenes/tc.png"),
                    "Santiago Rivas 1212",
                    "https://www.facebook.com/Tabaré-Cardozo-55179094281/?ref=br_rs",
                    "Cantante murguista"
            );
            this.altaProponente(
                    "cachilas", "Waldemar \"Cachila\"", "Silva",
                    "cachila.sil@c1080.org.uy", LocalDate.of(1947, 1, 1),
                    new File("imagenes/cs.png"),
                    "Br. Artigas 4567",
                    "https://www.facebook.com/C1080?ref=br_rs",
                    "Director comparsa"
            );
            this.altaProponente(
                    "juliob", "Julio", "Bocca",
                    "juliobocca@sodre.com.uy", LocalDate.of(1967, 3, 16),
                    new File("imagenes/jb.png"),
                    "Benito Blanco 4321",
                    "",
                    "Bailarín"
            );
            this.altaProponente(
                    "diegop", "Diego", "Parodi",
                    "diego@efectocine.com", LocalDate.of(1975, 1, 1),
                    new File("imagenes/dp.png"),
                    "Emilio Frugoni 1138 Ap. 02",
                    "http://www.efectocine.com",
                    "Cineasta"
            );
            this.altaProponente(
                    "kairoh", "Kairo", "Herrera",
                    "kairoher@pilsenrock.com.uy", LocalDate.of(1840, 4, 25),
                    new File("imagenes/kh.png"),
                    "Paraguay 1423",
                    "",
                    "Organizador eventos"
            );
            this.altaProponente(
                    "losBardo", "Los", "Bardo",
                    "losbardo@bardocientifico.com", LocalDate.of(1980, 10, 31),
                    new File(getClass().getClassLoader().getResource("Imagenes/LB.jpg").toURI()),
                    "8 de Octubre 1429",
                    "https://bardocientifico.com/",
                    "Divulgación científica"
            );
                // Colaboradores
            this.altaColaborador(
                    "robinh", "Robin", "Henderson",
                    "robin.h@tinglesa.com.uy", LocalDate.of(1940, 8, 3),
                    new File("imagenes/rh.png")
            );
            this.altaColaborador(
                    "marcelot", "Marcelo", "Tinelli",
                    "marcelot@ideasdelsur.com.ar", LocalDate.of(1960, 4, 1),
                    new File("imagenes/mt.png")
            );
            this.altaColaborador(
                    "novick", "Edgardo", "Novick",
                    "edgardo@novick.com.uy", LocalDate.of(1952, 7, 17),
                    new File("imagenes/en.png")
            );
            this.altaColaborador(
                    "sergiop", "Sergio", "Puglia",
                    "puglia@alpanpan.com.uy", LocalDate.of(1950, 1, 28),
                    new File(getClass().getClassLoader().getResource("Imagenes/SP.jpg").toURI())
            );
            this.altaColaborador(
                    "chino", "Alvaro", "Recoba",
                    "chino@trico.org.uy", LocalDate.of(1976, 3, 17),
                    null
            );
            this.altaColaborador(
                    "tonyp", "Antonio", "Pacheco",
                    "tonyp@manya.org.uy", LocalDate.of(1955, 2, 14),
                    new File(getClass().getClassLoader().getResource("Imagenes/AP.jpg").toURI())
            );
            this.altaColaborador(
                    "nicoJ", "Nicolás", "Jodal",
                    "jodal@artech.com.uy", LocalDate.of(1960, 8, 9),
                    new File(getClass().getClassLoader().getResource("Imagenes/NJ.jpg").toURI())
            );
            this.altaColaborador(
                    "juanP", "Juan", "Perez",
                    "juanp@elpueblo.com", LocalDate.of(1970, 1, 1),
                    null
            );
            this.altaColaborador(
                    "Mengano", "Mengano", "Gómez",
                    "menganog@elpueblo.com", LocalDate.of(1982, 2, 2),
                    null
            );
            this.altaColaborador(
                    "Perengano", "Perengano", "López",
                    "pere@elpueblo.com", LocalDate.of(1985, 3, 3),
                    null
            );
            this.altaColaborador(
                    "Tiajaci", "Tía", "Jacinta",
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
            this.altaPropuesta(
                    "Cine en el Botánico",
                    "El 16 de Diciembre a la hora 20 se proyectará la película \"Clever\", en el Jardín Botánico (Av. 19 de Abril 1181) en el marco de las actividades realizadas por el ciclo Cultura al Aire Libre. El largometraje uruguayo de ficción Clever es dirigido por Federico Borgia y Guillermo Madeiro. Es apto para mayores de 15 años.",
                    "Jardín Botánico",
                    LocalDate.of(2017, 9, 16),
                    (float)200, (float)150000,
                    EnumSet.of(TipoRetorno.PORCENTAJEGANANCIA),
                    null,
                    "diegop", "Cine"
            );
            
            this.altaPropuesta(
                    "Religiosamente",
                    "MOMOSAPIENS presenta \"Religiosamente\". Mediante dos parodias y un hilo conductor que aborda la temática de la religión Momosapiens, mediante el humor y la reflexión, hilvana una historia que muestra al hombre inmerso en el tema religioso. El libreto está escrito utilizando diferentes lenguajes de humor, dando una visión satírica y reflexiva desde distintos puntos de vista, logrando mediante situaciones paródicas armar una propuesta plena de arte carnavalero.",
                    "Teatro de Verano",
                    LocalDate.of(2017, 10, 7),
                    (float)300, (float)300000,
                    EnumSet.of(TipoRetorno.ENTRADAGRATIS, TipoRetorno.PORCENTAJEGANANCIA),
                    new File("imagenes/mom.png"),
                    "hrubino", "Parodistas"
            );
            
            this.altaPropuesta(
                    "El Pimiento Indomable",
                    "El Pimiento Indomable, formación compuesta por Kiko Veneno y el uruguayo Martín Buscaglia, presentará este 19 de Octubre, su primer trabajo. Bajo un título homónimo al del grupo, es un disco que según los propios protagonistas \"no se parece al de ninguno de los dos por separado. Entre los títulos que se podrán escuchar se encuentran \"Nadador salvador\", \"América es más grande\", \"Pescaito Enroscado\" o \"La reina del placer\".",
                    "Teatro Solís",
                    LocalDate.of(2017, 10, 19),
                    (float)400, (float)400000,
                    EnumSet.of(TipoRetorno.PORCENTAJEGANANCIA),
                    new File(getClass().getClassLoader().getResource("Imagenes/PIM.jpg").toURI()),
                    "mbusca", "Concierto"
            );
            
            this.altaPropuesta(
                    "Pilsen Rock",
                    "La edición 2017 del Pilsen Rock se celebrará el 21 de Octubre en la Rural del Prado y contará con la participación de más de 15 bandas nacionales. Quienes no puedan trasladarse al lugar, tendrán la posibilidad de disfrutar los shows a través de Internet, así como entrevistas en vivo a los músicos una vez finalizados los conciertos.",
                    "Rural de Prado",
                    LocalDate.of(2017, 10, 21),
                    (float)1000, (float)900000,
                    EnumSet.of(TipoRetorno.ENTRADAGRATIS, TipoRetorno.PORCENTAJEGANANCIA),
                    new File(getClass().getClassLoader().getResource("Imagenes/PIL.jpg").toURI()),
                    "kairoh", "Festival"
            );
            
            this.altaPropuesta(
                    "Romeo y Julieta",
                    "Romeo y Julieta de Kenneth MacMillan, uno de los ballets favoritos del director artístico Julio Bocca, se presentará nuevamente el 5 de Noviembre en el Auditorio Nacional del Sodre. Basada en la obra homónima de William Shakespeare, Romeo y Julieta es considerada la coreografía maestra del MacMillan. La producción de vestuario y escenografía se realizó en los Talleres del Auditorio Adela Reta, sobre los diseños originales.",
                    "Auditorio Nacional del Sodre",
                    LocalDate.of(2017, 11, 5),
                    (float)800, (float)750000,
                    EnumSet.of(TipoRetorno.PORCENTAJEGANANCIA),
                    new File("imagenes/ryj.png"),
                    "juliob", "Ballet"
            );
            
            this.altaPropuesta(
                    "Un día de Julio",
                    "La Catalina presenta el espectáculo \"Un Día de Julio\" en Landia. Un hombre misterioso y solitario vive encerrado entre las cuatro paredes de su casa. Intenta, con sus teorías extravagantes, cambiar el mundo exterior que le resulta inhabitable. Un día de Julio sucederá algo que cambiará su vida y la de su entorno para siempre.",
                    "Landia",
                    LocalDate.of(2017, 11, 16),
                    (float)650, (float)300000,
                    EnumSet.of(TipoRetorno.ENTRADAGRATIS, TipoRetorno.PORCENTAJEGANANCIA),
                    new File(getClass().getClassLoader().getResource("Imagenes/UDJ.jpg").toURI()),
                    "tabarec", "Murga"
            );
            
            this.altaPropuesta(
                    "El Lazarillo de Tormes",
                    "Vuelve unas de las producciones de El Galpón más aclamadas de los últimos tiempos. Esta obra se ha presentado en Miami, Nueva York, Washington, México, Guadalajara, Río de Janeiro y La Habana. En nuestro país, El Lazarillo de Tormes fue nominado en los rubros mejor espectáculo y mejor dirección a los Premios Florencio 1995, obteniendo su protagonista Héctor Guido el Florencio a Mejor actor de ese año.",
                    "Teatro el Galpón",
                    LocalDate.of(2017, 12, 3),
                    (float)350, (float)175000,
                    EnumSet.of(TipoRetorno.ENTRADAGRATIS),
                    null,
                    "hectorg", "Teatro Dramático"
            );
            
            this.altaPropuesta(
                    "Bardo en la FING",
                    "El 10 de Diciembre se presentará Bardo Científico en la FING. El humor puede ser usado como una herramienta importante para el aprendizaje y la democratización de la ciencia, los monólogos científicos son una forma didáctica de apropiación del conocimiento científico y contribuyen a que el público aprenda ciencia de forma amena. Los invitamos a pasar un rato divertido, en un espacio en el cual aprenderán cosas de la ciencia que los sorprenderán. ¡Los esperamos!",
                    "Anfiteatro Edificio \"José Luis Massera\"",
                    LocalDate.of(2017, 12, 10),
                    (float)200, (float)100000,
                    EnumSet.of(TipoRetorno.ENTRADAGRATIS),
                    null,
                    "losBardo", "Stand-up"
            );
            
            this.altaColaboracion(50000, LocalDate.of(2017, 5, 20), LocalTime.of(14,30), TipoRetorno.PORCENTAJEGANANCIA, "Cine en el Botánico", "novick");
            this.altaColaboracion(50000, LocalDate.of(2017, 5, 24), LocalTime.of(17,25), TipoRetorno.PORCENTAJEGANANCIA, "Cine en el Botánico", "robinh");
            this.altaColaboracion(50000, LocalDate.of(2017, 5, 30), LocalTime.of(18,30), TipoRetorno.PORCENTAJEGANANCIA, "Cine en el Botánico", "nicoJ");
            this.altaColaboracion(200000, LocalDate.of(2017, 6, 30), LocalTime.of(14,25), TipoRetorno.PORCENTAJEGANANCIA, "Religiosamente", "marcelot");
            this.altaColaboracion(500, LocalDate.of(2017, 7, 1), LocalTime.of(18,05), TipoRetorno.ENTRADAGRATIS, "Religiosamente", "Tiajaci");
            this.altaColaboracion(600, LocalDate.of(2017, 7, 7), LocalTime.of(17,45), TipoRetorno.ENTRADAGRATIS, "Religiosamente", "Mengano");
            this.altaColaboracion(50000, LocalDate.of(2017, 7, 10), LocalTime.of(14,35), TipoRetorno.PORCENTAJEGANANCIA, "Religiosamente", "novick");
            this.altaColaboracion(50000, LocalDate.of(2017, 7, 15), LocalTime.of(9,45), TipoRetorno.PORCENTAJEGANANCIA, "Religiosamente", "sergiop");
            this.altaColaboracion(200000, LocalDate.of(2017, 8, 1), LocalTime.of(7,40), TipoRetorno.PORCENTAJEGANANCIA, "El Pimiento Indomable", "marcelot");
            this.altaColaboracion(80000, LocalDate.of(2017, 8, 3), LocalTime.of(9,25), TipoRetorno.PORCENTAJEGANANCIA, "El Pimiento Indomable", "sergiop");
            this.altaColaboracion(50000, LocalDate.of(2017, 8, 5), LocalTime.of(16,50), TipoRetorno.ENTRADAGRATIS, "Pilsen Rock", "chino");
            this.altaColaboracion(120000, LocalDate.of(2017, 8, 10), LocalTime.of(15,50), TipoRetorno.PORCENTAJEGANANCIA, "Pilsen Rock", "novick");
            this.altaColaboracion(120000, LocalDate.of(2017, 8, 15), LocalTime.of(19,30), TipoRetorno.ENTRADAGRATIS, "Pilsen Rock", "tonyp");
            this.altaColaboracion(100000, LocalDate.of(2017, 8, 13), LocalTime.of(4,58), TipoRetorno.PORCENTAJEGANANCIA, "Romeo y Julieta", "sergiop");
            this.altaColaboracion(200000, LocalDate.of(2017, 8, 14), LocalTime.of(11,25), TipoRetorno.PORCENTAJEGANANCIA, "Romeo y Julieta", "marcelot");
            this.altaColaboracion(30000, LocalDate.of(2017, 8, 15), LocalTime.of(04,48), TipoRetorno.ENTRADAGRATIS, "Un día de Julio", "tonyp");
            this.altaColaboracion(150000, LocalDate.of(2017, 8, 17), LocalTime.of(15,30), TipoRetorno.PORCENTAJEGANANCIA, "Un día de Julio", "marcelot");

            
            
            // Estados para CEB
            this.nuevoEstadoPropuesta("Cine en el Botánico", TipoEstado.INGRESADA, LocalDate.of(2017, 5, 15), LocalTime.of(15, 30));
            this.nuevoEstadoPropuesta("Cine en el Botánico", TipoEstado.PUBLICADA, LocalDate.of(2017, 5, 17), LocalTime.of(8, 30));
            this.nuevoEstadoPropuesta("Cine en el Botánico", TipoEstado.ENFINANCIACION, LocalDate.of(2017, 5, 20), LocalTime.of(14, 30));
            this.nuevoEstadoPropuesta("Cine en el Botánico", TipoEstado.FINANCIADA, LocalDate.of(2017, 5, 30), LocalTime.of(18, 30));
            this.nuevoEstadoPropuesta("Cine en el Botánico", TipoEstado.CANCELADA, LocalDate.of(2017, 6, 15), LocalTime.of(14, 50));
            
            // Estados para MOM
            this.nuevoEstadoPropuesta("Religiosamente",TipoEstado.INGRESADA, LocalDate.of(2017, 6, 18), LocalTime.of(4,28));
            this.nuevoEstadoPropuesta("Religiosamente", TipoEstado.PUBLICADA, LocalDate.of(2017, 6, 20), LocalTime.of(4, 56));
            this.nuevoEstadoPropuesta("Religiosamente", TipoEstado.ENFINANCIACION, LocalDate.of(2017, 6, 30), LocalTime.of(14, 25));
            this.nuevoEstadoPropuesta("Religiosamente", TipoEstado.FINANCIADA, LocalDate.of(2017, 7, 15), LocalTime.of(9, 45));
         
            //Estados para PIM
            this.nuevoEstadoPropuesta("El Pimiento Indomable", TipoEstado.INGRESADA, LocalDate.of(2017, 7, 26), LocalTime.of(15, 30));
            this.nuevoEstadoPropuesta("El Pimiento Indomable", TipoEstado.PUBLICADA, LocalDate.of(2017, 7, 31), LocalTime.of(8, 30));
            this.nuevoEstadoPropuesta("El Pimiento Indomable", TipoEstado.ENFINANCIACION, LocalDate.of(2017, 8, 1), LocalTime.of(7, 40));

            //Estados para PIL
            this.nuevoEstadoPropuesta("Pilsen Rock", TipoEstado.INGRESADA, LocalDate.of(2017, 7, 30), LocalTime.of(15, 40));
            this.nuevoEstadoPropuesta("Pilsen Rock", TipoEstado.PUBLICADA, LocalDate.of(2017, 8, 1), LocalTime.of(14, 30));
            this.nuevoEstadoPropuesta("Pilsen Rock", TipoEstado.ENFINANCIACION, LocalDate.of(2017, 8, 5), LocalTime.of(16, 50));
   
            //Estados para RYJ
            this.nuevoEstadoPropuesta("Romeo y Julieta", TipoEstado.INGRESADA, LocalDate.of(2017, 8, 4), LocalTime.of(12, 20));
            this.nuevoEstadoPropuesta("Romeo y Julieta", TipoEstado.PUBLICADA, LocalDate.of(2017, 8, 10), LocalTime.of(10, 25));
            this.nuevoEstadoPropuesta("Romeo y Julieta", TipoEstado.ENFINANCIACION, LocalDate.of(2017, 8, 13), LocalTime.of(4, 58));

            // Estados para UDJ
            this.nuevoEstadoPropuesta("Un día de Julio", TipoEstado.INGRESADA, LocalDate.of(2017, 8, 6), LocalTime.of(2, 0));
            this.nuevoEstadoPropuesta("Un día de Julio", TipoEstado.PUBLICADA, LocalDate.of(2017, 8, 12), LocalTime.of(4, 50));
            this.nuevoEstadoPropuesta("Un día de Julio", TipoEstado.ENFINANCIACION, LocalDate.of(2017, 8, 15), LocalTime.of(4, 48));
         
            //Estados para LDT
            this.nuevoEstadoPropuesta("El Lazarillo de Tormes", TipoEstado.INGRESADA, LocalDate.of(2017, 8, 18), LocalTime.of(2, 40));
            this.nuevoEstadoPropuesta("El Lazarillo de Tormes", TipoEstado.PUBLICADA, LocalDate.of(2017, 8, 20), LocalTime.of(21, 58));

            // Estados para BEF
            this.nuevoEstadoPropuesta("Bardo en la FING", TipoEstado.INGRESADA, LocalDate.of(2017, 8, 23), LocalTime.of(2, 12));

            
            System.out.println("Datos de prueba cargados exitosamente.");
        } catch (Exception e) {
            throw new CargaFallida("Error al cargar datos de prueba: " + e);
        }
    }
}
