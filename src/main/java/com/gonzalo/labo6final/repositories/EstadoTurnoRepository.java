package com.gonzalo.labo6final.repositories;

import com.gonzalo.labo6final.models.EstadoTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EstadoTurnoRepository extends JpaRepository<EstadoTurno, Integer> {

    Optional<EstadoTurno> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
