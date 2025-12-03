# API Móvil - Clínica Odontológica

## Descripción

Backend simplificado para la aplicación móvil Android de gestión de turnos odontológicos. Autenticación sin JWT, usando ID de paciente almacenado en SharedPreferences.

## URL Base

```
http://localhost:8080
```

Para emulador Android usar: `http://10.0.2.2:8080`

---

## 🔐 Autenticación

### POST /auth/login-simple

Login simplificado sin JWT.

**Request:**

```json
{
  "email": "paciente@example.com",
  "password": "password123"
}
```

**Response exitoso (200):**

```json
{
  "idPaciente": 1,
  "idPersona": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "paciente@example.com",
  "mensaje": "Login exitoso"
}
```

**Response error:**

```json
{
  "mensaje": "Usuario no encontrado" | "Contraseña incorrecta" | "Este usuario no es un paciente" | "Paciente inactivo. Contacte con administración"
}
```

---

### POST /auth/register-simple

Registro de nuevo paciente.

**Request:**

```json
{
  "nombre": "María",
  "apellido": "García",
  "dni": "12345678",
  "email": "maria@example.com",
  "password": "password123",
  "telefono": "1123456789",
  "direccion": "Calle Falsa 123",
  "coberturaSocialIds": [1, 2]
}
```

**Response exitoso (200):**

```json
{
  "idPaciente": 2,
  "idPersona": 2,
  "nombre": "María",
  "apellido": "García",
  "email": "maria@example.com",
  "mensaje": "Login exitoso"
}
```

---

## 👤 Perfil del Paciente

### GET /api/mobile/paciente/{idPaciente}

Obtener datos completos del perfil.

**Response (200):**

```json
{
  "idPaciente": 1,
  "idPersona": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "dni": "12345678",
  "email": "paciente@example.com",
  "telefono": "1123456789",
  "direccion": "Calle Falsa 123",
  "estado": "Activo",
  "coberturas": [
    {
      "id": 1,
      "nombre": "OSDE",
      "estado": "Activo"
    }
  ]
}
```

---

### PUT /api/mobile/paciente/{idPaciente}

Actualizar datos del perfil.

**Request:**

```json
{
  "nombre": "Juan Carlos",
  "apellido": "Pérez",
  "telefono": "1198765432",
  "direccion": "Nueva Dirección 456"
}
```

**Response (200):**

```json
{
  "mensaje": "Perfil actualizado exitosamente",
  "idPaciente": 1,
  "nombre": "Juan Carlos",
  "apellido": "Pérez"
}
```

---

### PUT /api/mobile/paciente/{idPaciente}/coberturas

Actualizar coberturas sociales.

**Request:**

```json
[1, 3, 5]
```

**Response (200):**

```json
{
  "mensaje": "Coberturas actualizadas exitosamente",
  "coberturas": [
    {
      "id": 1,
      "nombre": "OSDE"
    },
    {
      "id": 3,
      "nombre": "Swiss Medical"
    }
  ]
}
```

---

### PUT /api/mobile/paciente/{idPaciente}/cambiar-password

Cambiar contraseña.

**Request:**

```json
{
  "passwordActual": "password123",
  "passwordNueva": "newPassword456"
}
```

**Response (200):**

```json
{
  "mensaje": "Contraseña cambiada exitosamente"
}
```

---

## 📅 Gestión de Turnos

### GET /api/mobile/turnos/paciente/{idPaciente}

Obtener turnos del paciente.

**Query params:**

- `tipo`: `proximos` | `pasados` | `todos` (default: `todos`)

**Response (200):**

```json
{
  "cantidad": 2,
  "turnos": [
    {
      "idTurno": 1,
      "fecha": "2025-12-10",
      "horaInicio": "10:00:00",
      "horaFin": "10:30:00",
      "estado": "Pendiente",
      "motivoConsulta": "Control",
      "paciente": {
        "idPaciente": 1,
        "nombre": "Juan",
        "apellido": "Pérez"
      },
      "odontologo": {
        "idOdontologo": 1,
        "nombre": "Dr. Carlos",
        "apellido": "López",
        "matricula": "MP12345"
      },
      "cobertura": {
        "idCobSocial": 1,
        "nombreCobertura": "OSDE"
      }
    }
  ]
}
```

---

### POST /api/mobile/turnos

Solicitar nuevo turno.

**Request:**

```json
{
  "idPaciente": 1,
  "idOdontologo": 1,
  "fecha": "2025-12-15",
  "horaInicio": "14:00:00",
  "motivoConsulta": "Control",
  "idCoberturaSocial": 1
}
```

**Response (201):**

```json
{
  "mensaje": "Turno solicitado exitosamente",
  "idTurno": 5,
  "fecha": "2025-12-15T14:00:00",
  "horaInicio": "14:00:00",
  "estado": "Pendiente"
}
```

---

### GET /api/mobile/turnos/{idTurno}

Obtener detalles de un turno.

**Response (200):**

```json
{
  "idTurno": 1,
  "fecha": "2025-12-10",
  "horaInicio": "10:00:00",
  "horaFin": "10:30:00",
  "estado": "Pendiente",
  "motivoConsulta": "Control",
  "observaciones": "Primera consulta",
  "paciente": {...},
  "odontologo": {...},
  "cobertura": {...}
}
```

---

### DELETE /api/mobile/turnos/{idTurno}/cancelar

Cancelar turno (con validación de 12 horas).

**Query params:**

- `idPaciente`: ID del paciente (validación de permisos)

**Response (200):**

```json
{
  "mensaje": "Turno cancelado exitosamente",
  "idTurno": 1
}
```

**Response error (400):**

```json
{
  "error": "No se puede cancelar el turno con menos de 12 horas de anticipación"
}
```

---

### GET /api/mobile/turnos/horarios-disponibles

Obtener horarios disponibles de un odontólogo.

**Query params:**

- `odontologoId`: ID del odontólogo
- `fecha`: Fecha en formato `yyyy-MM-dd`

**Response (200):**

```json
{
  "fecha": "2025-12-15",
  "odontologoId": 1,
  "horariosDisponibles": [
    {
      "hora": "09:00:00",
      "horaInicio": "09:00",
      "horaFin": "09:30",
      "duracion": 30,
      "disponible": true
    },
    {
      "hora": "09:30:00",
      "horaInicio": "09:30",
      "horaFin": "10:00",
      "duracion": 30,
      "disponible": true
    }
  ]
}
```

---

## 🏥 Datos Públicos

### GET /api/mobile/public/odontologos

Listar odontólogos activos.

**Response (200):**

```json
{
  "cantidad": 3,
  "odontologos": [
    {
      "id": 1,
      "nombre": "Carlos",
      "apellido": "López",
      "matricula": "MP12345",
      "especialidades": [
        {
          "id": 1,
          "nombre": "Odontología General"
        }
      ]
    }
  ]
}
```

---

### GET /api/mobile/public/especialidades

Listar especialidades.

**Response (200):**

```json
{
  "cantidad": 5,
  "especialidades": [
    {
      "id": 1,
      "nombre": "Odontología General"
    },
    {
      "id": 2,
      "nombre": "Ortodoncia"
    }
  ]
}
```

---

### GET /api/mobile/public/coberturas

Listar coberturas activas.

**Response (200):**

```json
{
  "cantidad": 4,
  "coberturas": [
    {
      "id_cob_social": 1,
      "nombre_cobertura": "OSDE",
      "estado_cobertura": "Activo"
    }
  ]
}
```

---

### GET /api/mobile/public/odontologos/especialidad/{idEspecialidad}

Listar odontólogos por especialidad.

**Response (200):**

```json
{
  "especialidadId": 1,
  "cantidad": 2,
  "odontologos": [
    {
      "id": 1,
      "nombre": "Carlos",
      "apellido": "López",
      "matricula": "MP12345"
    }
  ]
}
```

---

### GET /api/mobile/public/motivos-consulta

Listar motivos de consulta.

**Response (200):**

```json
{
  "cantidad": 9,
  "motivos": [
    {
      "valor": "Consulta",
      "descripcion": "Consulta general"
    },
    {
      "valor": "Control",
      "descripcion": "Control de rutina"
    },
    {
      "valor": "Urgencia",
      "descripcion": "Urgencia"
    }
  ]
}
```

---

## 🔒 Seguridad

- Los endpoints `/auth/login-simple`, `/auth/register-simple` y `/api/mobile/public/**` NO requieren autenticación
- Los endpoints `/api/mobile/paciente/**` y `/api/mobile/turnos/**` tampoco requieren JWT, pero debes enviar el `idPaciente` correcto
- CORS configurado para emuladores Android y redes locales

---

## 🚀 Flujo de Uso en la App

1. **Login/Registro**: Usar `/auth/login-simple` o `/auth/register-simple`
2. **Guardar datos**: Almacenar `idPaciente` en SharedPreferences
3. **Consultar perfil**: GET `/api/mobile/paciente/{idPaciente}`
4. **Ver turnos**: GET `/api/mobile/turnos/paciente/{idPaciente}?tipo=proximos`
5. **Solicitar turno**:
   - Listar odontólogos: GET `/api/mobile/public/odontologos`
   - Ver horarios: GET `/api/mobile/turnos/horarios-disponibles?odontologoId=1&fecha=2025-12-15`
   - Crear turno: POST `/api/mobile/turnos`
6. **Cancelar turno**: DELETE `/api/mobile/turnos/{idTurno}/cancelar?idPaciente={idPaciente}`

---

## 📝 Notas

- La zona horaria configurada es `America/Argentina/Buenos_Aires`
- Los turnos deben cancelarse con al menos 12 horas de anticipación
- Los estados de turno válidos son: `Pendiente`, `Confirmado`, `Completado`, `Cancelado`, `No asistió`
- Los motivos de consulta disponibles: `Consulta`, `Control`, `Urgencia`, `Tratamiento`, `Limpieza`, `Ortodoncia`, `Cirugia`, `Endodoncia`, `Otro`
