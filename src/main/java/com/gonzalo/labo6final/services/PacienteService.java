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
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteResponse obtenerPorId(Integer id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        return convertirAResponse(paciente);
    }

    public PacienteResponse obtenerPorDni(String dni) {
        Paciente paciente = pacienteRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        return convertirAResponse(paciente);
    }

    public List<PacienteResponse> obtenerTodos() {
        return pacienteRepository.findAll().stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional
    public PacienteResponse actualizarDatos(Integer id, PacienteResponse request) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        paciente.setNombre(request.getNombre());
        paciente.setApellido(request.getApellido());
        paciente.setTelefono(request.getTelefono());
        paciente.setDireccion(request.getDireccion());

        paciente = pacienteRepository.save(paciente);
        return convertirAResponse(paciente);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!pacienteRepository.existsById(id)) {
            throw new RuntimeException("Paciente no encontrado");
        }
        pacienteRepository.deleteById(id);
    }

    private PacienteResponse convertirAResponse(Paciente paciente) {
        PacienteResponse response = new PacienteResponse();
        response.setIdPaciente(paciente.getIdPaciente());
        response.setDni(paciente.getDni());
        response.setNombre(paciente.getNombre());
        response.setApellido(paciente.getApellido());
        response.setTelefono(paciente.getTelefono());
        response.setDireccion(paciente.getDireccion());
        response.setEmail(paciente.getUsuario().getEmail());

        if (paciente.getObrasSociales() != null) {
            response.setObrasSociales(paciente.getObrasSociales().stream()
                    .map(pos -> new ObraSocialResponse(
                            pos.getObraSocial().getIdObraSocial(),
                            pos.getObraSocial().getNombre(),
                            pos.getNroAfiliado()))
                    .toList());
        }

        return response;
    }
}
