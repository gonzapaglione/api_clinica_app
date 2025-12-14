package com.gonzalo.labo6final.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoHoyItemResponse {
    private Integer idTurno;
    private String hora;
    private String estado;
    private String odontologo;
    private String motivo;
}
