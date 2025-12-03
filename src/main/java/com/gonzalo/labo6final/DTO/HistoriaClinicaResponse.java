package com.gonzalo.labo6final.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriaClinicaResponse {
    private Integer idHistoria;
    private Integer idTurno;

    // Datos del turno
    private LocalDate fechaTurno;
    private LocalTime horaTurno;

    // Datos del paciente
    private Integer idPaciente;
    private String nombrePaciente;
    private String apellidoPaciente;
    private String dniPaciente;

    // Datos del odontólogo
    private Integer idOdontologo;
    private String nombreOdontologo;
    private String apellidoOdontologo;

    // Información clínica
    private String diagnostico;
    private String tratamientoRealizado;
    private String observaciones;
    private String motivoConsulta;
}
