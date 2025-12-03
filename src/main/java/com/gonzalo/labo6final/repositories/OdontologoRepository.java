package com.gonzalo.labo6final.repositories;

import com.gonzalo.labo6final.models.Odontologo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OdontologoRepository extends JpaRepository<Odontologo, Integer> {

    Optional<Odontologo> findByUsuarioIdUsuario(Integer idUsuario);

    @Query("SELECT o FROM Odontologo o JOIN FETCH o.especialidades WHERE o.idOdontologo = :idOdontologo")
    Optional<Odontologo> findByIdWithEspecialidades(Integer idOdontologo);

    @Query("SELECT DISTINCT o FROM Odontologo o JOIN o.especialidades oe WHERE oe.idEspecialidad = :idEspecialidad")
    List<Odontologo> findByEspecialidadId(Integer idEspecialidad);
}
