package com.gonzalo.labo6final.controllers;

import com.gonzalo.labo6final.DTO.*;
import com.gonzalo.labo6final.services.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CatalogoController {

    private final CatalogoService catalogoService;

    /**
     * Obtener motivos de consulta disponibles (RF 04 - Paso 3)
     */
    @GetMapping("/motivos-consulta")
    public ResponseEntity<ApiResponse<List<MotivoConsultaResponse>>> obtenerMotivosConsulta() {
        try {
            List<MotivoConsultaResponse> motivos = catalogoService.obtenerMotivosConsulta();
            return ResponseEntity.ok(ApiResponse.success(motivos));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
