package com.gonzalo.labo6final.services;

import com.gonzalo.labo6final.DTO.*;
import com.gonzalo.labo6final.models.*;
import com.gonzalo.labo6final.repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final ObraSocialRepository obraSocialRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        // Actualizar todos los campos excepto ID y email
        paciente.setDni(request.getDni());
        paciente.setNombre(request.getNombre());
        paciente.setApellido(request.getApellido());
        paciente.setTelefono(request.getTelefono());
        paciente.setDireccion(request.getDireccion());

        // Si se proporciona password, actualizarlo
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            paciente.getUsuario().setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Actualizar obras sociales si se proporcionan
        if (request.getObrasSociales() != null && !request.getObrasSociales().isEmpty()) {
            // Eliminar todas las obras sociales existentes EXCEPTO PARTICULAR (id=1)
            entityManager.createQuery(
                    "DELETE FROM PacienteObraSocial pos WHERE pos.idPaciente = :idPaciente AND pos.idObraSocial != 1")
                    .setParameter("idPaciente", id)
                    .executeUpdate();
            entityManager.flush();

            // Agregar las nuevas obras sociales
            for (ObraSocialResponse osRequest : request.getObrasSociales()) {
                // Verificar si ya existe la relación (para evitar duplicados con PARTICULAR)
                Long count = entityManager.createQuery(
                        "SELECT COUNT(pos) FROM PacienteObraSocial pos WHERE pos.idPaciente = :idPaciente AND pos.idObraSocial = :idObraSocial",
                        Long.class)
                        .setParameter("idPaciente", id)
                        .setParameter("idObraSocial", osRequest.getIdObraSocial())
                        .getSingleResult();

                if (count == 0) {
                    ObraSocial obraSocial = obraSocialRepository.findById(osRequest.getIdObraSocial())
                            .orElseThrow(() -> new RuntimeException(
                                    "Obra social no encontrada: " + osRequest.getIdObraSocial()));

                    PacienteObraSocial pos = new PacienteObraSocial();
                    pos.setIdPaciente(id);
                    pos.setIdObraSocial(obraSocial.getIdObraSocial());
                    pos.setPaciente(paciente);
                    pos.setObraSocial(obraSocial);

                    entityManager.persist(pos);
                }
            }

            // Asegurar que PARTICULAR siempre esté presente
            Long particularCount = entityManager.createQuery(
                    "SELECT COUNT(pos) FROM PacienteObraSocial pos WHERE pos.idPaciente = :idPaciente AND pos.idObraSocial = 1",
                    Long.class)
                    .setParameter("idPaciente", id)
                    .getSingleResult();

            if (particularCount == 0) {
                ObraSocial particular = obraSocialRepository.findById(1)
                        .orElseThrow(() -> new RuntimeException("Obra social PARTICULAR no encontrada"));

                PacienteObraSocial pos = new PacienteObraSocial();
                pos.setIdPaciente(id);
                pos.setIdObraSocial(1);
                pos.setPaciente(paciente);
                pos.setObraSocial(particular);

                entityManager.persist(pos);
            }
        }

        paciente = pacienteRepository.save(paciente);
        entityManager.refresh(paciente); // Refrescar para obtener las obras sociales actualizadas
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
        response.setPassword(paciente.getUsuario().getPassword());

        if (paciente.getObrasSociales() != null) {
            response.setObrasSociales(paciente.getObrasSociales().stream()
                    .map(pos -> new ObraSocialResponse(
                            pos.getObraSocial().getIdObraSocial(),
                            pos.getObraSocial().getNombre()))
                    .toList());
        }

        return response;
    }
}
