package com.culturarte.logica;
import com.culturarte.exepciones.DatosIncorrectos;
import com.culturarte.exepciones.PropuestaYaExiste;
import com.culturarte.exepciones.UsuarioNoSeguido;
import com.culturarte.logica.clases.*;
import com.culturarte.logica.datatypes.*;
import com.culturarte.logica.enums.TipoEstado;
import com.culturarte.logica.enums.TipoRetorno;
import com.culturarte.logica.manejadores.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import javax.swing.tree.DefaultTreeModel;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControladorTest {

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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("getDTColaborador — incluye colaboraciones")
    @Test
    public void getDTColaborador_incluyeColaboraciones() {
        Colaborador c = new Colaborador("nickC","pw","N","A","e@mail", LocalDate.of(1990,1,1), "img");
    Propuesta propMock = mock(Propuesta.class);
    when(propMock.getTitulo()).thenReturn("P1");
    Estado estProp = new Estado(LocalDate.now(), LocalTime.now(), TipoEstado.PUBLICADA);
    when(propMock.getHistorialEstados()).thenReturn(new java.util.LinkedList<>(List.of(estProp)));
    when(propMock.getCategoria()).thenReturn(new Categoria("cat", null));
        Colaboracion col = new Colaboracion(50f, LocalDate.now(), LocalTime.now(), TipoRetorno.ENTRADAGRATIS, propMock, c);
        c.addColaboracion(col);

        when(mu.buscarUsuario("nickC")).thenReturn(c);
        when(mp.getPropuesta("P1")).thenReturn(propMock);

        DTColaborador dt = controlador.getDTColaborador("nickC");

        assertNotNull(dt);
        assertEquals("nickC", dt.getNickname());
        assertEquals(1, dt.getColaboraciones().size());
    }

    @DisplayName("getDTProponente — con propuestas")
    @Test
    public void getDTProponente_conPropuestas() {
        Proponente p = new Proponente("prop","pw","N","A","e@p", LocalDate.of(1980,1,1), "img", "dir", "link", "bio");
    Propuesta pr = mock(Propuesta.class);
    when(pr.getTitulo()).thenReturn("T1");
    Estado est = new Estado(LocalDate.now(), LocalTime.now(), TipoEstado.PUBLICADA);
    when(pr.getHistorialEstados()).thenReturn(new java.util.LinkedList<>(List.of(est)));
    when(pr.getCategoria()).thenReturn(new Categoria("cat", null));
        p.addPropuestas(pr);

        when(mu.getProponenteConPropuestas("prop")).thenReturn(p);

        DTProponente dt = controlador.getDTProponente("prop");

        assertNotNull(dt);
        assertEquals("prop", dt.getNickname());
        assertTrue(dt.getPropuestas().stream().anyMatch(dtp -> dtp.getTitulo().equals("T1")));
    }

    @DisplayName("getDTPropuesta — comentarios y historial")
    @Test
    public void getDTPropuesta_withCommentsAndHistory() {
        Propuesta p = mock(Propuesta.class);
        when(p.getTitulo()).thenReturn("TP");
        when(p.getDescripcion()).thenReturn("desc");
        when(p.getLugar()).thenReturn("lugar");
        when(p.getFechaPrevista()).thenReturn(LocalDate.now());
        when(p.getPrecioEntrada()).thenReturn(10f);
        when(p.getMontoNecesario()).thenReturn(100f);
        when(p.getImagen()).thenReturn("img");
        when(p.getNicknameColaboradores()).thenReturn(new ArrayList<>());
        when(p.getProponenteNick()).thenReturn("prop");
    Estado est = new Estado(LocalDate.now(), LocalTime.now(), TipoEstado.PUBLICADA);
    when(p.getHistorialEstados()).thenReturn(new java.util.LinkedList<>(List.of(est)));
    when(p.getEstadoActual()).thenReturn(est);
    when(p.getCategoria()).thenReturn(new Categoria("cat", null));
        // comentarios
        Colaborador c = new Colaborador("cc","pw","N","A","e", LocalDate.now(), "img");
        Comentario com = new Comentario("hola", c, p, LocalDate.now());
        when(p.getComentarios()).thenReturn(List.of(com));

        when(mp.getPropuesta("TP")).thenReturn(p);

        DTPropuesta dt = controlador.getDTPropuesta("TP");

        assertNotNull(dt);
        assertEquals("TP", dt.getTitulo());
    assertEquals(1, dt.getHistEstados().size());
    assertEquals(1, dt.getComentarios().size());
    }

    @DisplayName("colaboradorPuedeComentar — true y false")
    @Test
    public void colaboradorPuedeComentar_trueAndFalse() {
        Colaborador c = new Colaborador("c1","pw","N","A","e", LocalDate.now(), "img");
        Propuesta p = mock(Propuesta.class);
        Estado estado = new Estado(LocalDate.now(), LocalTime.now(), TipoEstado.FINANCIADA);
        when(p.getEstadoActual()).thenReturn(estado);
        when(p.getComentarios()).thenReturn(new ArrayList<>());
        when(mp.getPropuesta("P1")).thenReturn(p);
        when(mu.buscarUsuario("c1")).thenReturn(c);

        assertTrue(controlador.colaboradorPuedeComentar("c1", "P1"));

        Comentario com = new Comentario("t", c, p, LocalDate.now());
        when(p.getComentarios()).thenReturn(List.of(com));
        assertFalse(controlador.colaboradorPuedeComentar("c1", "P1"));
    }

    @DisplayName("getTituloPropuestasPorEstado — filtra por estado")
    @Test
    public void getTituloPropuestasPorEstado_filters() {
        Propuesta p1 = mock(Propuesta.class);
        when(p1.getTitulo()).thenReturn("A");
        Estado e1 = new Estado(LocalDate.now(), LocalTime.now(), TipoEstado.PUBLICADA);
        when(p1.getEstadoActual()).thenReturn(e1);

        Propuesta p2 = mock(Propuesta.class);
        when(p2.getTitulo()).thenReturn("B");
        Estado e2 = new Estado(LocalDate.now(), LocalTime.now(), TipoEstado.INGRESADA);
        when(p2.getEstadoActual()).thenReturn(e2);

        when(mp.getPropuestas()).thenReturn(List.of(p1, p2));

        ArrayList<String> res = controlador.getTituloPropuestasPorEstado(TipoEstado.PUBLICADA);
        assertEquals(1, res.size());
        assertEquals("A", res.get(0));
    }

    @DisplayName("altaColaboracion — lanza cuando falta propuesta")
    @Test
    public void altaColaboracion_throwsWhenMissing() {
        when(mp.getPropuesta("NoExiste")).thenReturn(null);
        Exception ex = assertThrows(IllegalArgumentException.class, () -> controlador.altaColaboracion(10f, LocalDate.now(), LocalTime.now(), TipoRetorno.ENTRADAGRATIS, "NoExiste", "nick"));
        assertTrue(ex.getMessage().contains("No existe la propuesta"));
    }

    @DisplayName("seguir y dejar de seguir — flujos de usuario")
    @Test
    public void seguirYDejarDeSeguir_usuarioFlows() throws Exception {
        Usuario seguidor = new Colaborador("s","pw","N","A","e", LocalDate.now(), "img");
        Usuario seguido = new Proponente("t","pw","N","A","e", LocalDate.now(), "img", "d","l","b");
        when(mu.buscarUsuario("s")).thenReturn(seguidor);
        when(mu.buscarUsuario("t")).thenReturn(seguido);

        controlador.seguirUsuario("s","t");

        verify(mu, times(1)).actualizarUsuario(seguidor);
        verify(mu, times(1)).actualizarUsuario(seguido);

        Usuario seg2 = new Colaborador("x","pw","N","A","e", LocalDate.now(), "img");
        when(mu.buscarUsuario("x")).thenReturn(seg2);
        when(mu.buscarUsuario("t")).thenReturn(seguido);
        assertThrows(UsuarioNoSeguido.class, () -> controlador.dejarDeSeguirUsuario("x","t"));
    }

    @DisplayName("verificarPassword — falso cuando no existe usuario")
    @Test
    public void verificarPassword_falseWhenNotFound() {
        when(mu.buscarUsuario("unk")).thenReturn(null);
        when(mu.buscarUsuarioPorEmail("unk")).thenReturn(null);
        assertFalse(controlador.verificarPassword("pw","unk"));
    }

    @DisplayName("cancelarColaboracionPropuesta — lanza cuando falta colaboración")
    @Test
    public void cancelarColaboracionPropuesta_throwsWhenMissing() {
        Propuesta p = mock(Propuesta.class);
        when(mp.getPropuesta("P" )).thenReturn(p);
        when(mu.buscarUsuario("c" )).thenReturn(new Colaborador("c","pw","N","A","e", LocalDate.now(), "img"));
        when(p.getColaboraciones()).thenReturn(new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> controlador.cancelarColaboracionPropuesta("P","c"));
    }

    @DisplayName("getDTColaboracionesPropuestas y getDTColaboracionPropuesta")
    @Test
    public void getDTColaboracionesPropuestas_and_getDTColaboracionPropuesta() {
        Colaborador c = new Colaborador("nc","pw","N","A","e", LocalDate.now(), "img");
        Propuesta p = mock(Propuesta.class);
        when(p.getTitulo()).thenReturn("TP");
        Colaboracion col = new Colaboracion(20f, LocalDate.now(), LocalTime.now(), TipoRetorno.ENTRADAGRATIS, p, c);
        c.addColaboracion(col);

        when(mu.buscarUsuario("nc")).thenReturn(c);

        List<Propuesta> propuestas = List.of(p);
        when(mp.getPropuestas()).thenReturn(propuestas);
        when(p.getColaboraciones()).thenReturn(List.of(col));

        ArrayList<?> lista = controlador.getDTColaboracionesPropuestas("nc");
        assertFalse(lista.isEmpty());

        assertNotNull(controlador.getDTColaboracionPropuesta("nc","TP"));
    }

    @DisplayName("agregarComentario — lanza cuando faltan entidades")
    @Test
    public void agregarComentario_throwsWhenMissingEntities() {
        when(mu.buscarUsuario("u")).thenReturn(null);
        when(mp.getPropuesta("p")).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> controlador.agregarComentario("t","u","p"));
    }

    @DisplayName("modificarPropuesta — validaciones y no encontrado")
    @Test
    public void modificarPropuesta_validationsAndNotFound() {
        assertThrows(DatosIncorrectos.class, () -> controlador.modificarPropuesta("", "d", "l", LocalDate.now(), 10f, 100f, "img", "prop", "cat", "PUBLICADA"));

        when(mp.getPropuesta("X")).thenReturn(null);
        assertThrows(DatosIncorrectos.class, () -> controlador.modificarPropuesta("X","d","l", LocalDate.now(),10f,100f,"img","prop","cat","PUBLICADA"));
    }

    @DisplayName("getTiposRetorno — contiene valores esperados")
    @Test
    public void tiposRetorno_containsValues() {
        String[] arr = controlador.getTiposRetorno();
        assertTrue(Arrays.asList(arr).contains("ENTRADAGRATIS"));
    }

    @DisplayName("ejecutar muchos métodos — cubrir ramas")
    @Test
    public void exerciseManyMethods_coverBranches() throws Exception {
        Colaborador col = new Colaborador("col1","pw","Nom","Ap","c@mail", LocalDate.of(1990,1,1), "img");
        Proponente prop = new Proponente("prop1","pw","Nom","Ap","p@mail", LocalDate.of(1980,1,1), "img","dir","link","bio");
        when(mu.listarUsuarios()).thenReturn(List.of(col, prop));

        Propuesta p1 = mock(Propuesta.class);
        when(p1.getTitulo()).thenReturn("P1");
        Estado ePub = new Estado(LocalDate.now(), LocalTime.now(), TipoEstado.PUBLICADA);
        when(p1.getEstadoActual()).thenReturn(ePub);
        when(p1.getHistorialEstados()).thenReturn(new java.util.LinkedList<>(List.of(ePub)));
        when(p1.getNicknameColaboradores()).thenReturn(new ArrayList<>());
        when(p1.getMontoRecaudado()).thenReturn(0f);
        when(p1.getMontoNecesario()).thenReturn(100f);
        when(p1.getCategoria()).thenReturn(new Categoria("C", null));

        Propuesta p2 = mock(Propuesta.class);
        when(p2.getTitulo()).thenReturn("P2");
        Estado eIng = new Estado(LocalDate.now(), LocalTime.now(), TipoEstado.INGRESADA);
        when(p2.getEstadoActual()).thenReturn(eIng);
        when(p2.getHistorialEstados()).thenReturn(new java.util.LinkedList<>(List.of(eIng)));
        when(p2.getNicknameColaboradores()).thenReturn(new ArrayList<>());
        when(p2.getMontoRecaudado()).thenReturn(0f);
        when(p2.getMontoNecesario()).thenReturn(50f);
        when(p2.getCategoria()).thenReturn(new Categoria("C2", null));

        when(mp.getPropuestas()).thenReturn(List.of(p1, p2));
        when(mp.buscarPropuestas(anyString())).thenReturn(List.of(p1, p2));

        when(mu.buscarUsuario("col1")).thenReturn(col);
        when(mu.buscarUsuario("prop1")).thenReturn(prop);
        when(mu.getProponenteConPropuestas("prop1")).thenReturn(prop);

        when(mp.getPropuesta("P1")).thenReturn(p1);
        when(mp.getPropuesta("P2")).thenReturn(p2);

        controlador.getNickColaboradores();
        controlador.getNomProponentes();
        controlador.getNomColaboradores();
        controlador.getNickUsuarios();

        controlador.getDTPropuestas();
        controlador.getDTPropuestasWeb();
        controlador.getDTPropuesta("P1");

        when(mu.buscarUsuario("col1")).thenReturn(col);
        when(mp.getPropuesta("P1")).thenReturn(p1);
        controlador.altaColaboracion(20f, LocalDate.now(), LocalTime.now(), TipoRetorno.ENTRADAGRATIS, "P1", "col1");

        controlador.nuevoEstadoPropuesta("P1", TipoEstado.ENFINANCIACION, LocalDate.now(), LocalTime.now());

        controlador.buscarPropuestas("any");
        controlador.buscarUsuarios("Nom");

        when(mu.buscarUsuario("col1")).thenReturn(col);
        when(mp.getPropuesta("P1")).thenReturn(p1);
        controlador.agregarPropuestaFavorita("col1","P1");
        controlador.sacarPropuestaFavorita("col1","P1");

        when(mp.getPropuesta("P1")).thenReturn(p1);
        controlador.extenderFinanciacion("P1", LocalDate.now().plusDays(10));

        when(mu.buscarUsuario("col1")).thenReturn(col);
        when(mp.getPropuesta("P1")).thenReturn(p1);
        controlador.agregarComentario("hola","col1","P1");

        Colaboracion colab = new Colaboracion(10f, LocalDate.now(), LocalTime.now(), TipoRetorno.ENTRADAGRATIS, p1, col);
        when(p1.getColaboraciones()).thenReturn(new ArrayList<>(List.of(colab)));
        col.addColaboracion(colab);
        controlador.cancelarColaboracionPropuesta("P1","col1");

        List<?> found = controlador.buscarPropuestas("x");
        assertNotNull(found);

        verify(mp, atLeastOnce()).getPropuesta("P1");
        verify(mu, atLeastOnce()).buscarUsuario("col1");
    }

    @DisplayName("listarCategorias — lista completa y árbol")
    @Test
    public void listarCategorias_completasYArbol() {
        Categoria raiz = new Categoria("R", null);
        Categoria sub = new Categoria("S", null);
        Categoria sub2 = new Categoria("S2", null);
        raiz.addSubCategoria(sub);
        sub.addSubCategoria(sub2);

        when(mc.getCategoriasRaizConSubcategorias()).thenReturn(List.of(raiz));

        List<String> completa = controlador.listarCategoriasWebCompletas();
        assertTrue(completa.contains("R"));
        assertTrue(completa.contains("S"));
        assertTrue(completa.contains("S2"));

        DefaultTreeModel model = controlador.listarCategorias();
        assertNotNull(model);
    }

    @DisplayName("altaCategoria — excepción y subcategoría")
    @Test
    public void altaCategoria_exceptions_and_subcategory() throws Exception {
        when(mc.buscar("X")).thenReturn(new Categoria("X", null));
        assertThrows(Exception.class, () -> controlador.altaCategoria("X", null));

        when(mc.buscar("Y")).thenReturn(null);
        controlador.altaCategoria("Y", "padre");
        verify(mc).agregarSubcategoria("Y", "padre");
    }

    @DisplayName("getDTPropuesta — caso nulo")
    @Test
    public void getDTPropuesta_nullCase() {
        when(mp.getPropuesta("NO" )).thenReturn(null);
        DTPropuesta p = controlador.getDTPropuesta("NO");
        assertNotNull(p);
        assertNull(p.getTitulo());
    }

    @DisplayName("favoritos — lanza cuando entidades faltan")
    @Test
    public void favorites_exceptions() {
        when(mu.buscarUsuario("u")).thenReturn(null);
        when(mp.getPropuesta("P")).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> controlador.agregarPropuestaFavorita("u","P"));
        assertThrows(IllegalArgumentException.class, () -> controlador.sacarPropuestaFavorita("u","P"));
    }

    @DisplayName("dejarDeSeguir — caso exitoso")
    @Test
    public void dejarDeSeguir_successPath() throws Exception {
        Usuario seg = new Colaborador("a","pw","N","A","e", LocalDate.now(), "img");
        Usuario seguido = new Proponente("b","pw","N","A","e", LocalDate.now(), "img","d","l","bi");
        seg.addUsuariosSeguidos(seguido);
        seguido.addUsuarioSeguidor(seg);
        when(mu.buscarUsuario("a")).thenReturn(seg);
        when(mu.buscarUsuario("b")).thenReturn(seguido);

        controlador.dejarDeSeguirUsuario("a","b");
        verify(mu).actualizarUsuario(seg);
        verify(mu).actualizarUsuario(seguido);
    }

    @DisplayName("getDTUsuario — con listas")
    @Test
    public void getDTUsuario_withLists() {
        Colaborador c = new Colaborador("c","pw","N","A","e", LocalDate.now(), "img");
        Proponente p = new Proponente("pp","pw","N","A","e", LocalDate.now(), "img","d","l","b");
        c.addUsuariosSeguidos(p);
        p.addUsuarioSeguidor(c);
        when(mu.buscarUsuario("c")).thenReturn(c);
        DTUsuario dtu = controlador.getDTUsuario("c");
        assertNotNull(dtu);
    }

    @DisplayName("altaColaboracion — dispara estado financiada")
    @Test
    public void altaColaboracion_triggersEstadoFinanciada() {
        Colaborador c = new Colaborador("colF","pw","N","A","e", LocalDate.now(), "img");
        Propuesta p = mock(Propuesta.class);
    when(mp.getPropuesta("PF")).thenReturn(p);
    when(p.getTitulo()).thenReturn("PF");
        when(mu.buscarUsuario("colF")).thenReturn(c);
        Estado est = new Estado(LocalDate.now(), LocalTime.now(), TipoEstado.PUBLICADA);
        when(p.getEstadoActual()).thenReturn(est);
        when(p.getMontoRecaudado()).thenReturn(200f);
        when(p.getMontoNecesario()).thenReturn(100f);
        when(p.getColaboraciones()).thenReturn(new ArrayList<>());

        controlador.altaColaboracion(150f, LocalDate.now(), LocalTime.now(), TipoRetorno.ENTRADAGRATIS, "PF", "colF");
        verify(mp, atLeastOnce()).actualizarPropuesta(p);
    }

    @Test
    public void altaColaborador_exito() throws Exception {
        when(mu.buscarUsuario("nick1")).thenReturn(null);

        controlador.altaColaborador("nick1", "pass", "nombre", "apellido", "mail@mail.com",
                LocalDate.of(2000,1,1), "img.png");

        verify(mu).agregarUsuario(argThat(u ->
                u instanceof Colaborador && u.getNickname().equals("nick1")));
    }

    @Test
    public void altaProponente_exito() throws Exception {
        when(mu.buscarUsuario("propo1")).thenReturn(null);

        controlador.altaProponente("propo1", "pass", "Ana", "López", "ana@mail.com",
                LocalDate.of(1995, 2, 2), "img.png", "Calle Falsa 123", "www.ana.com", "Artista");

        verify(mu).agregarUsuario(argThat(u ->
                u instanceof Proponente && u.getNickname().equals("propo1")));
    }

    @Test
    public void altaPropuesta_exito() throws Exception {
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
    public void altaPropuesta_propuestaYaExiste() throws Exception {
        when(mp.getPropuesta("Expo")).thenReturn(mock(Propuesta.class));
        assertThrows(PropuestaYaExiste.class, () -> controlador.altaPropuesta(
                "Expo", "desc", "Montevideo",
                LocalDate.of(2025, 1, 1), 100f, 1000f,
                EnumSet.of(TipoRetorno.ENTRADAGRATIS),
                "img.png", "prop1", "Arte", LocalDate.now(), LocalTime.now()));
    }

    @Test
    public void verificarPassword_correcto() {
        Colaborador colab = new Colaborador("nick1", "pass123", "Juan", "Perez", "mail@mail.com",
                LocalDate.of(1990, 1, 1), "img.png");
        when(mu.buscarUsuario("nick1")).thenReturn(colab);

        assertTrue(controlador.verificarPassword("pass123", "nick1"));
    }

    @Test
    public void getTiposRetorno_incluyeTodos() {
        String[] tipos = controlador.getTiposRetorno();

        assertNotNull(tipos);
        assertTrue(tipos.length > 0);
        assertTrue(Arrays.asList(tipos).contains("ENTRADAGRATIS"));
        assertTrue(Arrays.asList(tipos).contains("PORCENTAJEGANANCIA"));
    }

    @Test
    public void nuevoEstadoPropuesta_cambiaEstado() throws Exception {
        Propuesta propuesta = mock(Propuesta.class);
        when(mp.getPropuesta("Expo")).thenReturn(propuesta);

        controlador.nuevoEstadoPropuesta("Expo", TipoEstado.PUBLICADA, LocalDate.now(), LocalTime.now());

        verify(propuesta).agregarEstado(argThat(estado ->
                estado.getEstado() == TipoEstado.PUBLICADA));
    }

    @Test
    public void altaCategoria_exitoso() throws Exception {
        when(mc.buscar("Música")).thenReturn(null);

        controlador.altaCategoria("Música", null);

        verify(mc).alta(argThat(cat -> cat.getNombre().equals("Música")));
    }

    @Test
    public void listarCategoriasWeb_devuelveLista() {
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
    public void getTituloPropuestas_devuelveLista() {
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
