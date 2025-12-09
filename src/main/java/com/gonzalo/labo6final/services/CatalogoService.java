package com.gonzalo.labo6final.services;

import com.gonzalo.labo6final.DTO.MotivoConsultaResponse;
import com.gonzalo.labo6final.DTO.ObraSocialCatalogoResponse;
import com.gonzalo.labo6final.repositories.MotivoConsultaRepository;
import com.gonzalo.labo6final.repositories.ObraSocialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogoService {

    private final MotivoConsultaRepository motivoConsultaRepository;
    private final ObraSocialRepository obraSocialRepository;

    public List<MotivoConsultaResponse> obtenerMotivosConsulta() {
        return motivoConsultaRepository.findAll().stream()
                .map(m -> new MotivoConsultaResponse(m.getIdMotivo(), m.getDescripcion()))
                .toList();
    }

    public List<ObraSocialCatalogoResponse> obtenerObrasSociales() {
        return obraSocialRepository.findAll().stream()
                .map(os -> new ObraSocialCatalogoResponse(os.getIdObraSocial(), os.getNombre()))
                .toList();
    }
}
