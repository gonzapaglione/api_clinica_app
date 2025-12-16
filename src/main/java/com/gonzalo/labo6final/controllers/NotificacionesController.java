package com.gonzalo.labo6final.controllers;

import com.gonzalo.labo6final.DTO.ApiResponse;
import com.gonzalo.labo6final.DTO.RegistrarFcmTokenRequest;
import com.gonzalo.labo6final.services.NotificacionesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificacionesController {

    private final NotificacionesService notificacionesService;

    @PostMapping("/fcm-token")
    public ResponseEntity<ApiResponse<Void>> registrarToken(@RequestBody RegistrarFcmTokenRequest request) {
        try {
            notificacionesService.registrarTokenFcm(request);
            return ResponseEntity.ok(ApiResponse.success("Token registrado", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/fcm-token/paciente/{idPaciente}")
    public ResponseEntity<ApiResponse<Void>> desregistrarToken(@PathVariable Integer idPaciente) {
        try {
            notificacionesService.desregistrarTokenFcm(idPaciente);
            return ResponseEntity.ok(ApiResponse.success("Token eliminado", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
