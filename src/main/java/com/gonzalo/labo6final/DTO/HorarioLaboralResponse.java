package com.gonzalo.labo6final.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioLaboralResponse {
    private Integer idHorario;
    private Integer idOdontologo;
    private String nombreOdontologo;
    private String apellidoOdontologo;
    private DayOfWeek diaSemana;
    private String diaSemanaTexto;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Boolean esDobleTurno;
    private LocalTime horaInicioTurno2;
    private LocalTime horaFinTurno2;
    private Boolean activo;
}
