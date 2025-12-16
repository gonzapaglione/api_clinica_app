package com.gonzalo.labo6final.controllers;

import com.gonzalo.labo6final.DTO.ApiResponse;
import com.gonzalo.labo6final.DTO.RegistrarFcmTokenRequest;
import com.gonzalo.labo6final.services.NotificacionesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
