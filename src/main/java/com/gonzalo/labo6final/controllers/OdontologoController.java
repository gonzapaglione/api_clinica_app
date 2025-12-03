package com.gonzalo.labo6final.controllers;

import com.gonzalo.labo6final.DTO.*;
import com.gonzalo.labo6final.services.OdontologoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/odontologos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OdontologoController {

    private final OdontologoService odontologoService;

    /**
     * Listar todos los odontólogos (RF 04 - Paso 1)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OdontologoResponse>>> listarOdontologos() {
        try {
            List<OdontologoResponse> odontologos = odontologoService.obtenerTodos();
            return ResponseEntity.ok(ApiResponse.success(odontologos));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Filtrar odontólogos por especialidad (RF 04 - Paso 1)
     */
    @GetMapping("/especialidad/{idEspecialidad}")
    public ResponseEntity<ApiResponse<List<OdontologoResponse>>> listarPorEspecialidad(
            @PathVariable Integer idEspecialidad) {
        try {
            List<OdontologoResponse> odontologos = odontologoService.obtenerPorEspecialidad(idEspecialidad);
            return ResponseEntity.ok(ApiResponse.success(odontologos));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Obtener detalle de un odontólogo
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OdontologoResponse>> obtenerOdontologo(@PathVariable Integer id) {
        try {
            OdontologoResponse response = odontologoService.obtenerPorId(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Listar especialidades disponibles (RF 04 - Paso 1)
     */
    @GetMapping("/especialidades")
    public ResponseEntity<ApiResponse<List<EspecialidadResponse>>> listarEspecialidades() {
        try {
            List<EspecialidadResponse> especialidades = odontologoService.obtenerTodasEspecialidades();
            return ResponseEntity.ok(ApiResponse.success(especialidades));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
