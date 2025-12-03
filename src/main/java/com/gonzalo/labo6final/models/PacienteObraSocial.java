package com.gonzalo.labo6final.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "paciente_obra_social")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PacienteObraSocialId.class)
public class PacienteObraSocial {

    @Id
    @Column(name = "id_paciente")
    private Integer idPaciente;

    @Id
    @Column(name = "id_obra_social")
    private Integer idObraSocial;

    @ManyToOne
    @JoinColumn(name = "id_paciente", insertable = false, updatable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "id_obra_social", insertable = false, updatable = false)
    private ObraSocial obraSocial;

    @Column(name = "nro_afiliado", length = 50)
    private String nroAfiliado;
}
