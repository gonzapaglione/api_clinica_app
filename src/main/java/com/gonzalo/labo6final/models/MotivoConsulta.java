package com.gonzalo.labo6final.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "motivo_consulta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotivoConsulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_motivo")
    private Integer idMotivo;

    @Column(name = "descripcion", nullable = false, length = 100)
    private String descripcion; // 'Dolor de muela', 'Blanqueamiento', 'Consulta General'
}
