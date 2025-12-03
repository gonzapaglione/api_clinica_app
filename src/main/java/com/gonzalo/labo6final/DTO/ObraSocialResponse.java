package com.gonzalo.labo6final.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObraSocialResponse {
    private Integer idObraSocial;
    private String nombre;
    private String nroAfiliado;
}
