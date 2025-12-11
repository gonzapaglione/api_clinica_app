package com.gonzalo.labo6final.services;

import com.gonzalo.labo6final.DTO.*;
import com.gonzalo.labo6final.models.*;
import com.gonzalo.labo6final.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PacienteRepository pacienteRepository;
    private final OdontologoRepository odontologoRepository;
    private final ObraSocialRepository obraSocialRepository;
    private final EspecialidadRepository especialidadRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String nombre = "";
        String apellido = "";

        // Obtener nombre y apellido según el rol
        if ("PACIENTE".equals(usuario.getRol().getNombre())) {
            Paciente paciente = pacienteRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
            nombre = paciente.getNombre();
            apellido = paciente.getApellido();
        } else if ("ODONTOLOGO".equals(usuario.getRol().getNombre())) {
            Odontologo odontologo = odontologoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));
            nombre = odontologo.getNombre();
            apellido = odontologo.getApellido();
        }

        return new LoginResponse(
                usuario.getEmail(),
                usuario.getRol().getNombre(),
                usuario.getIdUsuario(),
                nombre,
                apellido);
    }

    @Transactional
    public PacienteResponse registrarPaciente(RegistroPacienteRequest request) {
        // Validar que el email no esté registrado
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Validar que el DNI no esté registrado
        if (pacienteRepository.existsByDni(request.getDni())) {
            throw new RuntimeException("El DNI ya está registrado");
        }

        // Buscar el rol PACIENTE
        Rol rolPaciente = rolRepository.findByNombre("PACIENTE")
                .orElseThrow(() -> new RuntimeException("Rol PACIENTE no encontrado"));

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rolPaciente);
        usuario = usuarioRepository.save(usuario);

        // Crear paciente
        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setDni(request.getDni());
        paciente.setNombre(request.getNombre());
        paciente.setApellido(request.getApellido());
        paciente.setTelefono(request.getTelefono());
        paciente.setDireccion(request.getDireccion());
        paciente.setObrasSociales(new ArrayList<>());
        paciente = pacienteRepository.save(paciente);

        // Siempre agregar PARTICULAR por defecto
        ObraSocial particular = obraSocialRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Obra social PARTICULAR no encontrada"));

        PacienteObraSocial posParticular = new PacienteObraSocial();
        posParticular.setIdPaciente(paciente.getIdPaciente());
        posParticular.setIdObraSocial(1);
        posParticular.setPaciente(paciente);
        posParticular.setObraSocial(particular);
        paciente.getObrasSociales().add(posParticular);

        // Agregar obras sociales adicionales si existen (sin duplicar PARTICULAR)
        if (request.getObrasSociales() != null && !request.getObrasSociales().isEmpty()) {
            for (ObraSocialRequest osRequest : request.getObrasSociales()) {
                // Saltar PARTICULAR si ya está agregado
                if (osRequest.getIdObraSocial() == 1) {
                    continue;
                }

                ObraSocial obraSocial = obraSocialRepository.findById(osRequest.getIdObraSocial())
                        .orElseThrow(() -> new RuntimeException("Obra social no encontrada"));

                PacienteObraSocial pos = new PacienteObraSocial();
                pos.setIdPaciente(paciente.getIdPaciente());
                pos.setIdObraSocial(obraSocial.getIdObraSocial());
                pos.setPaciente(paciente);
                pos.setObraSocial(obraSocial);

                paciente.getObrasSociales().add(pos);
            }
        }

        // Convertir a response
        return convertirPacienteAResponse(paciente);
    }

    @Transactional
    public OdontologoResponse registrarOdontologo(RegistroOdontologoRequest request) {
        // Validar que el email no esté registrado
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Buscar el rol ODONTOLOGO
        Rol rolOdontologo = rolRepository.findByNombre("ODONTOLOGO")
                .orElseThrow(() -> new RuntimeException("Rol ODONTOLOGO no encontrado"));

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rolOdontologo);
        usuario = usuarioRepository.save(usuario);

        // Crear odontólogo
        Odontologo odontologo = new Odontologo();
        odontologo.setUsuario(usuario);
        odontologo.setNombre(request.getNombre());
        odontologo.setApellido(request.getApellido());
        odontologo.setEspecialidades(new ArrayList<>());
        odontologo = odontologoRepository.save(odontologo);

        // Agregar especialidades si existen
        if (request.getEspecialidades() != null && !request.getEspecialidades().isEmpty()) {
            for (Integer idEspecialidad : request.getEspecialidades()) {
                Especialidad especialidad = especialidadRepository.findById(idEspecialidad)
                        .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));

                OdontologoEspecialidad oe = new OdontologoEspecialidad();
                oe.setIdOdontologo(odontologo.getIdOdontologo());
                oe.setIdEspecialidad(especialidad.getIdEspecialidad());
                oe.setOdontologo(odontologo);
                oe.setEspecialidad(especialidad);

                odontologo.getEspecialidades().add(oe);
            }
        }

        return convertirOdontologoAResponse(odontologo);
    }

    private PacienteResponse convertirPacienteAResponse(Paciente paciente) {
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
                            pos.getObraSocial().getNombre()))
                    .toList());
        }

        return response;
    }

    private OdontologoResponse convertirOdontologoAResponse(Odontologo odontologo) {
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
