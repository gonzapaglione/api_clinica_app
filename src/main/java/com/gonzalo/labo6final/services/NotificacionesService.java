package com.gonzalo.labo6final.services;

import com.gonzalo.labo6final.DTO.RegistrarFcmTokenRequest;
import com.gonzalo.labo6final.models.Paciente;
import com.gonzalo.labo6final.models.Usuario;
import com.gonzalo.labo6final.repositories.PacienteRepository;
import com.gonzalo.labo6final.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        String token = request.getToken().trim();

        Paciente paciente = pacienteRepository.findById(request.getIdPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Usuario usuario = paciente.getUsuario();
        if (usuario == null || usuario.getIdUsuario() == null) {
            throw new RuntimeException("Usuario del paciente no encontrado");
        }

        // Si el token ya está registrado para este usuario, no hacemos nada.
        if (token.equals(usuario.getFcmToken())) {
            return;
        }

        // Evita que un mismo token (dispositivo/app) quede asociado a múltiples
        // usuarios.
        // Esto es clave si en el mismo teléfono se loguean distintos usuarios.
        List<Usuario> usuariosConEseToken = usuarioRepository.findAllByFcmToken(token);
        for (Usuario u : usuariosConEseToken) {
            if (u.getIdUsuario() != null && !u.getIdUsuario().equals(usuario.getIdUsuario())) {
                u.setFcmToken(null);
            }
        }
        if (!usuariosConEseToken.isEmpty()) {
            usuarioRepository.saveAll(usuariosConEseToken);
        }

        usuario.setFcmToken(token);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void desregistrarTokenFcm(Integer idPaciente) {
        if (idPaciente == null) {
            throw new RuntimeException("idPaciente es requerido");
        }

        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Usuario usuario = paciente.getUsuario();
        if (usuario == null || usuario.getIdUsuario() == null) {
            throw new RuntimeException("Usuario del paciente no encontrado");
        }

        if (usuario.getFcmToken() == null) {
            return;
        }

        usuario.setFcmToken(null);
        usuarioRepository.save(usuario);
    }
}
