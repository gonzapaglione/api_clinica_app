package com.gonzalo.labo6final.controllers;

import com.gonzalo.labo6final.DTO.*;
import com.gonzalo.labo6final.services.PacienteService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paciente")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PacienteController {

    private final PacienteService pacienteService;

    /**
     * Obtener perfil del paciente autenticado
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PacienteResponse>> obtenerPerfil(@PathVariable Integer id) {
        try {
            PacienteResponse response = pacienteService.obtenerPorId(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Actualizar datos del perfil del paciente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PacienteResponse>> actualizarPerfil(
            @PathVariable Integer id,
            @RequestBody PacienteResponse request) {
        try {
            PacienteResponse response = pacienteService.actualizarDatos(id, request);
            return ResponseEntity.ok(ApiResponse.success("Perfil actualizado exitosamente", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Cambiar contraseña del paciente
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> cambiarPassword(
            @PathVariable Integer id,
            @RequestBody CambiarPasswordRequest request) {
        try {
            pacienteService.cambiarPassword(id, request);
            return ResponseEntity.ok(ApiResponse.success("Contraseña actualizada exitosamente", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
