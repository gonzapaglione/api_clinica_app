package com.gonzalo.labo6final.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "odontologo_especialidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(OdontologoEspecialidadId.class)
public class OdontologoEspecialidad {

    @Id
    @Column(name = "id_odontologo")
    private Integer idOdontologo;

    @Id
    @Column(name = "id_especialidad")
    private Integer idEspecialidad;

    @ManyToOne
    @JoinColumn(name = "id_odontologo", insertable = false, updatable = false)
    private Odontologo odontologo;

    @ManyToOne
    @JoinColumn(name = "id_especialidad", insertable = false, updatable = false)
    private Especialidad especialidad;
}
