package com.gonzalo.labo6final.controllers;

import com.gonzalo.labo6final.DTO.*;
import com.gonzalo.labo6final.services.TurnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/turnos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TurnoController {

    private final TurnoService turnoService;

    /**
     * Crear un nuevo turno (RF 04)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TurnoResponse>> crearTurno(@RequestBody TurnoRequest request) {
        try {
            TurnoResponse response = turnoService.crearTurno(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Turno agendado exitosamente", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Obtener próximos turnos del paciente (RF 05)
     */
    @GetMapping("/paciente/{idPaciente}/proximos")
    public ResponseEntity<ApiResponse<List<TurnoResponse>>> obtenerProximosTurnos(
            @PathVariable Integer idPaciente) {
        try {
            List<TurnoResponse> turnos = turnoService.obtenerProximosTurnosPaciente(idPaciente);
            return ResponseEntity.ok(ApiResponse.success(turnos));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Obtener historial de turnos del paciente (RF 07)
     */
    @GetMapping("/paciente/{idPaciente}/historial")
    public ResponseEntity<ApiResponse<List<TurnoResponse>>> obtenerHistorial(
            @PathVariable Integer idPaciente) {
        try {
            List<TurnoResponse> turnos = turnoService.obtenerHistorialPaciente(idPaciente);
            return ResponseEntity.ok(ApiResponse.success(turnos));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Cancelar turno (RF 06)
     */
    @PostMapping("/cancelar")
    public ResponseEntity<ApiResponse<TurnoResponse>> cancelarTurno(
            @RequestBody CancelarTurnoRequest request) {
        try {
            TurnoResponse response = turnoService.cancelarTurno(request);
            return ResponseEntity.ok(ApiResponse.success("Turno cancelado exitosamente", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Obtener horarios disponibles para un odontólogo en una fecha (RF 04 - Paso 2)
     */
    @GetMapping("/horarios-disponibles")
    public ResponseEntity<ApiResponse<List<DisponibilidadResponse>>> obtenerHorariosDisponibles(
            @RequestParam Integer idOdontologo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        try {
            List<DisponibilidadResponse> horarios = turnoService.obtenerHorariosDisponibles(idOdontologo, fecha);
            return ResponseEntity.ok(ApiResponse.success(horarios));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Obtener turno por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TurnoResponse>> obtenerTurno(@PathVariable Integer id) {
        try {
            TurnoResponse response = turnoService.obtenerPorId(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
