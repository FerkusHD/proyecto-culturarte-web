package com.culturarte.logica.manejadores;

import com.culturarte.logica.datatypes.DTAcceso;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ManejadorAcceso {

    @PersistenceContext
    private EntityManager em;

    @SuppressWarnings("unchecked")
    public List<DTAcceso> obtenerAccesosOrdenados() {
        List<Object[]> filas = (List<Object[]>) em.createNativeQuery(
                "SELECT id, ip, url, browser, sistema_operativo, fecha_acceso " +
                        "FROM accesos ORDER BY fecha_acceso DESC"
        ).getResultList();

        List<DTAcceso> lista = new ArrayList<>();
        for (Object[] f : filas) {
            LocalDateTime fechaAcceso = convertirAFechaLocal(f[5]);
            lista.add(new DTAcceso(
                    f[0].toString(),          // id
                    (String) f[1],            // ip
                    (String) f[2],            // url
                    (String) f[3],            // browser
                    (String) f[4],            // sistema operativo
                    fechaAcceso)              // fecha_acceso
            );
        }
        return lista;
    }

    private LocalDateTime convertirAFechaLocal(Object fecha) {
        if (fecha == null) {
            return null;
        }
        if (fecha instanceof LocalDateTime) {
            return (LocalDateTime) fecha;
        }
        if (fecha instanceof Timestamp) {
            return ((Timestamp) fecha).toLocalDateTime();
        }
        if (fecha instanceof java.util.Date) {
            return new Timestamp(((java.util.Date) fecha).getTime()).toLocalDateTime();
        }
        // Si es un String, intentar parsearlo
        if (fecha instanceof String) {
            try {
                return LocalDateTime.parse((String) fecha);
            } catch (Exception e) {
                throw new IllegalArgumentException("No se pudo convertir a LocalDateTime: " + fecha, e);
            }
        }
        throw new IllegalArgumentException("Tipo no soportado para conversión a LocalDateTime: " + fecha.getClass().getName());
    }

}
