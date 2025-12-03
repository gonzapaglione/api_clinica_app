package com.gonzalo.labo6final.repositories;

import com.gonzalo.labo6final.models.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Integer> {

    // Buscar turnos por paciente
    List<Turno> findByPacienteIdPaciente(Integer idPaciente);

    // Buscar turnos por odontólogo
    List<Turno> findByOdontologoIdOdontologo(Integer idOdontologo);

    // Buscar turnos por estado
    List<Turno> findByEstadoTurnoIdEstado(Integer idEstado);

    // Buscar turnos por fecha
    List<Turno> findByFecha(LocalDate fecha);

    // Buscar turnos por odontólogo y fecha
    List<Turno> findByOdontologoIdOdontologoAndFecha(Integer idOdontologo, LocalDate fecha);

    // Verificar disponibilidad de turno
    @Query("SELECT COUNT(t) > 0 FROM Turno t WHERE t.odontologo.idOdontologo = :idOdontologo " +
            "AND t.fecha = :fecha AND t.hora = :hora AND t.estadoTurno.nombre != 'CANCELADO'")
    boolean existsTurnoByOdontologoAndFechaAndHora(Integer idOdontologo, LocalDate fecha, LocalTime hora);

    // Obtener turnos próximos de un paciente
    @Query("SELECT t FROM Turno t WHERE t.paciente.idPaciente = :idPaciente " +
            "AND t.fecha >= :fecha AND t.estadoTurno.nombre = 'PROGRAMADO' ORDER BY t.fecha, t.hora")
    List<Turno> findProximosTurnosByPaciente(Integer idPaciente, LocalDate fecha);

    // Obtener turnos del día para un odontólogo
    @Query("SELECT t FROM Turno t WHERE t.odontologo.idOdontologo = :idOdontologo " +
            "AND t.fecha = :fecha ORDER BY t.hora")
    List<Turno> findTurnosDelDiaByOdontologo(Integer idOdontologo, LocalDate fecha);

    // Obtener historial de turnos de un paciente
    @Query("SELECT t FROM Turno t WHERE t.paciente.idPaciente = :idPaciente " +
            "ORDER BY t.fecha DESC, t.hora DESC")
    List<Turno> findHistorialByPaciente(Integer idPaciente);
}
