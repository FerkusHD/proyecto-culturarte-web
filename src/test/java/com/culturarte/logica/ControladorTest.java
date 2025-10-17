package com.culturarte.logica;

import com.culturarte.exepciones.PropuestaYaExiste;
import com.culturarte.logica.clases.Categoria;
import com.culturarte.logica.clases.Proponente;
import com.culturarte.logica.clases.Propuesta;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.culturarte.exepciones.UsuarioYaExiste;
import com.culturarte.logica.clases.Colaborador;
import com.culturarte.logica.datatypes.DTColaboracion;
import com.culturarte.logica.datatypes.DTColaborador;
import com.culturarte.logica.datatypes.DTProponente;
import com.culturarte.logica.datatypes.DTPropuesta;
import com.culturarte.logica.datatypes.DTUsuario;
import com.culturarte.logica.manejadores.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;
import java.time.LocalDate;
import static org.mockito.Mockito.*;
import com.culturarte.logica.enums.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.swing.tree.DefaultTreeModel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

class ControladorTest {

    @Mock
    private ManejadorPropuesta mp;
    @Mock
    private ManejadorUsuario mu;
    @Mock
    private ManejadorCategoria mc;
    @Mock
    private ManejadorColaboracion mcol;

    @InjectMocks
    private Controlador controlador;

    @BeforeAll
    public static void setUpClass() throws Exception {
    }

    @AfterAll
    public static void tearDownClass() throws Exception {
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    void altaColaborador() throws UsuarioYaExiste {

        when(mu.buscarUsuario("nick1")).thenReturn(null);

        controlador.altaColaborador("nick1", "pass", "nombre", "apellido", "mail@mail.com",
                LocalDate.of(2000,1,1), "img.png");

        verify(mu).agregarUsuario(argThat(u ->
                u instanceof Colaborador && u.getNickname().equals("nick1")));

        when(mu.buscarUsuario("nick1")).thenReturn(mock(Colaborador.class));

        assertThrows(UsuarioYaExiste.class, () ->
                controlador.altaColaborador("nick1", "pass", "nombre", "apellido", "mail@mail.com",
                        LocalDate.of(2000,1,1), "img.png"));
    }

    @Test
    void altaProponente() {
    }

    @Test
    void getNickColaboradores() {
    }

    @Test
    void getNomProponentes() {
    }

    @Test
    void getNomColaboradores() {
    }

    @Test
    void getDTColaborador() {
    }

    @Test
    void getDTProponente() {
    }

    @Test
    void altaCategoria() {
    }

    @Test
    void listarCategoriasWeb() {
    }

    @Test
    void listarCategoriasWebCompletas() {
    }

    @Test
    void listarCategorias() {
    }

    @Test
    void altaPropuesta() throws Exception {
        when(mp.getPropuesta("Expo")).thenReturn(null);
        when(mu.buscarUsuario("prop1")).thenReturn(mock(Proponente.class));
        when(mc.buscar("Arte")).thenReturn(mock(Categoria.class));

        controlador.altaPropuesta("Expo", "desc", "Montevideo",
                LocalDate.of(2025, 1, 1), 100f, 1000f,
                java.util.EnumSet.of(TipoRetorno.ENTRADAGRATIS),
                "img.png", "prop1", "Arte");
        verify(mp).agregarPropuesta(any(Propuesta.class));

        when(mp.getPropuesta("Expo")).thenReturn(mock(Propuesta.class));
        assertThrows(PropuestaYaExiste.class, () -> controlador.altaPropuesta(
                "Expo", "desc", "Montevideo",
                LocalDate.of(2025, 1, 1), 100f, 1000f,
                java.util.EnumSet.of(TipoRetorno.ENTRADAGRATIS),
                "img.png", "prop1", "Arte"));
    }

    @Test
    void getTituloPropuestas() {
    }

    @Test
    void getDTPropuestas() {
    }

    @Test
    void getDTPropuestasWeb() {
    }

    @Test
    void getDTPropuesta() {
    }

    @Test
    void getTituloPropuestasPorEstado() {
    }

    @Test
    void altaColaboracion() {
    }

    @Test
    void getNickUsuarios() {
    }

    @Test
    void seguirUsuario() {
    }

    @Test
    void dejarDeSeguirUsuario() {
    }

    @Test
    void getDTUsuario() {
    }

    @Test
    void verificarPassword() {
    }

    @Test
    void cancelarColaboracionPropuesta() {
    }

    @Test
    void getDTColaboracionesPropuestas() {
    }

    @Test
    void getDTColaboracionPropuesta() {
    }

    @Test
    void getDTColaboraciones() {
    }

    @Test
    void nuevoEstadoPropuesta() {
    }

    @Test
    void getNickProponente() {
    }

    @Test
    void buscarPropuestas() {
    }

    @Test
    void buscarUsuarios() {
    }

    @Test
    void modificarPropuesta() {
    }

    @Test
    void getTiposRetorno() {
    }

    @Test
    void extenderFinanciacion() {
    }

    /**
     * Test of altaColaborador method, of class Controlador.
     */
    @Test
    public void testAltaColaborador() throws Exception {
        System.out.println("altaColaborador");
        String nickname = "";
        String password = "";
        String nombre = "";
        String apellido = "";
        String email = "";
        LocalDate fechaNacimiento = null;
        String imagen = "";
        Controlador instance = null;
        instance.altaColaborador(nickname, password, nombre, apellido, email, fechaNacimiento, imagen);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of altaProponente method, of class Controlador.
     */
    @Test
    public void testAltaProponente() throws Exception {
        System.out.println("altaProponente");
        String nickname = "";
        String password = "";
        String nombre = "";
        String apellido = "";
        String email = "";
        LocalDate fechaNacimiento = null;
        String imagen = "";
        String direccion = "";
        String linkWeb = "";
        String bibliografia = "";
        Controlador instance = null;
        instance.altaProponente(nickname, password, nombre, apellido, email, fechaNacimiento, imagen, direccion, linkWeb, bibliografia);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNickColaboradores method, of class Controlador.
     */
    @Test
    public void testGetNickColaboradores() {
        System.out.println("getNickColaboradores");
        Controlador instance = null;
        ArrayList<String> expResult = null;
        ArrayList<String> result = instance.getNickColaboradores();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNomProponentes method, of class Controlador.
     */
    @Test
    public void testGetNomProponentes() {
        System.out.println("getNomProponentes");
        Controlador instance = null;
        ArrayList<String> expResult = null;
        ArrayList<String> result = instance.getNomProponentes();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNomColaboradores method, of class Controlador.
     */
    @Test
    public void testGetNomColaboradores() {
        System.out.println("getNomColaboradores");
        Controlador instance = null;
        ArrayList<String> expResult = null;
        ArrayList<String> result = instance.getNomColaboradores();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDTColaborador method, of class Controlador.
     */
    @Test
    public void testGetDTColaborador() {
        System.out.println("getDTColaborador");
        String nickname = "";
        Controlador instance = null;
        DTColaborador expResult = null;
        DTColaborador result = instance.getDTColaborador(nickname);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDTProponente method, of class Controlador.
     */
    @Test
    public void testGetDTProponente() {
        System.out.println("getDTProponente");
        String nickname = "";
        Controlador instance = null;
        DTProponente expResult = null;
        DTProponente result = instance.getDTProponente(nickname);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of altaCategoria method, of class Controlador.
     */
    @Test
    public void testAltaCategoria() throws Exception {
        System.out.println("altaCategoria");
        String nombre = "";
        String catPadre = "";
        Controlador instance = null;
        instance.altaCategoria(nombre, catPadre);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listarCategoriasWeb method, of class Controlador.
     */
    @Test
    public void testListarCategoriasWeb() {
        System.out.println("listarCategoriasWeb");
        Controlador instance = null;
        List<String> expResult = null;
        List<String> result = instance.listarCategoriasWeb();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listarCategoriasWebCompletas method, of class Controlador.
     */
    @Test
    public void testListarCategoriasWebCompletas() {
        System.out.println("listarCategoriasWebCompletas");
        Controlador instance = null;
        List<String> expResult = null;
        List<String> result = instance.listarCategoriasWebCompletas();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listarCategorias method, of class Controlador.
     */
    @Test
    public void testListarCategorias() {
        System.out.println("listarCategorias");
        Controlador instance = null;
        DefaultTreeModel expResult = null;
        DefaultTreeModel result = instance.listarCategorias();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of altaPropuesta method, of class Controlador.
     */
    @Test
    public void testAltaPropuesta() throws Exception {
        System.out.println("altaPropuesta");
        String titulo = "";
        String descripcion = "";
        String lugar = "";
        LocalDate fechaPrevista = null;
        Float precioEntrada = null;
        Float montoNecesario = null;
        EnumSet<TipoRetorno> tipoRetornos = null;
        String imagen = "";
        String proponente = "";
        String categoria = "";
        Controlador instance = null;
        instance.altaPropuesta(titulo, descripcion, lugar, fechaPrevista, precioEntrada, montoNecesario, tipoRetornos, imagen, proponente, categoria);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTituloPropuestas method, of class Controlador.
     */
    @Test
    public void testGetTituloPropuestas() {
        System.out.println("getTituloPropuestas");
        Controlador instance = null;
        ArrayList<String> expResult = null;
        ArrayList<String> result = instance.getTituloPropuestas();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDTPropuestas method, of class Controlador.
     */
    @Test
    public void testGetDTPropuestas() {
        System.out.println("getDTPropuestas");
        Controlador instance = null;
        ArrayList<DTPropuesta> expResult = null;
        ArrayList<DTPropuesta> result = instance.getDTPropuestas();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDTPropuestasWeb method, of class Controlador.
     */
    @Test
    public void testGetDTPropuestasWeb() {
        System.out.println("getDTPropuestasWeb");
        Controlador instance = null;
        ArrayList<DTPropuesta> expResult = null;
        ArrayList<DTPropuesta> result = instance.getDTPropuestasWeb();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDTPropuesta method, of class Controlador.
     */
    @Test
    public void testGetDTPropuesta() {
        System.out.println("getDTPropuesta");
        String titulo = "";
        Controlador instance = null;
        DTPropuesta expResult = null;
        DTPropuesta result = instance.getDTPropuesta(titulo);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTituloPropuestasPorEstado method, of class Controlador.
     */
    @Test
    public void testGetTituloPropuestasPorEstado() {
        System.out.println("getTituloPropuestasPorEstado");
        TipoEstado estado = null;
        Controlador instance = null;
        ArrayList<String> expResult = null;
        ArrayList<String> result = instance.getTituloPropuestasPorEstado(estado);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of altaColaboracion method, of class Controlador.
     */
    @Test
    public void testAltaColaboracion() {
        System.out.println("altaColaboracion");
        float monto = 0.0F;
        LocalDate fecha = null;
        LocalTime hora = null;
        TipoRetorno tipoRetorno = null;
        String tituloPropuesta = "";
        String nickColaborador = "";
        Controlador instance = null;
        instance.altaColaboracion(monto, fecha, hora, tipoRetorno, tituloPropuesta, nickColaborador);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNickUsuarios method, of class Controlador.
     */
    @Test
    public void testGetNickUsuarios() {
        System.out.println("getNickUsuarios");
        Controlador instance = null;
        ArrayList<String> expResult = null;
        ArrayList<String> result = instance.getNickUsuarios();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of seguirUsuario method, of class Controlador.
     */
    @Test
    public void testSeguirUsuario() throws Exception {
        System.out.println("seguirUsuario");
        String nickSeguidor = "";
        String nickSeguido = "";
        Controlador instance = null;
        instance.seguirUsuario(nickSeguidor, nickSeguido);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of dejarDeSeguirUsuario method, of class Controlador.
     */
    @Test
    public void testDejarDeSeguirUsuario() throws Exception {
        System.out.println("dejarDeSeguirUsuario");
        String nickSeguidor = "";
        String nickSeguido = "";
        Controlador instance = null;
        instance.dejarDeSeguirUsuario(nickSeguidor, nickSeguido);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDTUsuario method, of class Controlador.
     */
    @Test
    public void testGetDTUsuario() {
        System.out.println("getDTUsuario");
        String nickname = "";
        Controlador instance = null;
        DTUsuario expResult = null;
        DTUsuario result = instance.getDTUsuario(nickname);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of verificarPassword method, of class Controlador.
     */
    @Test
    public void testVerificarPassword() {
        System.out.println("verificarPassword");
        String password = "";
        String nick = "";
        Controlador instance = null;
        boolean expResult = false;
        boolean result = instance.verificarPassword(password, nick);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cancelarColaboracionPropuesta method, of class Controlador.
     */
    @Test
    public void testCancelarColaboracionPropuesta() {
        System.out.println("cancelarColaboracionPropuesta");
        String tituloPropuesta = "";
        String nickColaborador = "";
        Controlador instance = null;
        instance.cancelarColaboracionPropuesta(tituloPropuesta, nickColaborador);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDTColaboracionesPropuestas method, of class Controlador.
     */
    @Test
    public void testGetDTColaboracionesPropuestas() {
        System.out.println("getDTColaboracionesPropuestas");
        String nickColab = "";
        Controlador instance = null;
        ArrayList<DTColaboracion> expResult = null;
        ArrayList<DTColaboracion> result = instance.getDTColaboracionesPropuestas(nickColab);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDTColaboracionPropuesta method, of class Controlador.
     */
    @Test
    public void testGetDTColaboracionPropuesta() {
        System.out.println("getDTColaboracionPropuesta");
        String nickColab = "";
        String tituloProp = "";
        Controlador instance = null;
        DTColaboracion expResult = null;
        DTColaboracion result = instance.getDTColaboracionPropuesta(nickColab, tituloProp);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDTColaboraciones method, of class Controlador.
     */
    @Test
    public void testGetDTColaboraciones() {
        System.out.println("getDTColaboraciones");
        Controlador instance = null;
        ArrayList<DTColaboracion> expResult = null;
        ArrayList<DTColaboracion> result = instance.getDTColaboraciones();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of nuevoEstadoPropuesta method, of class Controlador.
     */
    @Test
    public void testNuevoEstadoPropuesta() {
        System.out.println("nuevoEstadoPropuesta");
        String propuesta = "";
        TipoEstado estado = null;
        LocalDate fecha = null;
        LocalTime hora = null;
        Controlador instance = null;
        instance.nuevoEstadoPropuesta(propuesta, estado, fecha, hora);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNickProponente method, of class Controlador.
     */
    @Test
    public void testGetNickProponente() {
        System.out.println("getNickProponente");
        String tituloPropuesta = "";
        Controlador instance = null;
        String expResult = "";
        String result = instance.getNickProponente(tituloPropuesta);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of buscarPropuestas method, of class Controlador.
     */
    @Test
    public void testBuscarPropuestas() {
        System.out.println("buscarPropuestas");
        String texto = "";
        Controlador instance = null;
        List<DTPropuesta> expResult = null;
        List<DTPropuesta> result = instance.buscarPropuestas(texto);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of buscarUsuarios method, of class Controlador.
     */
    @Test
    public void testBuscarUsuarios() {
        System.out.println("buscarUsuarios");
        String nombre = "";
        Controlador instance = null;
        List<DTUsuario> expResult = null;
        List<DTUsuario> result = instance.buscarUsuarios(nombre);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of agregarComentario method, of class Controlador.
     */
    @Test
    public void testAgregarComentario() {
        System.out.println("agregarComentario");
        String texto = "";
        String nickColaborador = "";
        String tituloPropuesta = "";
        Controlador instance = null;
        instance.agregarComentario(texto, nickColaborador, tituloPropuesta);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of modificarPropuesta method, of class Controlador.
     */
    @Test
    public void testModificarPropuesta() throws Exception {
        System.out.println("modificarPropuesta");
        String titulo = "";
        String descripcion = "";
        String lugar = "";
        LocalDate fechaPrevista = null;
        Float precioEntrada = null;
        Float montoNecesario = null;
        String imagen = "";
        String proponente = "";
        String categoria = "";
        String nuevoEstado = "";
        Controlador instance = null;
        instance.modificarPropuesta(titulo, descripcion, lugar, fechaPrevista, precioEntrada, montoNecesario, imagen, proponente, categoria, nuevoEstado);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTiposRetorno method, of class Controlador.
     */
    @Test
    public void testGetTiposRetorno() {
        System.out.println("getTiposRetorno");
        Controlador instance = null;
        String[] expResult = null;
        String[] result = instance.getTiposRetorno();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of extenderFinanciacion method, of class Controlador.
     */
    @Test
    public void testExtenderFinanciacion() {
        System.out.println("extenderFinanciacion");
        String tituloPropuesta = "";
        LocalDate nuevaFecha = null;
        Controlador instance = null;
        instance.extenderFinanciacion(tituloPropuesta, nuevaFecha);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cargarDatosPrueba method, of class Controlador.
     */
    @Test
    public void testCargarDatosPrueba() throws Exception {
        System.out.println("cargarDatosPrueba");
        Controlador instance = null;
        instance.cargarDatosPrueba();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}