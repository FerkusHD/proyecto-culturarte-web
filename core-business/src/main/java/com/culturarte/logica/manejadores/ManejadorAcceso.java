package com.culturarte.logica.manejadores;

import com.culturarte.logica.datatypes.DTAcceso;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ManejadorAcceso {

    @PersistenceContext
    private EntityManager em;

    public List<DTAcceso> obtenerAccesosOrdenados() {
        List<Object[]> filas = em.createNativeQuery(
                "SELECT id, ip, url, browser, sistema_operativo, fecha_acceso " +
                        "FROM accesos ORDER BY fecha_acceso DESC"
        ).getResultList();

        List<DTAcceso> lista = new ArrayList<>();
        for (Object[] f : filas) {
            lista.add(new DTAcceso(
                    f[0].toString(),          // id
                    (String) f[1],            // ip
                    (String) f[2],            // url
                    (String) f[3],            // browser
                    (String) f[4],            // sistema operativo
                    (LocalDateTime) f[5])  // fecha_acceso
            );
        }
        return lista;
    }


}
