package com.gonzalo.labo6final.services;

import com.gonzalo.labo6final.DTO.HorarioLaboralRequest;
import com.gonzalo.labo6final.DTO.HorarioLaboralResponse;
import com.gonzalo.labo6final.models.HorarioLaboral;
import com.gonzalo.labo6final.models.Odontologo;
import com.gonzalo.labo6final.repositories.HorarioLaboralRepository;
import com.gonzalo.labo6final.repositories.OdontologoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HorarioLaboralService {

    private final HorarioLaboralRepository horarioLaboralRepository;
    private final OdontologoRepository odontologoRepository;

    @Transactional
    public HorarioLaboralResponse crearHorario(HorarioLaboralRequest request) {
        // Validar que el odontólogo existe
        Odontologo odontologo = odontologoRepository.findById(request.getIdOdontologo())
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        // Validar que no exista ya un horario para ese día
        if (horarioLaboralRepository.existsByOdontologoIdOdontologoAndDiaSemanaAndActivoTrue(
                request.getIdOdontologo(), request.getDiaSemana())) {
            throw new RuntimeException("Ya existe un horario activo para este día");
        }

        // Validar horarios
        validarHorarios(request);

        HorarioLaboral horario = new HorarioLaboral();
        horario.setOdontologo(odontologo);
        horario.setDiaSemana(request.getDiaSemana());
        horario.setHoraInicio(request.getHoraInicio());
        horario.setHoraFin(request.getHoraFin());
        horario.setEsDobleTurno(request.getEsDobleTurno() != null && request.getEsDobleTurno());

        if (horario.getEsDobleTurno()) {
            if (request.getHoraInicioTurno2() == null || request.getHoraFinTurno2() == null) {
                throw new RuntimeException("Debe especificar horarios del segundo turno");
            }
            horario.setHoraInicioTurno2(request.getHoraInicioTurno2());
            horario.setHoraFinTurno2(request.getHoraFinTurno2());
        }

        horario.setActivo(true);

        horario = horarioLaboralRepository.save(horario);
        return convertirAResponse(horario);
    }

    @Transactional
    public HorarioLaboralResponse actualizarHorario(Integer idHorario, HorarioLaboralRequest request) {
        HorarioLaboral horario = horarioLaboralRepository.findById(idHorario)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        // Validar horarios
        validarHorarios(request);

        horario.setHoraInicio(request.getHoraInicio());
        horario.setHoraFin(request.getHoraFin());
        horario.setEsDobleTurno(request.getEsDobleTurno() != null && request.getEsDobleTurno());

        if (horario.getEsDobleTurno()) {
            if (request.getHoraInicioTurno2() == null || request.getHoraFinTurno2() == null) {
                throw new RuntimeException("Debe especificar horarios del segundo turno");
            }
            horario.setHoraInicioTurno2(request.getHoraInicioTurno2());
            horario.setHoraFinTurno2(request.getHoraFinTurno2());
        } else {
            horario.setHoraInicioTurno2(null);
            horario.setHoraFinTurno2(null);
        }

        horario = horarioLaboralRepository.save(horario);
        return convertirAResponse(horario);
    }

    @Transactional
    public void eliminarHorario(Integer idHorario) {
        HorarioLaboral horario = horarioLaboralRepository.findById(idHorario)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        horario.setActivo(false);
        horarioLaboralRepository.save(horario);
    }

    public List<HorarioLaboralResponse> obtenerHorariosPorOdontologo(Integer idOdontologo) {
        return horarioLaboralRepository.findByOdontologoIdOdontologoAndActivoTrue(idOdontologo)
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public HorarioLaboralResponse obtenerPorId(Integer idHorario) {
        HorarioLaboral horario = horarioLaboralRepository.findById(idHorario)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
        return convertirAResponse(horario);
    }

    public boolean odontologoTrabaja(Integer idOdontologo, DayOfWeek diaSemana, LocalTime hora) {
        return horarioLaboralRepository.findHorarioActivo(idOdontologo, diaSemana)
                .map(horario -> {
                    // Verificar turno 1
                    boolean enTurno1 = !hora.isBefore(horario.getHoraInicio()) &&
                            hora.isBefore(horario.getHoraFin());

                    // Verificar turno 2 si existe
                    boolean enTurno2 = false;
                    if (horario.getEsDobleTurno() && horario.getHoraInicioTurno2() != null) {
                        enTurno2 = !hora.isBefore(horario.getHoraInicioTurno2()) &&
                                hora.isBefore(horario.getHoraFinTurno2());
                    }

                    return enTurno1 || enTurno2;
                })
                .orElse(false);
    }

    private void validarHorarios(HorarioLaboralRequest request) {
        if (request.getHoraInicio().isAfter(request.getHoraFin()) ||
                request.getHoraInicio().equals(request.getHoraFin())) {
            throw new RuntimeException("La hora de inicio debe ser anterior a la hora de fin");
        }

        if (Boolean.TRUE.equals(request.getEsDobleTurno())) {
            if (request.getHoraInicioTurno2() == null || request.getHoraFinTurno2() == null) {
                throw new RuntimeException("Debe especificar ambos horarios del segundo turno");
            }

            if (request.getHoraInicioTurno2().isAfter(request.getHoraFinTurno2()) ||
                    request.getHoraInicioTurno2().equals(request.getHoraFinTurno2())) {
                throw new RuntimeException("La hora de inicio del turno 2 debe ser anterior a la hora de fin");
            }

            // Validar que no se solapen los turnos
            if (!request.getHoraFin().isBefore(request.getHoraInicioTurno2()) &&
                    !request.getHoraFin().equals(request.getHoraInicioTurno2())) {
                throw new RuntimeException(
                        "Los turnos no pueden solaparse. El turno 2 debe iniciar después del turno 1");
            }
        }
    }

    private HorarioLaboralResponse convertirAResponse(HorarioLaboral horario) {
        HorarioLaboralResponse response = new HorarioLaboralResponse();
        response.setIdHorario(horario.getIdHorario());
        response.setIdOdontologo(horario.getOdontologo().getIdOdontologo());
        response.setNombreOdontologo(horario.getOdontologo().getNombre());
        response.setApellidoOdontologo(horario.getOdontologo().getApellido());
        response.setDiaSemana(horario.getDiaSemana());
        response.setDiaSemanaTexto(traducirDia(horario.getDiaSemana()));
        response.setHoraInicio(horario.getHoraInicio());
        response.setHoraFin(horario.getHoraFin());
        response.setEsDobleTurno(horario.getEsDobleTurno());
        response.setHoraInicioTurno2(horario.getHoraInicioTurno2());
        response.setHoraFinTurno2(horario.getHoraFinTurno2());
        response.setActivo(horario.getActivo());
        return response;
    }

    private String traducirDia(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miércoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }
}
