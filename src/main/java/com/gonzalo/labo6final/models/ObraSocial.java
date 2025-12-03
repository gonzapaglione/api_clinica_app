package com.gonzalo.labo6final.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "obra_social")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObraSocial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_obra_social")
    private Integer idObraSocial;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre; // 'OSDE', 'OSPE', 'PARTICULAR'
}
