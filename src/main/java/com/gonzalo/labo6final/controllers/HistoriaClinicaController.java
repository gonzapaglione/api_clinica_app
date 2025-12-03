package com.gonzalo.labo6final.controllers;

import com.gonzalo.labo6final.DTO.*;
import com.gonzalo.labo6final.services.HistoriaClinicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/historia-clinica")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HistoriaClinicaController {

    private final HistoriaClinicaService historiaClinicaService;

    /**
     * Obtener historial clínico del paciente (RF 07)
     */
    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<ApiResponse<List<HistoriaClinicaResponse>>> obtenerHistorialPaciente(
            @PathVariable Integer idPaciente) {
        try {
            List<HistoriaClinicaResponse> historial = historiaClinicaService.obtenerHistorialPaciente(idPaciente);
            return ResponseEntity.ok(ApiResponse.success(historial));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Obtener historia clínica de un turno específico
     */
    @GetMapping("/turno/{idTurno}")
    public ResponseEntity<ApiResponse<HistoriaClinicaResponse>> obtenerPorTurno(
            @PathVariable Integer idTurno) {
        try {
            HistoriaClinicaResponse response = historiaClinicaService.obtenerPorTurno(idTurno);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
