package com.gonzalo.labo6final.services;

import com.gonzalo.labo6final.DTO.*;
import com.gonzalo.labo6final.models.*;
import com.gonzalo.labo6final.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final PacienteRepository pacienteRepository;
    private final OdontologoRepository odontologoRepository;
    private final MotivoConsultaRepository motivoConsultaRepository;
    private final EstadoTurnoRepository estadoTurnoRepository;
    private final ObraSocialRepository obraSocialRepository;
    private final HorarioLaboralRepository horarioLaboralRepository;
    private final ValoracionRepository valoracionRepository;

    private final FcmNotificationService fcmNotificationService;

    private static final DateTimeFormatter FECHA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA_FMT = DateTimeFormatter.ofPattern("HH:mm");

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

        // Si es hoy, no permitir crear turnos en horarios ya pasados
        if (request.getFecha().isEqual(LocalDate.now())) {
            LocalTime ahora = LocalTime.now();
            if (!request.getHora().isAfter(ahora)) {
                throw new RuntimeException("No se pueden agendar turnos en horarios pasados");
            }
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
                        // Si es hoy, solo mostrar horarios futuros
                        if (!esHoy || horaActual.isAfter(ahora)) {
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
                            // Si es hoy, solo mostrar horarios futuros
                            if (!esHoy || horaActual.isAfter(ahora)) {
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

        // Regla: el paciente solo puede cancelar hasta 3 horas antes del turno
        LocalDateTime fechaHoraTurno = LocalDateTime.of(turno.getFecha(), turno.getHora());
        LocalDateTime limiteCancelacion = fechaHoraTurno.minusHours(3);
        if (LocalDateTime.now().isAfter(limiteCancelacion)) {
            throw new RuntimeException("Solo se puede cancelar hasta 3 horas antes del turno");
        }

        return cancelarTurnoInterno(turno, request.getMotivo());
    }

    @Transactional
    public TurnoResponse cancelarTurnoPorOdontologo(Integer idOdontologo, CancelarTurnoRequest request) {
        Turno turno = turnoRepository.findById(request.getIdTurno())
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        if (turno.getOdontologo() == null || turno.getOdontologo().getIdOdontologo() == null) {
            throw new RuntimeException("Odontólogo del turno no encontrado");
        }
        if (!turno.getOdontologo().getIdOdontologo().equals(idOdontologo)) {
            throw new RuntimeException("El turno no pertenece al odontólogo");
        }

        TurnoResponse response = cancelarTurnoInterno(turno, request.getMotivo());
        notificarCancelacionPorOdontologoPostCommit(turno);
        return response;
    }

    private void notificarCancelacionPorOdontologoPostCommit(Turno turno) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            notificarCancelacionPorOdontologo(turno);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                notificarCancelacionPorOdontologo(turno);
            }
        });
    }

    private void notificarCancelacionPorOdontologo(Turno turno) {
        try {
            if (turno == null || turno.getPaciente() == null || turno.getPaciente().getUsuario() == null) {
                return;
            }

            String token = turno.getPaciente().getUsuario().getFcmToken();
            if (token == null || token.isBlank()) {
                return;
            }

            String fecha = turno.getFecha() != null ? turno.getFecha().format(FECHA_FMT) : "";
            String hora = turno.getHora() != null ? turno.getHora().format(HORA_FMT) : "";
            String title = "Turno cancelado";
            String body = "Tu turno del " + fecha + " a las " + hora + " fue cancelado por el odontólogo.";

            HashMap<String, String> data = new HashMap<>();
            if (turno.getIdTurno() != null) {
                data.put("turnoId", turno.getIdTurno().toString());
            }
            data.put("estado", "CANCELADO");

            fcmNotificationService.sendToToken(token, title, body, data);
        } catch (Exception e) {
            // No bloqueamos la cancelación si falla FCM
            log.warn("No se pudo enviar notificación FCM para turno cancelado (idTurno={})",
                    turno != null ? turno.getIdTurno() : null, e);
        }
    }

    private TurnoResponse cancelarTurnoInterno(Turno turno, String motivo) {
        // Verificar que el turno esté en estado PROGRAMADO
        if (!"PROGRAMADO".equals(turno.getEstadoTurno().getNombre())) {
            throw new RuntimeException("Solo se pueden cancelar turnos programados");
        }

        // Buscar estado CANCELADO
        EstadoTurno estadoCancelado = estadoTurnoRepository.findByNombre("CANCELADO")
                .orElseThrow(() -> new RuntimeException("Estado CANCELADO no encontrado"));

        turno.setEstadoTurno(estadoCancelado);
        turno.setNotasCancelacion(motivo);

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
