package com.gonzalo.labo6final.repositories;

import com.gonzalo.labo6final.models.MotivoConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MotivoConsultaRepository extends JpaRepository<MotivoConsulta, Integer> {

    Optional<MotivoConsulta> findByDescripcion(String descripcion);

    boolean existsByDescripcion(String descripcion);
}
