package com.gonzalo.labo6final.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteObraSocialId implements Serializable {
    private Integer idPaciente;
    private Integer idObraSocial;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PacienteObraSocialId that = (PacienteObraSocialId) o;
        return Objects.equals(idPaciente, that.idPaciente) &&
                Objects.equals(idObraSocial, that.idObraSocial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPaciente, idObraSocial);
    }
}
