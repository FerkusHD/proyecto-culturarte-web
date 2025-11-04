package com.culturarte.soap.endpoint;

import com.culturarte.logica.IControlador;
import com.culturarte.logica.datatypes.DTPropuesta;
import com.culturarte.logica.enums.TipoEstado;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class PropuestasEndpointTest {

    @Test
    public void listarPropuestas_mapsAndReturnsPropuestas() throws Exception {
        IControlador ctrl = mock(IControlador.class);

        DTPropuesta dt = new DTPropuesta(
                "MiTitulo",
                "Descripcion",
                TipoEstado.PUBLICADA,
                2,
                100f,
                50f,
                LocalDate.of(2025,11,1),
                "img.png",
                "Musica",
                "proponente"
        );

    java.util.ArrayList<DTPropuesta> list = new ArrayList<>();
        list.add(dt);

        when(ctrl.getDTPropuestasWeb()).thenReturn(list);

    Class<?> cls = Class.forName("com.culturarte.soap.endpoint.PropuestasEndpoint");
    java.lang.reflect.Constructor<?> ctor = cls.getConstructor(IControlador.class);
    Object endpoint = ctor.newInstance(ctrl);
    java.lang.reflect.Method listar = cls.getMethod("listarPropuestas", Object.class);
    Object resp = listar.invoke(endpoint, new Object[] { null });

    assertNotNull(resp);
    assertEquals("com.culturarte.soap.gen.ListarPropuestasResponse", resp.getClass().getName());
    java.lang.reflect.Method getPropuesta = resp.getClass().getMethod("getPropuesta");
    Object propuestaListObj = getPropuesta.invoke(resp);
    assertTrue(propuestaListObj instanceof java.util.List);
    java.util.List<?> propuestaList = (java.util.List<?>) propuestaListObj;
    assertEquals(1, propuestaList.size());
    Object first = propuestaList.get(0);
    java.lang.reflect.Method getTitulo = first.getClass().getMethod("getTitulo");
    Object titulo = getTitulo.invoke(first);
    assertEquals("MiTitulo", String.valueOf(titulo));
    }
}
