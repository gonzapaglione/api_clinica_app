package com.gonzalo.labo6final.controllers;

import com.gonzalo.labo6final.DTO.*;
import com.gonzalo.labo6final.services.ValoracionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/valoraciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ValoracionController {

    private final ValoracionService valoracionService;

    @PostMapping
    public ResponseEntity<ApiResponse<ValoracionResponse>> crear(@RequestBody CrearValoracionRequest request) {
        try {
            ValoracionResponse response = valoracionService.crearValoracion(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Valoración creada exitosamente", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/odontologo/{idOdontologo}")
    public ResponseEntity<ApiResponse<List<ValoracionResponse>>> listarPorOdontologo(
            @PathVariable Integer idOdontologo) {
        try {
            List<ValoracionResponse> response = valoracionService.listarPorOdontologo(idOdontologo);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/odontologo/{idOdontologo}/promedio")
    public ResponseEntity<ApiResponse<PromedioValoracionResponse>> promedio(@PathVariable Integer idOdontologo) {
        try {
            PromedioValoracionResponse response = valoracionService.obtenerPromedioPorOdontologo(idOdontologo);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
