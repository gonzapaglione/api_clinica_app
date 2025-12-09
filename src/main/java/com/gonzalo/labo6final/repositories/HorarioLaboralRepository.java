package com.gonzalo.labo6final.repositories;

import com.gonzalo.labo6final.models.HorarioLaboral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface HorarioLaboralRepository extends JpaRepository<HorarioLaboral, Integer> {

    List<HorarioLaboral> findByOdontologoIdOdontologoAndActivoTrue(Integer idOdontologo);

    Optional<HorarioLaboral> findByOdontologoIdOdontologoAndDiaSemanaAndActivoTrue(
            Integer idOdontologo, DayOfWeek diaSemana);

    @Query("SELECT h FROM HorarioLaboral h WHERE h.odontologo.idOdontologo = :idOdontologo " +
            "AND h.diaSemana = :diaSemana AND h.activo = true")
    Optional<HorarioLaboral> findHorarioActivo(
            @Param("idOdontologo") Integer idOdontologo,
            @Param("diaSemana") DayOfWeek diaSemana);

    boolean existsByOdontologoIdOdontologoAndDiaSemanaAndActivoTrue(
            Integer idOdontologo, DayOfWeek diaSemana);
}
