package com.culturarte.web.repository;

import com.culturarte.web.entity.Acceso;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Acceso.
 */
@Repository
public interface AccesoRepository extends JpaRepository<Acceso, Long> {

    long count();

    /**
     * Elimina accesos anteriores a la fecha especificada.
     * 
     * @param fechaLimite Fecha límite. Se eliminan accesos anteriores a esta fecha.
     * @return Número de registros eliminados
     */
    @Modifying
    @Query("DELETE FROM Acceso a WHERE a.fechaAcceso < :fechaLimite")
    int deleteByFechaAccesoBefore(@Param("fechaLimite") LocalDateTime fechaLimite);

    /**
     * Obtiene los accesos más antiguos, ordenados por fecha de acceso.
     * 
     * @param pageable Para limitar el número de resultados
     * @return Lista de accesos ordenados por fecha (más antiguos primero)
     */
    @Query("SELECT a FROM Acceso a ORDER BY a.fechaAcceso ASC")
    List<Acceso> findOldestAccesos(Pageable pageable);

    /**
     * Obtiene todos los accesos ordenados por fecha de acceso descendente.
     * 
     * @return Lista de accesos ordenados por fecha (más recientes primero)
     */
    @Query("SELECT a FROM Acceso a ORDER BY a.fechaAcceso DESC")
    List<Acceso> findAllOrderByFechaAccesoDesc();
}

