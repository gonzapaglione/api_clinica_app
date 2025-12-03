package com.gonzalo.labo6final.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroOdontologoRequest {
    private String email;
    private String password;
    private String nombre;
    private String apellido;
    private List<Integer> especialidades;
}
