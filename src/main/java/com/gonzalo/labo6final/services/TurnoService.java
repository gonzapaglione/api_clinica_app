package com.gonzalo.labo6final.services;

import com.gonzalo.labo6final.DTO.*;
import com.gonzalo.labo6final.models.*;
import com.gonzalo.labo6final.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final PacienteRepository pacienteRepository;
    private final OdontologoRepository odontologoRepository;
    private final MotivoConsultaRepository motivoConsultaRepository;
    private final EstadoTurnoRepository estadoTurnoRepository;
    private final ObraSocialRepository obraSocialRepository;
    private final HorarioLaboralRepository horarioLaboralRepository;
    private final ValoracionRepository valoracionRepository;

    @Transactional
    public TurnoResponse crearTurno(TurnoRequest request) {
        // Validar que el paciente existe
        Paciente paciente = pacienteRepository.findById(request.getIdPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Validar que el odontólogo existe
        Odontologo odontologo = odontologoRepository.findById(request.getIdOdontologo())
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        // Validar que el motivo existe
        MotivoConsulta motivo = motivoConsultaRepository.findById(request.getIdMotivo())
                .orElseThrow(() -> new RuntimeException("Motivo de consulta no encontrado"));

        // Validar que el odontólogo trabaja ese día y en ese horario
        java.time.DayOfWeek diaSemana = request.getFecha().getDayOfWeek();
        boolean trabajaEseDia = horarioLaboralRepository.findHorarioActivo(request.getIdOdontologo(), diaSemana)
                .map(horario -> {
                    // Verificar turno 1
                    boolean enTurno1 = !request.getHora().isBefore(horario.getHoraInicio()) &&
                            request.getHora().isBefore(horario.getHoraFin());

                    // Verificar turno 2 si existe
                    boolean enTurno2 = false;
                    if (Boolean.TRUE.equals(horario.getEsDobleTurno()) &&
                            horario.getHoraInicioTurno2() != null) {
                        enTurno2 = !request.getHora().isBefore(horario.getHoraInicioTurno2()) &&
                                request.getHora().isBefore(horario.getHoraFinTurno2());
                    }

                    return enTurno1 || enTurno2;
                })
                .orElse(false);

        if (!trabajaEseDia) {
            throw new RuntimeException("El odontólogo no trabaja en ese día/horario");
        }

        // Validar disponibilidad
        if (turnoRepository.existsTurnoByOdontologoAndFechaAndHora(
                request.getIdOdontologo(), request.getFecha(), request.getHora())) {
            throw new RuntimeException("El horario no está disponible");
        }

        // Validar que la fecha no sea pasada
        if (request.getFecha().isBefore(LocalDate.now())) {
            throw new RuntimeException("No se pueden agendar turnos en fechas pasadas");
        }

        // Buscar estado PROGRAMADO
        EstadoTurno estadoProgramado = estadoTurnoRepository.findByNombre("PROGRAMADO")
                .orElseThrow(() -> new RuntimeException("Estado PROGRAMADO no encontrado"));

        // Crear turno
        Turno turno = new Turno();
        turno.setFecha(request.getFecha());
        turno.setHora(request.getHora());
        turno.setPaciente(paciente);
        turno.setOdontologo(odontologo);
        turno.setMotivoConsulta(motivo);
        turno.setEstadoTurno(estadoProgramado);

        // Obra social opcional
        if (request.getIdObraSocial() != null) {
            ObraSocial obraSocial = obraSocialRepository.findById(request.getIdObraSocial())
                    .orElseThrow(() -> new RuntimeException("Obra social no encontrada"));
            turno.setObraSocial(obraSocial);
        }

        turno = turnoRepository.save(turno);
        return convertirAResponse(turno);
    }

    public TurnoResponse obtenerPorId(Integer id) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        return convertirAResponse(turno);
    }

    public List<TurnoResponse> obtenerTurnosPorPaciente(Integer idPaciente) {
        return turnoRepository.findByPacienteIdPaciente(idPaciente).stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<TurnoResponse> obtenerTurnosPorOdontologo(Integer idOdontologo) {
        return turnoRepository.findByOdontologoIdOdontologo(idOdontologo).stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<TurnoResponse> obtenerProximosTurnosPaciente(Integer idPaciente) {
        return turnoRepository.findProximosTurnosByPaciente(idPaciente, LocalDate.now()).stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<TurnoResponse> obtenerTurnosDelDia(Integer idOdontologo, LocalDate fecha) {
        return turnoRepository.findTurnosDelDiaByOdontologo(idOdontologo, fecha).stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<TurnoResponse> obtenerHistorialPaciente(Integer idPaciente) {
        return turnoRepository.findHistorialByPaciente(idPaciente).stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public boolean verificarDisponibilidad(Integer idOdontologo, LocalDate fecha, LocalTime hora) {
        return !turnoRepository.existsTurnoByOdontologoAndFechaAndHora(idOdontologo, fecha, hora);
    }

    public List<DisponibilidadResponse> obtenerHorariosDisponibles(Integer idOdontologo, LocalDate fecha) {
        // Validar que el odontólogo existe
        odontologoRepository.findById(idOdontologo)
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        // Validar que la fecha no sea pasada
        if (fecha.isBefore(LocalDate.now())) {
            throw new RuntimeException("No se pueden consultar horarios de fechas pasadas");
        }

        // Obtener fecha y hora actual
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();
        boolean esHoy = fecha.equals(hoy);

        // Obtener el día de la semana
        java.time.DayOfWeek diaSemana = fecha.getDayOfWeek();

        // Buscar el horario laboral del odontólogo para ese día
        return horarioLaboralRepository.findHorarioActivo(idOdontologo, diaSemana)
                .map(horario -> {
                    List<DisponibilidadResponse> horarios = new java.util.ArrayList<>();

                    // Generar slots del turno 1
                    LocalTime horaActual = horario.getHoraInicio();
                    while (horaActual.isBefore(horario.getHoraFin())) {
                        // Si es hoy, solo mostrar horarios futuros (con al menos 30 min de margen)
                        if (!esHoy || horaActual.isAfter(ahora.plusMinutes(30))) {
                            boolean disponible = verificarDisponibilidad(idOdontologo, fecha, horaActual);
                            horarios.add(new DisponibilidadResponse(fecha, horaActual, disponible));
                        }
                        horaActual = horaActual.plusMinutes(30);
                    }

                    // Generar slots del turno 2 si existe
                    if (Boolean.TRUE.equals(horario.getEsDobleTurno()) &&
                            horario.getHoraInicioTurno2() != null &&
                            horario.getHoraFinTurno2() != null) {

                        horaActual = horario.getHoraInicioTurno2();
                        while (horaActual.isBefore(horario.getHoraFinTurno2())) {
                            // Si es hoy, solo mostrar horarios futuros (con al menos 30 min de margen)
                            if (!esHoy || horaActual.isAfter(ahora.plusMinutes(30))) {
                                boolean disponible = verificarDisponibilidad(idOdontologo, fecha, horaActual);
                                horarios.add(new DisponibilidadResponse(fecha, horaActual, disponible));
                            }
                            horaActual = horaActual.plusMinutes(30);
                        }
                    }

                    return horarios;
                })
                .orElse(java.util.Collections.emptyList()); // El odontólogo no trabaja ese día
    }

    @Transactional
    public TurnoResponse cancelarTurno(CancelarTurnoRequest request) {
        Turno turno = turnoRepository.findById(request.getIdTurno())
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        // Verificar que el turno esté en estado PROGRAMADO
        if (!"PROGRAMADO".equals(turno.getEstadoTurno().getNombre())) {
            throw new RuntimeException("Solo se pueden cancelar turnos programados");
        }

        // Buscar estado CANCELADO
        EstadoTurno estadoCancelado = estadoTurnoRepository.findByNombre("CANCELADO")
                .orElseThrow(() -> new RuntimeException("Estado CANCELADO no encontrado"));

        turno.setEstadoTurno(estadoCancelado);
        turno.setNotasCancelacion(request.getMotivo());

        turno = turnoRepository.save(turno);
        return convertirAResponse(turno);
    }

    @Transactional
    public TurnoResponse marcarComoRealizado(Integer idTurno) {
        Turno turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        EstadoTurno estadoRealizado = estadoTurnoRepository.findByNombre("REALIZADO")
                .orElseThrow(() -> new RuntimeException("Estado REALIZADO no encontrado"));

        turno.setEstadoTurno(estadoRealizado);
        turno = turnoRepository.save(turno);

        return convertirAResponse(turno);
    }

    @Transactional
    public TurnoResponse marcarComoAusente(Integer idTurno) {
        Turno turno = turnoRepository.findById(idTurno)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        EstadoTurno estadoAusente = estadoTurnoRepository.findByNombre("AUSENTE")
                .orElseThrow(() -> new RuntimeException("Estado AUSENTE no encontrado"));

        turno.setEstadoTurno(estadoAusente);
        turno = turnoRepository.save(turno);

        return convertirAResponse(turno);
    }

    private TurnoResponse convertirAResponse(Turno turno) {
        TurnoResponse response = new TurnoResponse();
        response.setIdTurno(turno.getIdTurno());
        response.setFecha(turno.getFecha());
        response.setHora(turno.getHora());
        response.setEstadoTurno(turno.getEstadoTurno().getNombre());
        response.setMotivoConsulta(turno.getMotivoConsulta().getDescripcion());
        response.setFechaSolicitud(turno.getFechaSolicitud());

        // Datos del paciente
        response.setIdPaciente(turno.getPaciente().getIdPaciente());
        response.setNombrePaciente(turno.getPaciente().getNombre());
        response.setApellidoPaciente(turno.getPaciente().getApellido());
        response.setDniPaciente(turno.getPaciente().getDni());

        // Datos del odontólogo
        response.setIdOdontologo(turno.getOdontologo().getIdOdontologo());
        response.setNombreOdontologo(turno.getOdontologo().getNombre());
        response.setApellidoOdontologo(turno.getOdontologo().getApellido());

        // Obra social
        if (turno.getObraSocial() != null) {
            response.setObraSocial(turno.getObraSocial().getNombre());
        }

        // Notas de cancelación
        response.setNotasCancelacion(turno.getNotasCancelacion());

        // Valoración (si existe)
        Optional<Valoracion> valoracionOpt = valoracionRepository.findFirstByTurnoIdTurno(turno.getIdTurno());
        if (valoracionOpt.isPresent()) {
            Valoracion v = valoracionOpt.get();
            response.setIdValoracion(v.getIdValoracion());
            response.setEstrellasValoracion(v.getEstrellas());
            response.setComentarioValoracion(v.getComentario());
        }

        return response;
    }
}
