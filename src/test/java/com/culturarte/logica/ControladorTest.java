package com.culturarte.logica;
import com.culturarte.exepciones.CategoriaYaExiste;
import com.culturarte.exepciones.PropuestaYaExiste;
import com.culturarte.logica.clases.Categoria;
import com.culturarte.logica.clases.Proponente;
import com.culturarte.logica.clases.Propuesta;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.culturarte.exepciones.UsuarioYaExiste;
import com.culturarte.logica.clases.Colaborador;
import com.culturarte.logica.manejadores.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.Mockito.*;
import com.culturarte.logica.enums.*;
import java.time.LocalTime;
import java.util.*;

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
    void altaColaborador_exito() throws UsuarioYaExiste {
        when(mu.buscarUsuario("nick1")).thenReturn(null);

        controlador.altaColaborador("nick1", "pass", "nombre", "apellido", "mail@mail.com",
                LocalDate.of(2000,1,1), "img.png");

        verify(mu).agregarUsuario(argThat(u ->
                u instanceof Colaborador && u.getNickname().equals("nick1")));
    }

    @Test
    void altaColaborador_usuarioYaExiste() throws UsuarioYaExiste {
        when(mu.buscarUsuario("nick1")).thenReturn(mock(Colaborador.class));

        assertThrows(UsuarioYaExiste.class, () ->
                controlador.altaColaborador("nick1", "pass", "nombre", "apellido", "mail@mail.com",
                        LocalDate.of(2000,1,1), "img.png"));
    }

    @Test
    void altaProponente_exito() throws UsuarioYaExiste {
        when(mu.buscarUsuario("propo1")).thenReturn(null);

        controlador.altaProponente("propo1", "pass", "Ana", "López", "ana@mail.com",
                LocalDate.of(1995, 2, 2), "img.png", "Calle Falsa 123", "www.ana.com", "Artista");

        verify(mu).agregarUsuario(argThat(u ->
                u instanceof Proponente && u.getNickname().equals("propo1")));
    }

    @Test
    void altaProponente_usuarioYaExiste() throws UsuarioYaExiste {
        when(mu.buscarUsuario("propo1")).thenReturn(mock(Colaborador.class));

        assertThrows(UsuarioYaExiste.class, () ->
                controlador.altaProponente("propo1", "pass", "Ana", "López", "ana@mail.com",
                        LocalDate.of(1995, 2, 2), "img.png", "Calle Falsa 123", "www.ana.com", "Artista"));
    }

    @Test
    void altaPropuesta_exito() throws Exception {
        Map<String, Propuesta> store = new HashMap<>();

        when(mp.getPropuesta(anyString())).thenAnswer(invocation -> store.get(invocation.getArgument(0)));

        doAnswer(invocation -> {
            Propuesta p = invocation.getArgument(0);
            store.put(p.getTitulo(), p);
            return null;
        }).when(mp).agregarPropuesta(any(Propuesta.class));

        when(mu.buscarUsuario("prop1")).thenReturn(mock(Proponente.class));
        when(mc.buscar("Arte")).thenReturn(mock(Categoria.class));

        controlador.altaPropuesta("Expo", "desc", "Montevideo",
                LocalDate.of(2025, 1, 1), 100f, 1000f,
                EnumSet.of(TipoRetorno.ENTRADAGRATIS),
                "img.png", "prop1", "Arte", LocalDate.now(), LocalTime.now());

        verify(mp).agregarPropuesta(any(Propuesta.class));

        assertNotNull(store.get("Expo"));
    }



    @Test
    void altaPropuesta_propuestaYaExiste() throws Exception {
        when(mp.getPropuesta("Expo")).thenReturn(mock(Propuesta.class));
        assertThrows(PropuestaYaExiste.class, () -> controlador.altaPropuesta(
                "Expo", "desc", "Montevideo",
                LocalDate.of(2025, 1, 1), 100f, 1000f,
                java.util.EnumSet.of(TipoRetorno.ENTRADAGRATIS),
                "img.png", "prop1", "Arte", LocalDate.now(), LocalTime.now()));
    }

    @Test
    void verificarPassword_correcto() throws UsuarioYaExiste {
        Colaborador colab = new Colaborador("nick1", "pass123", "Juan", "Perez", "mail@mail.com",
                LocalDate.of(1990, 1, 1), "img.png");
        when(mu.buscarUsuario("nick1")).thenReturn(colab);

        assertTrue(controlador.verificarPassword("pass123", "nick1"));
    }

    @Test
    void verificarPassword_incorrecto() {
        Colaborador colab = new Colaborador("nick1", "pass123", "Juan", "Perez", "mail@mail.com",
                LocalDate.of(1990, 1, 1), "img.png");
        when(mu.buscarUsuario("nick1")).thenReturn(colab);

        assertFalse(controlador.verificarPassword("otraClave", "nick1"));
    }

    @Test
    void verificarPassword_usuarioNoExiste() {
        when(mu.buscarUsuario("inexistente")).thenReturn(null);

        assertFalse(controlador.verificarPassword("pass123", "inexistente"));
    }
    
    @Test
    void getTiposRetorno_incluyeTodos() {
        String[] tipos = controlador.getTiposRetorno();

        assertNotNull(tipos);
        assertTrue(tipos.length > 0);
        assertTrue(Arrays.asList(tipos).contains("ENTRADAGRATIS"));
        assertTrue(Arrays.asList(tipos).contains("PORCENTAJEGANANCIA"));
    }

    @Test
    void nuevoEstadoPropuesta_cambiaEstado() throws Exception {
        Propuesta propuesta = mock(Propuesta.class);
        when(mp.getPropuesta("Expo")).thenReturn(propuesta);

        controlador.nuevoEstadoPropuesta("Expo", TipoEstado.PUBLICADA, LocalDate.now(), LocalTime.now());

        verify(propuesta).agregarEstado(argThat(estado ->
                estado.getEstado() == TipoEstado.PUBLICADA));
    }

    @Test
    void nuevoEstadoPropuesta_propuestaNoExiste() {
        when(mp.getPropuesta("Inexistente")).thenReturn(null);

        assertThrows(NullPointerException.class, () ->
                controlador.nuevoEstadoPropuesta("Inexistente", TipoEstado.PUBLICADA, LocalDate.now(), LocalTime.now()));
    }


    @Test
    void altaCategoria_exitoso() throws Exception {
        when(mc.buscar("Música")).thenReturn(null);

        controlador.altaCategoria("Música", null);

        verify(mc).alta(argThat(cat -> cat.getNombre().equals("Música")));
    }

    @Test
    void altaCategoria_repetida() throws Exception {
        when(mc.buscar("Música")).thenReturn(mock(Categoria.class));

        assertThrows(CategoriaYaExiste.class, () ->
                controlador.altaCategoria("Música", null));
    }

    @Test
    void listarCategoriasWeb_devuelveLista() {
        List<Categoria> categoriasMock = List.of(
                new Categoria("Música", null),
                new Categoria("Teatro", null),
                new Categoria("Arte", null)
        );
        when(mc.getCategoriasRaizConSubcategorias()).thenReturn(categoriasMock);

        List<String> result = controlador.listarCategoriasWeb();

        assertEquals(List.of("Música", "Teatro", "Arte"), result);
        verify(mc).getCategoriasRaizConSubcategorias();
    }


    @Test
    void getTituloPropuestas_devuelveLista() {
        Propuesta p1 = mock(Propuesta.class);
        when(p1.getTitulo()).thenReturn("Expo");
        Propuesta p2 = mock(Propuesta.class);
        when(p2.getTitulo()).thenReturn("Concierto");

        List<Propuesta> propuestasMock = List.of(p1, p2);
        when(mp.getPropuestas()).thenReturn(propuestasMock);

        ArrayList<String> result = controlador.getTituloPropuestas();

        assertEquals(new ArrayList<>(List.of("Concierto", "Expo")), result); // Orden alfabético
        verify(mp).getPropuestas();
    }


}