package com.gonzalo.labo6final.repositories;

import com.gonzalo.labo6final.models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Integer> {

    Optional<Paciente> findByDni(String dni);

    Optional<Paciente> findByUsuarioIdUsuario(Integer idUsuario);

    boolean existsByDni(String dni);

    @Query("SELECT p FROM Paciente p JOIN FETCH p.obrasSociales WHERE p.idPaciente = :idPaciente")
    Optional<Paciente> findByIdWithObrasSociales(Integer idPaciente);
}
