package com.gonzalo.labo6final.repositories;

import com.gonzalo.labo6final.models.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ValoracionRepository extends JpaRepository<Valoracion, Integer> {

    boolean existsByTurnoIdTurno(Integer idTurno);

    Optional<Valoracion> findFirstByTurnoIdTurno(Integer idTurno);

    List<Valoracion> findByOdontologoIdOdontologo(Integer idOdontologo);

    @Query("select avg(v.estrellas) from Valoracion v where v.odontologo.idOdontologo = :idOdontologo")
    Double obtenerPromedioEstrellasPorOdontologo(@Param("idOdontologo") Integer idOdontologo);

    long countByOdontologoIdOdontologo(Integer idOdontologo);
}
