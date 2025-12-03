package com.gonzalo.labo6final.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OdontologoEspecialidadId implements Serializable {
    private Integer idOdontologo;
    private Integer idEspecialidad;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OdontologoEspecialidadId that = (OdontologoEspecialidadId) o;
        return Objects.equals(idOdontologo, that.idOdontologo) &&
                Objects.equals(idEspecialidad, that.idEspecialidad);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOdontologo, idEspecialidad);
    }
}
