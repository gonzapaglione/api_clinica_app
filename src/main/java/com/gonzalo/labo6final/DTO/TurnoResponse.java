package com.gonzalo.labo6final.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoResponse {
    private Integer idTurno;
    private LocalDate fecha;
    private LocalTime hora;
    private String estadoTurno;
    private String motivoConsulta;
    private LocalDateTime fechaSolicitud;

    // Datos del paciente
    private Integer idPaciente;
    private String nombrePaciente;
    private String apellidoPaciente;
    private String dniPaciente;

    // Datos del odontólogo
    private Integer idOdontologo;
    private String nombreOdontologo;
    private String apellidoOdontologo;

    // Obra social
    private String obraSocial;

    // Notas de cancelación (si aplica)
    private String notasCancelacion;

    // Valoración (si existe)
    private Integer idValoracion;
    private Integer estrellasValoracion;
    private String comentarioValoracion;
}
