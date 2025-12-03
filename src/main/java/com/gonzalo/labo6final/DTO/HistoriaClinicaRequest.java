package com.gonzalo.labo6final.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriaClinicaRequest {
    private Integer idTurno;
    private String diagnostico;
    private String tratamientoRealizado;
    private String observaciones;
}
