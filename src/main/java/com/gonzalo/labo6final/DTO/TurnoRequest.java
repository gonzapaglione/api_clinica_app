package com.gonzalo.labo6final.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoRequest {
    private LocalDate fecha;
    private LocalTime hora;
    private Integer idPaciente;
    private Integer idOdontologo;
    private Integer idMotivo;
    private Integer idObraSocial;
}
