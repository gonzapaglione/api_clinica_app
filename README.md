<h2>Configuración inicial</h2>

<p>
  Antes de ejecutar la aplicación, modificá el archivo
  <strong>application.properties</strong> con tus credenciales de MySQL:
</p>

<pre>
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASEÑA
</pre>

<p>
  La base de datos <strong>clinica_app</strong> se crea automáticamente al iniciar la aplicación.
</p>

<hr>

<h2>Documentación de la API (Swagger / OpenAPI)</h2>

<p>
  La aplicación cuenta con <strong>Swagger OpenAPI</strong> implementado para visualizar y probar todos los endpoints de la API de manera interactiva.
</p>

<p>Puedes acceder a la documentación desde:</p>

<pre>
http://localhost:8080/swagger-ui/index.html
</pre>

<p>
  Desde allí puedes explorar los controladores, enviar requests y verificar el funcionamiento de la API sin necesidad de usar herramientas externas.
</p>

<hr>

<h2>Cuentas de prueba</h2>

<p>La aplicación incluye usuarios de prueba con turnos pre-cargados.</p>

<h3>Pacientes</h3>

<table>
  <thead>
    <tr>
      <th>Usuario</th>
      <th>Email</th>
      <th>Contraseña</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Paciente 1</td>
      <td>paciente1@gmail.com</td>
      <td>paciente</td>
    </tr>
    <tr>
      <td>Paciente 2</td>
      <td>paciente2@gmail.com</td>
      <td>paciente</td>
    </tr>
    <tr>
      <td>Paciente 3</td>
      <td>paciente3@gmail.com</td>
      <td>paciente</td>
    </tr>
    <tr>
      <td>Paciente 4</td>
      <td>paciente4@gmail.com</td>
      <td>paciente</td>
    </tr>
    <tr>
      <td>Paciente 5</td>
      <td>paciente5@gmail.com</td>
      <td>paciente</td>
    </tr>
  </tbody>
</table>
