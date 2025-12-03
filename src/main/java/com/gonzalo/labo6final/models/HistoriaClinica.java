package com.gonzalo.labo6final.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "historia_clinica")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoriaClinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historia")
    private Integer idHistoria;

    @OneToOne
    @JoinColumn(name = "id_turno", nullable = false, unique = true)
    private Turno turno;

    @Column(name = "diagnostico", columnDefinition = "TEXT")
    private String diagnostico;

    @Column(name = "tratamiento_realizado", columnDefinition = "TEXT")
    private String tratamientoRealizado;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}
