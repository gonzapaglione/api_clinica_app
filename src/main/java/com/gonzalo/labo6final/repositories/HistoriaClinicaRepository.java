package com.gonzalo.labo6final.repositories;

import com.gonzalo.labo6final.models.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, Integer> {

    // Buscar historia clínica por turno
    Optional<HistoriaClinica> findByTurnoIdTurno(Integer idTurno);

    // Verificar si existe historia clínica para un turno
    boolean existsByTurnoIdTurno(Integer idTurno);

    // Obtener historial completo de un paciente
    @Query("SELECT hc FROM HistoriaClinica hc " +
            "JOIN hc.turno t WHERE t.paciente.idPaciente = :idPaciente " +
            "ORDER BY t.fecha DESC, t.hora DESC")
    List<HistoriaClinica> findHistorialByPaciente(Integer idPaciente);

    // Obtener historias clínicas atendidas por un odontólogo
    @Query("SELECT hc FROM HistoriaClinica hc " +
            "JOIN hc.turno t WHERE t.odontologo.idOdontologo = :idOdontologo " +
            "ORDER BY t.fecha DESC, t.hora DESC")
    List<HistoriaClinica> findHistorialByOdontologo(Integer idOdontologo);
}
