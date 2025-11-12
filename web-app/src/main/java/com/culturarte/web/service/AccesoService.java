package com.culturarte.web.service;

import com.culturarte.web.entity.Acceso;
import com.culturarte.web.repository.AccesoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestionar los registros de acceso al sitio.
 * 
 * Según el requisito 7.1:
 * - Se guardan hasta 10,000 accesos
 * - Se mantienen solo los accesos de los últimos 30 días
 * - El registro se realiza automáticamente sin intervención del usuario
 */
@Service
public class AccesoService {

    private static final Logger logger = LoggerFactory.getLogger(AccesoService.class);
    
    private static final int MAX_ACCESOS = 10000;
    private static final int DIAS_RETENCION = 30;

    @Autowired
    private AccesoRepository accesoRepository;

    /**
     * Registra un nuevo acceso al sitio.
     * Este método se ejecuta de forma asíncrona para no bloquear la respuesta HTTP.
     * 
     * @param ip Dirección IP del acceso
     * @param url URL accedida
     * @param browser Navegador utilizado
     * @param sistemaOperativo Sistema operativo utilizado
     */
    @Async
    @Transactional
    public void registrarAcceso(String ip, String url, String browser, String sistemaOperativo) {
        try {
            // Limpiar accesos antiguos (más de 30 días)
            limpiarAccesosAntiguos();

            // Verificar si se ha alcanzado el límite de 10,000 accesos
            long totalAccesos = accesoRepository.count();
            if (totalAccesos >= MAX_ACCESOS) {
                // Eliminar los accesos más antiguos hasta quedar por debajo del límite
                eliminarAccesosExcedentes();
            }

            // Crear y guardar el nuevo acceso
            Acceso acceso = new Acceso(ip, url, browser, sistemaOperativo);
            accesoRepository.save(acceso);
            
            logger.debug("Acceso registrado: IP={}, URL={}, Browser={}, SO={}", 
                    ip, url, browser, sistemaOperativo);
        } catch (Exception e) {
            // Log del error pero no lanzar excepción para no afectar la respuesta HTTP
            logger.error("Error al registrar acceso: IP={}, URL={}", ip, url, e);
        }
    }

    /**
     * Elimina accesos anteriores a los últimos 30 días.
     */
    @Transactional
    public void limpiarAccesosAntiguos() {
        try {
            LocalDateTime fechaLimite = LocalDateTime.now().minusDays(DIAS_RETENCION);
            int eliminados = accesoRepository.deleteByFechaAccesoBefore(fechaLimite);
            if (eliminados > 0) {
                logger.info("Eliminados {} accesos anteriores a {} días", eliminados, DIAS_RETENCION);
            }
        } catch (Exception e) {
            logger.error("Error al limpiar accesos antiguos", e);
        }
    }

    /**
     * Elimina los accesos más antiguos cuando se excede el límite de 10,000.
     */
    @Transactional
    public void eliminarAccesosExcedentes() {
        try {
            long totalAccesos = accesoRepository.count();
            if (totalAccesos >= MAX_ACCESOS) {
                int excedentes = (int) (totalAccesos - MAX_ACCESOS + 100); // Eliminar 100 extras para dejar margen
                
                List<Acceso> accesosAntiguos = accesoRepository.findOldestAccesos(
                        PageRequest.of(0, excedentes));
                if (!accesosAntiguos.isEmpty()) {
                    accesoRepository.deleteAll(accesosAntiguos);
                    logger.info("Eliminados {} accesos excedentes (límite: {})", 
                            accesosAntiguos.size(), MAX_ACCESOS);
                }
            }
        } catch (Exception e) {
            logger.error("Error al eliminar accesos excedentes", e);
        }
    }

    /**
     * Obtiene todos los accesos registrados, ordenados por fecha descendente.
     * 
     * @return Lista de accesos
     */
    public List<Acceso> obtenerTodosLosAccesos() {
        return accesoRepository.findAllOrderByFechaAccesoDesc();
    }

    /**
     * Obtiene el número total de accesos registrados.
     * 
     * @return Número total de accesos
     */
    public long contarAccesos() {
        return accesoRepository.count();
    }
}

