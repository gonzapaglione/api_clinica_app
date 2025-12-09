package com.gonzalo.labo6final.controllers;

import com.gonzalo.labo6final.DTO.ApiResponse;
import com.gonzalo.labo6final.DTO.HorarioLaboralRequest;
import com.gonzalo.labo6final.DTO.HorarioLaboralResponse;
import com.gonzalo.labo6final.services.HorarioLaboralService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/horarios-laborales")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HorarioLaboralController {

    private final HorarioLaboralService horarioLaboralService;

    /**
     * Crear nuevo horario laboral para un odontólogo
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HorarioLaboralResponse>> crearHorario(
            @RequestBody HorarioLaboralRequest request) {
        try {
            HorarioLaboralResponse response = horarioLaboralService.crearHorario(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Horario creado exitosamente", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Actualizar horario laboral existente
     */
    @PutMapping("/{idHorario}")
    public ResponseEntity<ApiResponse<HorarioLaboralResponse>> actualizarHorario(
            @PathVariable Integer idHorario,
            @RequestBody HorarioLaboralRequest request) {
        try {
            HorarioLaboralResponse response = horarioLaboralService.actualizarHorario(idHorario, request);
            return ResponseEntity.ok(ApiResponse.success("Horario actualizado exitosamente", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Eliminar (desactivar) horario laboral
     */
    @DeleteMapping("/{idHorario}")
    public ResponseEntity<ApiResponse<Void>> eliminarHorario(@PathVariable Integer idHorario) {
        try {
            horarioLaboralService.eliminarHorario(idHorario);
            return ResponseEntity.ok(ApiResponse.success("Horario eliminado exitosamente", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Obtener todos los horarios de un odontólogo
     */
    @GetMapping("/odontologo/{idOdontologo}")
    public ResponseEntity<ApiResponse<List<HorarioLaboralResponse>>> obtenerHorariosPorOdontologo(
            @PathVariable Integer idOdontologo) {
        try {
            List<HorarioLaboralResponse> horarios = horarioLaboralService.obtenerHorariosPorOdontologo(idOdontologo);
            return ResponseEntity.ok(ApiResponse.success(horarios));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Obtener detalle de un horario específico
     */
    @GetMapping("/{idHorario}")
    public ResponseEntity<ApiResponse<HorarioLaboralResponse>> obtenerHorario(
            @PathVariable Integer idHorario) {
        try {
            HorarioLaboralResponse response = horarioLaboralService.obtenerPorId(idHorario);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
