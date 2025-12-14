package com.gonzalo.labo6final.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteTurnosStatsResponse {
    private long total;
    private long programados;
    private long realizados;
    private long ausentes;
    private long cancelados;
}
