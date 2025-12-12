package com.gonzalo.labo6final.services;

import com.gonzalo.labo6final.DTO.CrearValoracionRequest;
import com.gonzalo.labo6final.DTO.PromedioValoracionResponse;
import com.gonzalo.labo6final.DTO.ValoracionResponse;
import com.gonzalo.labo6final.models.*;
import com.gonzalo.labo6final.repositories.TurnoRepository;
import com.gonzalo.labo6final.repositories.ValoracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ValoracionService {

    private final ValoracionRepository valoracionRepository;
    private final TurnoRepository turnoRepository;

    @Transactional
    public ValoracionResponse crearValoracion(CrearValoracionRequest request) {
        if (request.getIdTurno() == null) {
            throw new RuntimeException("El idTurno es obligatorio");
        }
        if (request.getIdPaciente() == null) {
            throw new RuntimeException("El idPaciente es obligatorio");
        }
        if (request.getEstrellas() == null) {
            throw new RuntimeException("Las estrellas son obligatorias");
        }
        if (request.getEstrellas() < 1 || request.getEstrellas() > 5) {
            throw new RuntimeException("Las estrellas deben estar entre 1 y 5");
        }

        Turno turno = turnoRepository.findById(request.getIdTurno())
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        if (!"REALIZADO".equals(turno.getEstadoTurno().getNombre())) {
            throw new RuntimeException("Solo se puede valorar un turno en estado REALIZADO");
        }

        if (!request.getIdPaciente().equals(turno.getPaciente().getIdPaciente())) {
            throw new RuntimeException("El turno no pertenece al paciente indicado");
        }

        if (valoracionRepository.existsByTurnoIdTurno(turno.getIdTurno())) {
            throw new RuntimeException("Este turno ya tiene una valoración");
        }

        Valoracion valoracion = new Valoracion();
        valoracion.setTurno(turno);
        valoracion.setPaciente(turno.getPaciente());
        valoracion.setOdontologo(turno.getOdontologo());
        valoracion.setEstrellas(request.getEstrellas());
        valoracion.setComentario(request.getComentario());

        valoracion = valoracionRepository.save(valoracion);
        return convertirAResponse(valoracion);
    }

    public List<ValoracionResponse> listarPorOdontologo(Integer idOdontologo) {
        return valoracionRepository.findByOdontologoIdOdontologo(idOdontologo).stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public PromedioValoracionResponse obtenerPromedioPorOdontologo(Integer idOdontologo) {
        Double promedio = valoracionRepository.obtenerPromedioEstrellasPorOdontologo(idOdontologo);
        long cantidad = valoracionRepository.countByOdontologoIdOdontologo(idOdontologo);
        return new PromedioValoracionResponse(idOdontologo, promedio, cantidad);
    }

    private ValoracionResponse convertirAResponse(Valoracion valoracion) {
        return new ValoracionResponse(
                valoracion.getIdValoracion(),
                valoracion.getTurno().getIdTurno(),
                valoracion.getPaciente().getIdPaciente(),
                valoracion.getOdontologo().getIdOdontologo(),
                valoracion.getEstrellas(),
                valoracion.getComentario(),
                valoracion.getFecha());
    }
}
