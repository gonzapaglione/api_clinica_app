package com.gonzalo.labo6final.services;

import com.gonzalo.labo6final.DTO.*;
import com.gonzalo.labo6final.models.*;
import com.gonzalo.labo6final.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoriaClinicaService {

    private final HistoriaClinicaRepository historiaClinicaRepository;
    private final TurnoRepository turnoRepository;

    @Transactional
    public HistoriaClinicaResponse crearHistoriaClinica(HistoriaClinicaRequest request) {
        // Validar que el turno existe
        Turno turno = turnoRepository.findById(request.getIdTurno())
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        // Verificar que el turno esté REALIZADO
        if (!"REALIZADO".equals(turno.getEstadoTurno().getNombre())) {
            throw new RuntimeException("Solo se puede crear historia clínica para turnos realizados");
        }

        // Verificar que no exista ya una historia clínica para este turno
        if (historiaClinicaRepository.existsByTurnoIdTurno(request.getIdTurno())) {
            throw new RuntimeException("Ya existe una historia clínica para este turno");
        }

        // Crear historia clínica
        HistoriaClinica historia = new HistoriaClinica();
        historia.setTurno(turno);
        historia.setDiagnostico(request.getDiagnostico());
        historia.setTratamientoRealizado(request.getTratamientoRealizado());
        historia.setObservaciones(request.getObservaciones());

        historia = historiaClinicaRepository.save(historia);
        return convertirAResponse(historia);
    }

    public HistoriaClinicaResponse obtenerPorId(Integer id) {
        HistoriaClinica historia = historiaClinicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historia clínica no encontrada"));
        return convertirAResponse(historia);
    }

    public HistoriaClinicaResponse obtenerPorTurno(Integer idTurno) {
        HistoriaClinica historia = historiaClinicaRepository.findByTurnoIdTurno(idTurno)
                .orElseThrow(() -> new RuntimeException("Historia clínica no encontrada para este turno"));
        return convertirAResponse(historia);
    }

    public List<HistoriaClinicaResponse> obtenerHistorialPaciente(Integer idPaciente) {
        return historiaClinicaRepository.findHistorialByPaciente(idPaciente).stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<HistoriaClinicaResponse> obtenerHistorialOdontologo(Integer idOdontologo) {
        return historiaClinicaRepository.findHistorialByOdontologo(idOdontologo).stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional
    public HistoriaClinicaResponse actualizarHistoriaClinica(Integer id, HistoriaClinicaRequest request) {
        HistoriaClinica historia = historiaClinicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historia clínica no encontrada"));

        historia.setDiagnostico(request.getDiagnostico());
        historia.setTratamientoRealizado(request.getTratamientoRealizado());
        historia.setObservaciones(request.getObservaciones());

        historia = historiaClinicaRepository.save(historia);
        return convertirAResponse(historia);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!historiaClinicaRepository.existsById(id)) {
            throw new RuntimeException("Historia clínica no encontrada");
        }
        historiaClinicaRepository.deleteById(id);
    }

    private HistoriaClinicaResponse convertirAResponse(HistoriaClinica historia) {
        Turno turno = historia.getTurno();

        HistoriaClinicaResponse response = new HistoriaClinicaResponse();
        response.setIdHistoria(historia.getIdHistoria());
        response.setIdTurno(turno.getIdTurno());

        // Datos del turno
        response.setFechaTurno(turno.getFecha());
        response.setHoraTurno(turno.getHora());

        // Datos del paciente
        response.setIdPaciente(turno.getPaciente().getIdPaciente());
        response.setNombrePaciente(turno.getPaciente().getNombre());
        response.setApellidoPaciente(turno.getPaciente().getApellido());
        response.setDniPaciente(turno.getPaciente().getDni());

        // Datos del odontólogo
        response.setIdOdontologo(turno.getOdontologo().getIdOdontologo());
        response.setNombreOdontologo(turno.getOdontologo().getNombre());
        response.setApellidoOdontologo(turno.getOdontologo().getApellido());

        // Información clínica
        response.setDiagnostico(historia.getDiagnostico());
        response.setTratamientoRealizado(historia.getTratamientoRealizado());
        response.setObservaciones(historia.getObservaciones());
        response.setMotivoConsulta(turno.getMotivoConsulta().getDescripcion());

        return response;
    }
}
