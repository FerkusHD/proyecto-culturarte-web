package com.culturarte.logica;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
                "img.png", "prop1", "Arte", LocalDate.now(), LocalTime.now());
        verify(mp).agregarPropuesta(any(Propuesta.class));

        when(mp.getPropuesta("Expo")).thenReturn(mock(Propuesta.class));
        assertThrows(PropuestaYaExiste.class, () -> controlador.altaPropuesta(
                "Expo", "desc", "Montevideo",
                LocalDate.of(2025, 1, 1), 100f, 1000f,
                java.util.EnumSet.of(TipoRetorno.ENTRADAGRATIS),
                "img.png", "prop1", "Arte", LocalDate.now(), LocalTime.now()));
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

}