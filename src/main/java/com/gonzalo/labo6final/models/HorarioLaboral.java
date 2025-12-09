package com.gonzalo.labo6final.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "horarios_laborales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorarioLaboral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario")
    private Integer idHorario;

    @ManyToOne
    @JoinColumn(name = "id_odontologo", nullable = false)
    private Odontologo odontologo;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DayOfWeek diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "es_doble_turno", nullable = false)
    private Boolean esDobleTurno = false;

    // Para doble turno (turno tarde)
    @Column(name = "hora_inicio_turno2")
    private LocalTime horaInicioTurno2;

    @Column(name = "hora_fin_turno2")
    private LocalTime horaFinTurno2;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}
