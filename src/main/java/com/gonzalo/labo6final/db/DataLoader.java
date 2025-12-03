package com.gonzalo.labo6final.db;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.gonzalo.labo6final.models.*;
import com.gonzalo.labo6final.repositories.*;

import jakarta.transaction.Transactional;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private OdontologoRepository odontologoRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private ObraSocialRepository obraSocialRepository;

    @Autowired
    private MotivoConsultaRepository motivoConsultaRepository;

    @Autowired
    private EstadoTurnoRepository estadoTurnoRepository;

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private HistoriaClinicaRepository historiaClinicaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println(">>> Iniciando precarga de datos...");

        if (datosPrecargados()) {
            System.out.println(">>> Datos de prueba ya existentes. Saltando precarga completa");
            return;
        }

        precargarRoles();
        precargarEstadosTurno();
        precargarEspecialidades();
        precargarObrasSociales();
        precargarMotivosConsulta();

        UsuariosIniciales usuarios = crearUsuarios();
        PacientesIniciales pacientes = crearPacientes(usuarios);
        OdontologosIniciales odontologos = crearOdontologos(usuarios);

        asignarEspecialidades(odontologos);
        asignarObrasSocialesPacientes(pacientes);
        crearTurnosDemo(pacientes, odontologos);

        System.out.println(">>> Precarga completada exitosamente");
    }

    private void precargarRoles() {
        if (rolRepository.count() == 0) {
            rolRepository.saveAll(List.of(
                    new Rol(null, "PACIENTE"),
                    new Rol(null, "ODONTOLOGO"),
                    new Rol(null, "ADMIN")));
            System.out.println("   - Roles precargados");
        }
    }

    private void precargarEstadosTurno() {
        if (estadoTurnoRepository.count() == 0) {
            estadoTurnoRepository.saveAll(List.of(
                    new EstadoTurno(null, "PROGRAMADO"),
                    new EstadoTurno(null, "CANCELADO"),
                    new EstadoTurno(null, "REALIZADO"),
                    new EstadoTurno(null, "AUSENTE")));
            System.out.println("   - Estados de turno precargados");
        }
    }

    private void precargarEspecialidades() {
        if (especialidadRepository.count() == 0) {
            especialidadRepository.saveAll(List.of(
                    new Especialidad(null, "Ortodoncia"),
                    new Especialidad(null, "Endodoncia"),
                    new Especialidad(null, "Periodoncia"),
                    new Especialidad(null, "Implantología"),
                    new Especialidad(null, "Odontopediatría"),
                    new Especialidad(null, "Cirugía Oral")));
            System.out.println("   - Especialidades precargadas");
        }
    }

    private void precargarObrasSociales() {
        if (obraSocialRepository.count() == 0) {
            obraSocialRepository.saveAll(List.of(
                    new ObraSocial(null, "PARTICULAR"),
                    new ObraSocial(null, "OSDE"),
                    new ObraSocial(null, "PAMI"),
                    new ObraSocial(null, "GALENO"),
                    new ObraSocial(null, "SWISS MEDICAL"),
                    new ObraSocial(null, "OSPE"),
                    new ObraSocial(null, "IOSEP")));
            System.out.println("   - Obras sociales precargadas");
        }
    }

    private void precargarMotivosConsulta() {
        if (motivoConsultaRepository.count() == 0) {
            motivoConsultaRepository.saveAll(List.of(
                    new MotivoConsulta(null, "Dolor de muela"),
                    new MotivoConsulta(null, "Limpieza dental"),
                    new MotivoConsulta(null, "Revisión periódica"),
                    new MotivoConsulta(null, "Caries"),
                    new MotivoConsulta(null, "Blanqueamiento"),
                    new MotivoConsulta(null, "Ortodoncia"),
                    new MotivoConsulta(null, "Implantes"),
                    new MotivoConsulta(null, "Extracción"),
                    new MotivoConsulta(null, "Fractura dental"),
                    new MotivoConsulta(null, "Consulta general")));
            System.out.println("   - Motivos de consulta precargados");
        }
    }

    private UsuariosIniciales crearUsuarios() {
        if (usuarioRepository.count() > 0) {
            System.out.println("   - Usuarios ya existentes. Saltando precarga de usuarios.");
            return null;
        }

        Rol rolPaciente = rolRepository.findByNombre("PACIENTE").orElseThrow();
        Rol rolOdontologo = rolRepository.findByNombre("ODONTOLOGO").orElseThrow();
        Rol rolAdmin = rolRepository.findByNombre("ADMIN").orElseThrow();

        // Usuarios Pacientes
        Usuario usuarioPac1 = new Usuario(null, "paciente1@gmail.com",
                passwordEncoder.encode("paciente"), rolPaciente, null);
        Usuario usuarioPac2 = new Usuario(null, "paciente2@gmail.com",
                passwordEncoder.encode("paciente"), rolPaciente, null);
        Usuario usuarioPac3 = new Usuario(null, "paciente3@gmail.com",
                passwordEncoder.encode("paciente"), rolPaciente, null);
        Usuario usuarioPac4 = new Usuario(null, "paciente4@gmail.com",
                passwordEncoder.encode("paciente"), rolPaciente, null);
        Usuario usuarioPac5 = new Usuario(null, "paciente5@gmail.com",
                passwordEncoder.encode("paciente"), rolPaciente, null);

        // Usuarios Odontólogos
        Usuario usuarioOd1 = new Usuario(null, "odontologo1@gmail.com",
                passwordEncoder.encode("odontologo"), rolOdontologo, null);
        Usuario usuarioOd2 = new Usuario(null, "odontologo2@gmail.com",
                passwordEncoder.encode("odontologo"), rolOdontologo, null);
        Usuario usuarioOd3 = new Usuario(null, "odontologo3@gmail.com",
                passwordEncoder.encode("odontologo"), rolOdontologo, null);

        // Usuario Admin
        Usuario usuarioAdmin = new Usuario(null, "admin@gmail.com",
                passwordEncoder.encode("administrador"), rolAdmin, null);

        usuarioRepository.saveAll(List.of(
                usuarioPac1, usuarioPac2, usuarioPac3, usuarioPac4, usuarioPac5,
                usuarioOd1, usuarioOd2, usuarioOd3, usuarioAdmin));

        System.out.println("   - Usuarios precargados");
        return new UsuariosIniciales(usuarioPac1, usuarioPac2, usuarioPac3, usuarioPac4, usuarioPac5,
                usuarioOd1, usuarioOd2, usuarioOd3, usuarioAdmin);
    }

    private PacientesIniciales crearPacientes(UsuariosIniciales usuarios) {
        if (usuarios == null)
            return null;

        Paciente pac1 = new Paciente(null, usuarios.usuarioPac1(), "30111222", "Gonzalo", "Lopez Paglione",
                "111-222-333", "Av. Siempreviva 123", new ArrayList<>());
        Paciente pac2 = new Paciente(null, usuarios.usuarioPac2(), "30122333", "Lourdes", "Guerrieri Viaña",
                "111-333-444", "Calle 9 456", new ArrayList<>());
        Paciente pac3 = new Paciente(null, usuarios.usuarioPac3(), "30133444", "Lautaro", "Mercado",
                "111-444-555", "Bv. San Martín 12", new ArrayList<>());
        Paciente pac4 = new Paciente(null, usuarios.usuarioPac4(), "30144555", "María", "Sol",
                "111-555-666", "Calle Falsa 123", new ArrayList<>());
        Paciente pac5 = new Paciente(null, usuarios.usuarioPac5(), "30155666", "Juan", "Perez",
                "111-666-777", "Av. Colón 456", new ArrayList<>());

        pacienteRepository.saveAll(List.of(pac1, pac2, pac3, pac4, pac5));
        System.out.println("   - Pacientes precargados");

        return new PacientesIniciales(pac1, pac2, pac3, pac4, pac5);
    }

    private OdontologosIniciales crearOdontologos(UsuariosIniciales usuarios) {
        if (usuarios == null)
            return null;

        Odontologo od1 = new Odontologo(null, usuarios.usuarioOd1(), "Diego", "Ruiz",
                new ArrayList<>());
        Odontologo od2 = new Odontologo(null, usuarios.usuarioOd2(), "Laura", "Sosa",
                new ArrayList<>());
        Odontologo od3 = new Odontologo(null, usuarios.usuarioOd3(), "Ana", "Gomez",
                new ArrayList<>());

        odontologoRepository.saveAll(List.of(od1, od2, od3));
        System.out.println("   - Odontólogos precargados");

        return new OdontologosIniciales(od1, od2, od3);
    }

    private void asignarEspecialidades(OdontologosIniciales odontologos) {
        if (odontologos == null)
            return;

        Especialidad ortodoncia = especialidadRepository.findByNombre("Ortodoncia").orElse(null);
        Especialidad endodoncia = especialidadRepository.findByNombre("Endodoncia").orElse(null);
        Especialidad periodoncia = especialidadRepository.findByNombre("Periodoncia").orElse(null);
        Especialidad implantologia = especialidadRepository.findByNombre("Implantología").orElse(null);

        List<OdontologoEspecialidad> especialidadesAsignadas = new ArrayList<>();

        if (ortodoncia != null) {
            OdontologoEspecialidad oe1 = new OdontologoEspecialidad();
            oe1.setIdOdontologo(odontologos.odontologo1().getIdOdontologo());
            oe1.setIdEspecialidad(ortodoncia.getIdEspecialidad());
            oe1.setOdontologo(odontologos.odontologo1());
            oe1.setEspecialidad(ortodoncia);
            especialidadesAsignadas.add(oe1);

            OdontologoEspecialidad oe2 = new OdontologoEspecialidad();
            oe2.setIdOdontologo(odontologos.odontologo2().getIdOdontologo());
            oe2.setIdEspecialidad(ortodoncia.getIdEspecialidad());
            oe2.setOdontologo(odontologos.odontologo2());
            oe2.setEspecialidad(ortodoncia);
            especialidadesAsignadas.add(oe2);
        }

        if (endodoncia != null) {
            OdontologoEspecialidad oe = new OdontologoEspecialidad();
            oe.setIdOdontologo(odontologos.odontologo2().getIdOdontologo());
            oe.setIdEspecialidad(endodoncia.getIdEspecialidad());
            oe.setOdontologo(odontologos.odontologo2());
            oe.setEspecialidad(endodoncia);
            especialidadesAsignadas.add(oe);
        }

        if (periodoncia != null) {
            OdontologoEspecialidad oe = new OdontologoEspecialidad();
            oe.setIdOdontologo(odontologos.odontologo3().getIdOdontologo());
            oe.setIdEspecialidad(periodoncia.getIdEspecialidad());
            oe.setOdontologo(odontologos.odontologo3());
            oe.setEspecialidad(periodoncia);
            especialidadesAsignadas.add(oe);
        }

        if (implantologia != null) {
            OdontologoEspecialidad oe = new OdontologoEspecialidad();
            oe.setIdOdontologo(odontologos.odontologo1().getIdOdontologo());
            oe.setIdEspecialidad(implantologia.getIdEspecialidad());
            oe.setOdontologo(odontologos.odontologo1());
            oe.setEspecialidad(implantologia);
            especialidadesAsignadas.add(oe);
        }

        if (!especialidadesAsignadas.isEmpty()) {
            // Guardar manualmente las especialidades (sin repositorio de tabla intermedia)
            odontologos.odontologo1().getEspecialidades().addAll(
                    especialidadesAsignadas.stream()
                            .filter(e -> e.getOdontologo().equals(odontologos.odontologo1()))
                            .toList());
            odontologos.odontologo2().getEspecialidades().addAll(
                    especialidadesAsignadas.stream()
                            .filter(e -> e.getOdontologo().equals(odontologos.odontologo2()))
                            .toList());
            odontologos.odontologo3().getEspecialidades().addAll(
                    especialidadesAsignadas.stream()
                            .filter(e -> e.getOdontologo().equals(odontologos.odontologo3()))
                            .toList());

            odontologoRepository.saveAll(List.of(
                    odontologos.odontologo1(),
                    odontologos.odontologo2(),
                    odontologos.odontologo3()));

            System.out.println("   - Especialidades asignadas a odontólogos");
        }
    }

    private void asignarObrasSocialesPacientes(PacientesIniciales pacientes) {
        if (pacientes == null)
            return;

        ObraSocial osde = obraSocialRepository.findByNombre("OSDE").orElse(null);
        ObraSocial pami = obraSocialRepository.findByNombre("PAMI").orElse(null);
        ObraSocial particular = obraSocialRepository.findByNombre("PARTICULAR").orElse(null);
        ObraSocial galeno = obraSocialRepository.findByNombre("GALENO").orElse(null);

        List<PacienteObraSocial> asignaciones = new ArrayList<>();

        if (osde != null) {
            PacienteObraSocial pos1 = new PacienteObraSocial();
            pos1.setIdPaciente(pacientes.paciente1().getIdPaciente());
            pos1.setIdObraSocial(osde.getIdObraSocial());
            pos1.setPaciente(pacientes.paciente1());
            pos1.setObraSocial(osde);
            pos1.setNroAfiliado("OSDE-111222");
            asignaciones.add(pos1);
        }

        if (pami != null) {
            PacienteObraSocial pos2 = new PacienteObraSocial();
            pos2.setIdPaciente(pacientes.paciente2().getIdPaciente());
            pos2.setIdObraSocial(pami.getIdObraSocial());
            pos2.setPaciente(pacientes.paciente2());
            pos2.setObraSocial(pami);
            pos2.setNroAfiliado("PAMI-122333");
            asignaciones.add(pos2);
        }

        if (particular != null) {
            PacienteObraSocial pos3 = new PacienteObraSocial();
            pos3.setIdPaciente(pacientes.paciente3().getIdPaciente());
            pos3.setIdObraSocial(particular.getIdObraSocial());
            pos3.setPaciente(pacientes.paciente3());
            pos3.setObraSocial(particular);
            asignaciones.add(pos3);
        }

        if (galeno != null) {
            PacienteObraSocial pos4 = new PacienteObraSocial();
            pos4.setIdPaciente(pacientes.paciente4().getIdPaciente());
            pos4.setIdObraSocial(galeno.getIdObraSocial());
            pos4.setPaciente(pacientes.paciente4());
            pos4.setObraSocial(galeno);
            pos4.setNroAfiliado("GAL-144555");
            asignaciones.add(pos4);
        }

        if (!asignaciones.isEmpty()) {
            pacientes.paciente1().getObrasSociales().addAll(
                    asignaciones.stream().filter(a -> a.getPaciente().equals(pacientes.paciente1())).toList());
            pacientes.paciente2().getObrasSociales().addAll(
                    asignaciones.stream().filter(a -> a.getPaciente().equals(pacientes.paciente2())).toList());
            pacientes.paciente3().getObrasSociales().addAll(
                    asignaciones.stream().filter(a -> a.getPaciente().equals(pacientes.paciente3())).toList());
            pacientes.paciente4().getObrasSociales().addAll(
                    asignaciones.stream().filter(a -> a.getPaciente().equals(pacientes.paciente4())).toList());

            pacienteRepository.saveAll(List.of(
                    pacientes.paciente1(),
                    pacientes.paciente2(),
                    pacientes.paciente3(),
                    pacientes.paciente4()));

            System.out.println("   - Obras sociales asignadas a pacientes");
        }
    }

    private void crearTurnosDemo(PacientesIniciales pacientes, OdontologosIniciales odontologos) {
        if (pacientes == null || odontologos == null)
            return;

        EstadoTurno programado = estadoTurnoRepository.findByNombre("PROGRAMADO").orElseThrow();
        EstadoTurno realizado = estadoTurnoRepository.findByNombre("REALIZADO").orElseThrow();
        EstadoTurno cancelado = estadoTurnoRepository.findByNombre("CANCELADO").orElseThrow();
        EstadoTurno ausente = estadoTurnoRepository.findByNombre("AUSENTE").orElseThrow();

        MotivoConsulta revision = motivoConsultaRepository.findByDescripcion("Revisión periódica").orElseThrow();
        MotivoConsulta limpieza = motivoConsultaRepository.findByDescripcion("Limpieza dental").orElseThrow();
        MotivoConsulta dolor = motivoConsultaRepository.findByDescripcion("Dolor de muela").orElseThrow();
        MotivoConsulta caries = motivoConsultaRepository.findByDescripcion("Caries").orElseThrow();

        ObraSocial osde = obraSocialRepository.findByNombre("OSDE").orElse(null);
        ObraSocial pami = obraSocialRepository.findByNombre("PAMI").orElse(null);
        ObraSocial particular = obraSocialRepository.findByNombre("PARTICULAR").orElse(null);

        List<Turno> turnos = new ArrayList<>();

        // Turnos pasados REALIZADOS
        Turno t1 = new Turno(null, LocalDate.now().minusDays(10), LocalTime.of(18, 0),
                pacientes.paciente1(), odontologos.odontologo1(), revision, realizado, osde, null, null);
        turnos.add(t1);

        Turno t2 = new Turno(null, LocalDate.now().minusDays(7), LocalTime.of(10, 0),
                pacientes.paciente2(), odontologos.odontologo2(), limpieza, realizado, pami, null, null);
        turnos.add(t2);

        // Turnos futuros PROGRAMADOS
        turnos.add(new Turno(null, LocalDate.now().plusDays(2), LocalTime.of(9, 0),
                pacientes.paciente1(), odontologos.odontologo2(), dolor, programado, particular, null, null));

        turnos.add(new Turno(null, LocalDate.now().plusDays(5), LocalTime.of(16, 0),
                pacientes.paciente3(), odontologos.odontologo1(), caries, programado, particular, null, null));

        turnos.add(new Turno(null, LocalDate.now().plusDays(7), LocalTime.of(11, 30),
                pacientes.paciente4(), odontologos.odontologo3(), revision, programado, osde, null, null));

        // Turno CANCELADO
        turnos.add(new Turno(null, LocalDate.now().plusDays(3), LocalTime.of(14, 0),
                pacientes.paciente5(), odontologos.odontologo1(), limpieza, cancelado, null, null,
                "Paciente solicitó cancelación"));

        // Turno AUSENTE
        turnos.add(new Turno(null, LocalDate.now().minusDays(2), LocalTime.of(15, 0),
                pacientes.paciente3(), odontologos.odontologo2(), dolor, ausente, osde, null, null));

        turnoRepository.saveAll(turnos);
        System.out.println("   - Turnos demo precargados");

        // Crear historias clínicas para turnos REALIZADOS
        crearHistoriasClinicas(t1, t2);
    }

    private void crearHistoriasClinicas(Turno turno1, Turno turno2) {
        HistoriaClinica hc1 = new HistoriaClinica(null, turno1,
                "Control general sin complicaciones",
                "Limpieza dental profunda realizada",
                "Paciente en buen estado general");

        HistoriaClinica hc2 = new HistoriaClinica(null, turno2,
                "Encías en buen estado",
                "Limpieza y aplicación de flúor",
                "Se recomienda control en 6 meses");

        historiaClinicaRepository.saveAll(List.of(hc1, hc2));
        System.out.println("   - Historias clínicas demo precargadas");
    }

    private boolean datosPrecargados() {
        return rolRepository.count() > 0
                && estadoTurnoRepository.count() > 0
                && especialidadRepository.count() > 0
                && obraSocialRepository.count() > 0
                && motivoConsultaRepository.count() > 0
                && usuarioRepository.count() > 0
                && pacienteRepository.count() > 0
                && odontologoRepository.count() > 0;
    }

    private record UsuariosIniciales(
            Usuario usuarioPac1, Usuario usuarioPac2, Usuario usuarioPac3,
            Usuario usuarioPac4, Usuario usuarioPac5,
            Usuario usuarioOd1, Usuario usuarioOd2, Usuario usuarioOd3,
            Usuario usuarioAdmin) {
    }

    private record PacientesIniciales(
            Paciente paciente1, Paciente paciente2, Paciente paciente3,
            Paciente paciente4, Paciente paciente5) {
    }

    private record OdontologosIniciales(
            Odontologo odontologo1, Odontologo odontologo2, Odontologo odontologo3) {
    }
}
