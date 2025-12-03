package com.gonzalo.labo6final.repositories;

import com.gonzalo.labo6final.models.ObraSocial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ObraSocialRepository extends JpaRepository<ObraSocial, Integer> {

    Optional<ObraSocial> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
