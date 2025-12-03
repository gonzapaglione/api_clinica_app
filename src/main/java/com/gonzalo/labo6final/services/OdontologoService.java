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
public class OdontologoService {

    private final OdontologoRepository odontologoRepository;
    private final EspecialidadRepository especialidadRepository;

    public OdontologoResponse obtenerPorId(Integer id) {
        Odontologo odontologo = odontologoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));
        return convertirAResponse(odontologo);
    }

    public List<OdontologoResponse> obtenerTodos() {
        return odontologoRepository.findAll().stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<OdontologoResponse> obtenerPorEspecialidad(Integer idEspecialidad) {
        return odontologoRepository.findByEspecialidadId(idEspecialidad).stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional
    public OdontologoResponse actualizarDatos(Integer id, OdontologoResponse request) {
        Odontologo odontologo = odontologoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        odontologo.setNombre(request.getNombre());
        odontologo.setApellido(request.getApellido());

        odontologo = odontologoRepository.save(odontologo);
        return convertirAResponse(odontologo);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!odontologoRepository.existsById(id)) {
            throw new RuntimeException("Odontólogo no encontrado");
        }
        odontologoRepository.deleteById(id);
    }

    public List<EspecialidadResponse> obtenerTodasEspecialidades() {
        return especialidadRepository.findAll().stream()
                .map(e -> new EspecialidadResponse(e.getIdEspecialidad(), e.getNombre()))
                .toList();
    }

    private OdontologoResponse convertirAResponse(Odontologo odontologo) {
        OdontologoResponse response = new OdontologoResponse();
        response.setIdOdontologo(odontologo.getIdOdontologo());
        response.setNombre(odontologo.getNombre());
        response.setApellido(odontologo.getApellido());
        response.setEmail(odontologo.getUsuario().getEmail());

        if (odontologo.getEspecialidades() != null) {
            response.setEspecialidades(odontologo.getEspecialidades().stream()
                    .map(oe -> new EspecialidadResponse(
                            oe.getEspecialidad().getIdEspecialidad(),
                            oe.getEspecialidad().getNombre()))
                    .toList());
        }

        return response;
    }
}
