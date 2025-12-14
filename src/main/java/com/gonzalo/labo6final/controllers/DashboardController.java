package com.gonzalo.labo6final.controllers;

import com.gonzalo.labo6final.DTO.ApiResponse;
import com.gonzalo.labo6final.DTO.PacienteTurnosStatsResponse;
import com.gonzalo.labo6final.DTO.TurnoHoyItemResponse;
import com.gonzalo.labo6final.DTO.TurnosHoyResponse;
import com.gonzalo.labo6final.models.Turno;
import com.gonzalo.labo6final.repositories.TurnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final TurnoRepository turnoRepository;

    @GetMapping("/paciente/{idPaciente}/turnos-hoy")
    public ResponseEntity<ApiResponse<TurnosHoyResponse>> turnosHoy(@PathVariable Integer idPaciente) {
        LocalDate hoy = LocalDate.now();
        List<Turno> turnos = turnoRepository.findByPacienteIdPacienteAndFechaAndEstadoTurnoNombreNot(
                idPaciente,
                hoy,
                "CANCELADO");

        List<TurnoHoyItemResponse> items = new ArrayList<>();
        if (turnos != null) {
            for (Turno t : turnos) {
                if (t == null)
                    continue;
                String odontologo = t.getOdontologo() != null
                        ? (t.getOdontologo().getApellido() + " " + t.getOdontologo().getNombre())
                        : null;
                String motivo = t.getMotivoConsulta() != null ? t.getMotivoConsulta().getDescripcion() : null;
                String estado = t.getEstadoTurno() != null ? t.getEstadoTurno().getNombre() : null;
                String hora = t.getHora() != null ? t.getHora().toString() : null;
                items.add(new TurnoHoyItemResponse(t.getIdTurno(), hora, estado, odontologo, motivo));
            }
        }

        TurnosHoyResponse payload = new TurnosHoyResponse(items.size(), items);
        return ResponseEntity.ok(ApiResponse.success(payload));
    }

    @GetMapping("/paciente/{idPaciente}/turnos/estadisticas")
    public ResponseEntity<ApiResponse<PacienteTurnosStatsResponse>> estadisticasTurnosPaciente(
            @PathVariable Integer idPaciente) {

        long total = turnoRepository.countByPacienteIdPaciente(idPaciente);
        long programados = turnoRepository.countByPacienteIdPacienteAndEstadoTurnoNombre(idPaciente, "PROGRAMADO");
        long realizados = turnoRepository.countByPacienteIdPacienteAndEstadoTurnoNombre(idPaciente, "REALIZADO");
        long ausentes = turnoRepository.countByPacienteIdPacienteAndEstadoTurnoNombre(idPaciente, "AUSENTE");
        long cancelados = turnoRepository.countByPacienteIdPacienteAndEstadoTurnoNombre(idPaciente, "CANCELADO");

        PacienteTurnosStatsResponse payload = new PacienteTurnosStatsResponse(
                total,
                programados,
                realizados,
                ausentes,
                cancelados);
        return ResponseEntity.ok(ApiResponse.success(payload));
    }
}
