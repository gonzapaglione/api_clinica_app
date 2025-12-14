package com.gonzalo.labo6final.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnosHoyResponse {
    private long cantidad;
    private java.util.List<TurnoHoyItemResponse> turnos;
}
