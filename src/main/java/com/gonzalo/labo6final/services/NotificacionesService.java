package com.gonzalo.labo6final.services;

import com.gonzalo.labo6final.DTO.RegistrarFcmTokenRequest;
import com.gonzalo.labo6final.models.Paciente;
import com.gonzalo.labo6final.models.Usuario;
import com.gonzalo.labo6final.repositories.PacienteRepository;
import com.gonzalo.labo6final.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificacionesService {

    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void registrarTokenFcm(RegistrarFcmTokenRequest request) {
        if (request == null || request.getIdPaciente() == null) {
            throw new RuntimeException("idPaciente es requerido");
        }
        if (request.getToken() == null || request.getToken().trim().isEmpty()) {
            throw new RuntimeException("token es requerido");
        }

        Paciente paciente = pacienteRepository.findById(request.getIdPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Usuario usuario = paciente.getUsuario();
        if (usuario == null || usuario.getIdUsuario() == null) {
            throw new RuntimeException("Usuario del paciente no encontrado");
        }

        usuario.setFcmToken(request.getToken().trim());
        usuarioRepository.save(usuario);
    }
}
