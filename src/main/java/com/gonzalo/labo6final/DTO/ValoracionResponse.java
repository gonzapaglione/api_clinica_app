package com.gonzalo.labo6final.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValoracionResponse {
    private Integer idValoracion;
    private Integer idTurno;
    private Integer idPaciente;
    private Integer idOdontologo;
    private Integer estrellas;
    private String comentario;
    private LocalDateTime fecha;
}
