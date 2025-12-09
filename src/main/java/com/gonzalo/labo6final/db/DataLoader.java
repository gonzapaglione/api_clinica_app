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
        private HorarioLaboralRepository horarioLaboralRepository;

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
                crearHorariosLaborales(odontologos);
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
                                                        .filter(e -> e.getOdontologo()
                                                                        .equals(odontologos.odontologo1()))
                                                        .toList());
                        odontologos.odontologo2().getEspecialidades().addAll(
                                        especialidadesAsignadas.stream()
                                                        .filter(e -> e.getOdontologo()
                                                                        .equals(odontologos.odontologo2()))
                                                        .toList());
                        odontologos.odontologo3().getEspecialidades().addAll(
                                        especialidadesAsignadas.stream()
                                                        .filter(e -> e.getOdontologo()
                                                                        .equals(odontologos.odontologo3()))
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
                        asignaciones.add(pos1);
                }

                if (pami != null) {
                        PacienteObraSocial pos2 = new PacienteObraSocial();
                        pos2.setIdPaciente(pacientes.paciente2().getIdPaciente());
                        pos2.setIdObraSocial(pami.getIdObraSocial());
                        pos2.setPaciente(pacientes.paciente2());
                        pos2.setObraSocial(pami);
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
                        asignaciones.add(pos4);
                }

                if (!asignaciones.isEmpty()) {
                        pacientes.paciente1().getObrasSociales().addAll(
                                        asignaciones.stream().filter(a -> a.getPaciente().equals(pacientes.paciente1()))
                                                        .toList());
                        pacientes.paciente2().getObrasSociales().addAll(
                                        asignaciones.stream().filter(a -> a.getPaciente().equals(pacientes.paciente2()))
                                                        .toList());
                        pacientes.paciente3().getObrasSociales().addAll(
                                        asignaciones.stream().filter(a -> a.getPaciente().equals(pacientes.paciente3()))
                                                        .toList());
                        pacientes.paciente4().getObrasSociales().addAll(
                                        asignaciones.stream().filter(a -> a.getPaciente().equals(pacientes.paciente4()))
                                                        .toList());

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

                MotivoConsulta revision = motivoConsultaRepository.findByDescripcion("Revisión periódica")
                                .orElseThrow();
                MotivoConsulta limpieza = motivoConsultaRepository.findByDescripcion("Limpieza dental").orElseThrow();
                MotivoConsulta dolor = motivoConsultaRepository.findByDescripcion("Dolor de muela").orElseThrow();
                MotivoConsulta caries = motivoConsultaRepository.findByDescripcion("Caries").orElseThrow();
                MotivoConsulta blanqueamiento = motivoConsultaRepository.findByDescripcion("Blanqueamiento")
                                .orElseThrow();
                MotivoConsulta extraccion = motivoConsultaRepository.findByDescripcion("Extracción").orElseThrow();

                ObraSocial osde = obraSocialRepository.findByNombre("OSDE").orElse(null);
                ObraSocial pami = obraSocialRepository.findByNombre("PAMI").orElse(null);
                ObraSocial particular = obraSocialRepository.findByNombre("PARTICULAR").orElse(null);
                ObraSocial galeno = obraSocialRepository.findByNombre("GALENO").orElse(null);

                List<Turno> turnos = new ArrayList<>();
                List<Turno> turnosRealizados = new ArrayList<>();

                // PACIENTE 1 - 2 turnos realizados, 1 cancelado
                Turno t1p1 = new Turno(null, LocalDate.now().minusDays(30), LocalTime.of(9, 0),
                                pacientes.paciente1(), odontologos.odontologo1(), revision, realizado, osde, null,
                                null);
                turnos.add(t1p1);
                turnosRealizados.add(t1p1);

                Turno t2p1 = new Turno(null, LocalDate.now().minusDays(15), LocalTime.of(14, 30),
                                pacientes.paciente1(), odontologos.odontologo2(), limpieza, realizado, osde, null,
                                null);
                turnos.add(t2p1);
                turnosRealizados.add(t2p1);

                turnos.add(new Turno(null, LocalDate.now().minusDays(5), LocalTime.of(10, 0),
                                pacientes.paciente1(), odontologos.odontologo1(), dolor, cancelado, osde, null,
                                "Paciente canceló por motivos personales"));

                // PACIENTE 2 - 3 turnos realizados
                Turno t1p2 = new Turno(null, LocalDate.now().minusDays(45), LocalTime.of(11, 0),
                                pacientes.paciente2(), odontologos.odontologo2(), limpieza, realizado, pami, null,
                                null);
                turnos.add(t1p2);
                turnosRealizados.add(t1p2);

                Turno t2p2 = new Turno(null, LocalDate.now().minusDays(25), LocalTime.of(16, 0),
                                pacientes.paciente2(), odontologos.odontologo1(), caries, realizado, pami, null, null);
                turnos.add(t2p2);
                turnosRealizados.add(t2p2);

                Turno t3p2 = new Turno(null, LocalDate.now().minusDays(10), LocalTime.of(10, 30),
                                pacientes.paciente2(), odontologos.odontologo2(), revision, realizado, pami, null,
                                null);
                turnos.add(t3p2);
                turnosRealizados.add(t3p2);

                // PACIENTE 3 - 2 turnos realizados, 1 ausente
                Turno t1p3 = new Turno(null, LocalDate.now().minusDays(40), LocalTime.of(15, 0),
                                pacientes.paciente3(), odontologos.odontologo3(), revision, realizado, particular, null,
                                null);
                turnos.add(t1p3);
                turnosRealizados.add(t1p3);

                Turno t2p3 = new Turno(null, LocalDate.now().minusDays(20), LocalTime.of(9, 30),
                                pacientes.paciente3(), odontologos.odontologo1(), blanqueamiento, realizado, particular,
                                null, null);
                turnos.add(t2p3);
                turnosRealizados.add(t2p3);

                turnos.add(new Turno(null, LocalDate.now().minusDays(3), LocalTime.of(14, 0),
                                pacientes.paciente3(), odontologos.odontologo2(), limpieza, ausente, particular, null,
                                null));

                // PACIENTE 4 - 3 turnos realizados
                Turno t1p4 = new Turno(null, LocalDate.now().minusDays(50), LocalTime.of(10, 0),
                                pacientes.paciente4(), odontologos.odontologo2(), dolor, realizado, galeno, null, null);
                turnos.add(t1p4);
                turnosRealizados.add(t1p4);

                Turno t2p4 = new Turno(null, LocalDate.now().minusDays(35), LocalTime.of(13, 0),
                                pacientes.paciente4(), odontologos.odontologo1(), extraccion, realizado, galeno, null,
                                null);
                turnos.add(t2p4);
                turnosRealizados.add(t2p4);

                Turno t3p4 = new Turno(null, LocalDate.now().minusDays(18), LocalTime.of(11, 30),
                                pacientes.paciente4(), odontologos.odontologo3(), revision, realizado, galeno, null,
                                null);
                turnos.add(t3p4);
                turnosRealizados.add(t3p4);

                // PACIENTE 5 - 2 turnos cancelados, 1 realizado
                Turno t1p5 = new Turno(null, LocalDate.now().minusDays(28), LocalTime.of(16, 30),
                                pacientes.paciente5(), odontologos.odontologo1(), limpieza, realizado, particular, null,
                                null);
                turnos.add(t1p5);
                turnosRealizados.add(t1p5);

                turnos.add(new Turno(null, LocalDate.now().minusDays(12), LocalTime.of(9, 0),
                                pacientes.paciente5(), odontologos.odontologo2(), caries, cancelado, particular, null,
                                "Paciente enfermo, reprogramar"));

                turnos.add(new Turno(null, LocalDate.now().minusDays(7), LocalTime.of(15, 30),
                                pacientes.paciente5(), odontologos.odontologo3(), revision, cancelado, particular, null,
                                "Cambio de horario solicitado"));

                // Algunos turnos futuros programados
                turnos.add(new Turno(null, LocalDate.now().plusDays(3), LocalTime.of(10, 0),
                                pacientes.paciente1(), odontologos.odontologo1(), caries, programado, osde, null,
                                null));

                turnos.add(new Turno(null, LocalDate.now().plusDays(7), LocalTime.of(14, 0),
                                pacientes.paciente3(), odontologos.odontologo2(), limpieza, programado, particular,
                                null, null));

                turnoRepository.saveAll(turnos);
                System.out.println("   - Turnos demo precargados");

                // Crear historias clínicas para turnos REALIZADOS
                crearHistoriasClinicas(turnosRealizados);
        }

        private void crearHistoriasClinicas(List<Turno> turnosRealizados) {
                List<HistoriaClinica> historias = new ArrayList<>();

                String[] diagnosticos = {
                                "Control general sin complicaciones",
                                "Presencia de caries en molar superior",
                                "Encías en buen estado",
                                "Sensibilidad dental detectada",
                                "Placa bacteriana moderada",
                                "Paciente en buen estado general",
                                "Requiere seguimiento en próxima visita",
                                "Evolución favorable del tratamiento"
                };

                String[] tratamientos = {
                                "Limpieza dental profunda realizada",
                                "Obturación de caries con composite",
                                "Aplicación de flúor",
                                "Extracción de pieza dental",
                                "Tratamiento de blanqueamiento iniciado",
                                "Sellado de fisuras preventivo",
                                "Pulido y limpieza profesional",
                                "Ajuste de oclusión"
                };

                String[] observaciones = {
                                "Paciente en buen estado general",
                                "Se recomienda control en 6 meses",
                                "Mantener buena higiene dental",
                                "Evitar alimentos duros durante 48hs",
                                "Programar próxima consulta en 3 meses",
                                "Continuar con cepillado frecuente",
                                "Paciente colaborador y atento",
                                "Se entregaron instrucciones de cuidado"
                };

                for (int i = 0; i < turnosRealizados.size(); i++) {
                        HistoriaClinica hc = new HistoriaClinica(
                                        null,
                                        turnosRealizados.get(i),
                                        diagnosticos[i % diagnosticos.length],
                                        tratamientos[i % tratamientos.length],
                                        observaciones[i % observaciones.length]);
                        historias.add(hc);
                }

                historiaClinicaRepository.saveAll(historias);
                System.out.println("   - Historias clínicas demo precargadas");
        }

        private void crearHorariosLaborales(OdontologosIniciales odontologos) {
                if (odontologos == null)
                        return;

                List<HorarioLaboral> horarios = new ArrayList<>();

                // ODONTÓLOGO 1 - Diego Ruiz
                // Lunes a Viernes: 8:00-12:00 y 14:00-18:00 (doble turno)
                horarios.add(new HorarioLaboral(null, odontologos.odontologo1(),
                                java.time.DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0),
                                true, LocalTime.of(14, 0), LocalTime.of(18, 0), true));

                horarios.add(new HorarioLaboral(null, odontologos.odontologo1(),
                                java.time.DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(12, 0),
                                true, LocalTime.of(14, 0), LocalTime.of(18, 0), true));

                horarios.add(new HorarioLaboral(null, odontologos.odontologo1(),
                                java.time.DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(12, 0),
                                true, LocalTime.of(14, 0), LocalTime.of(18, 0), true));

                horarios.add(new HorarioLaboral(null, odontologos.odontologo1(),
                                java.time.DayOfWeek.THURSDAY, LocalTime.of(8, 0), LocalTime.of(12, 0),
                                true, LocalTime.of(14, 0), LocalTime.of(18, 0), true));

                horarios.add(new HorarioLaboral(null, odontologos.odontologo1(),
                                java.time.DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(12, 0),
                                true, LocalTime.of(14, 0), LocalTime.of(18, 0), true));

                // ODONTÓLOGO 2 - Laura Sosa
                // Lunes, Miércoles y Viernes: 9:00-17:00 (turno corrido)
                horarios.add(new HorarioLaboral(null, odontologos.odontologo2(),
                                java.time.DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0),
                                false, null, null, true));

                horarios.add(new HorarioLaboral(null, odontologos.odontologo2(),
                                java.time.DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(17, 0),
                                false, null, null, true));

                horarios.add(new HorarioLaboral(null, odontologos.odontologo2(),
                                java.time.DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(17, 0),
                                false, null, null, true));

                // ODONTÓLOGO 3 - Ana Gomez
                // Martes y Jueves: 10:00-14:00 y 15:00-19:00 (doble turno)
                // Sábado: 8:00-13:00 (turno simple)
                horarios.add(new HorarioLaboral(null, odontologos.odontologo3(),
                                java.time.DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(14, 0),
                                true, LocalTime.of(15, 0), LocalTime.of(19, 0), true));

                horarios.add(new HorarioLaboral(null, odontologos.odontologo3(),
                                java.time.DayOfWeek.THURSDAY, LocalTime.of(10, 0), LocalTime.of(14, 0),
                                true, LocalTime.of(15, 0), LocalTime.of(19, 0), true));

                horarios.add(new HorarioLaboral(null, odontologos.odontologo3(),
                                java.time.DayOfWeek.SATURDAY, LocalTime.of(8, 0), LocalTime.of(13, 0),
                                false, null, null, true));

                horarioLaboralRepository.saveAll(horarios);
                System.out.println("   - Horarios laborales precargados");
                System.out.println("     * Odontólogo 1: Lunes a Viernes 8-12 y 14-18");
                System.out.println("     * Odontólogo 2: Lunes, Miércoles y Viernes 9-17");
                System.out.println("     * Odontólogo 3: Martes y Jueves 10-14 y 15-19, Sábado 8-13");
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
