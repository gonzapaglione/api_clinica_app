package com.gonzalo.labo6final.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioLaboralRequest {
    private Integer idOdontologo;
    private DayOfWeek diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Boolean esDobleTurno;

    // Para doble turno
    private LocalTime horaInicioTurno2;
    private LocalTime horaFinTurno2;
}
