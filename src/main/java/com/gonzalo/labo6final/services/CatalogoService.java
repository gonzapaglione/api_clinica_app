package com.gonzalo.labo6final.services;

import com.gonzalo.labo6final.DTO.MotivoConsultaResponse;
import com.gonzalo.labo6final.repositories.MotivoConsultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogoService {

    private final MotivoConsultaRepository motivoConsultaRepository;

    public List<MotivoConsultaResponse> obtenerMotivosConsulta() {
        return motivoConsultaRepository.findAll().stream()
                .map(m -> new MotivoConsultaResponse(m.getIdMotivo(), m.getDescripcion()))
                .toList();
    }
}
