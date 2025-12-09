package com.gonzalo.labo6final.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteResponse {
    private Integer idPaciente;
    private String dni;
    private String nombre;
    private String apellido;
    private String telefono;
    private String direccion;
    private String email;
    private String password;
    private List<ObraSocialResponse> obrasSociales;
}
