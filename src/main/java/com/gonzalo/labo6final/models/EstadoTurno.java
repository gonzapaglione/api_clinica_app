package com.gonzalo.labo6final.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_turno")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoTurno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Integer idEstado;

    @Column(name = "nombre", nullable = false, length = 20)
    private String nombre; // 'PROGRAMADO', 'CANCELADO', 'REALIZADO', 'AUSENTE'
}
