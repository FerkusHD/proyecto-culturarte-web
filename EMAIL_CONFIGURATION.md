# Configuración de Email para Culturarte

## Opciones para Envío de Emails

### Opción 1: Mailtrap (⭐ Recomendado para Desarrollo)

**Mailtrap** captura los emails y los muestra en una bandeja web. Los emails NO se envían a los destinatarios reales, pero puedes verlos completamente renderizados.

1. **Regístrate gratis**: https://mailtrap.io/
2. **Crea un inbox** en tu cuenta
3. **Copia las credenciales SMTP** que te proporciona Mailtrap
4. **Edita el archivo** `email-config-mailtrap.properties`:
   ```bash
   nano email-config-mailtrap.properties
   ```
5. **Reemplaza** `TU_MAILTRAP_USERNAME` y `TU_MAILTRAP_PASSWORD` con tus credenciales
6. **Copia el archivo** a la ubicación correcta:
   ```bash
   mkdir -p ~/.Culturarte
   cp email-config-mailtrap.properties ~/.Culturarte/soap-service.properties
   ```
7. **Reinicia los servicios**: `./open-jar.sh`

### Opción 2: Gmail (Para Enviar Emails Reales)

Usa tu cuenta de Gmail para enviar emails reales a las direcciones de los usuarios.

1. **Genera una contraseña de aplicación**:
   - Ve a https://myaccount.google.com/
   - Seguridad → Verificación en dos pasos (actívala si no la tienes)
   - Contraseñas de aplicaciones → Genera una nueva
   - Copia la contraseña de 16 caracteres

2. **Edita el archivo** `email-config-gmail.properties`:
   ```bash
   nano email-config-gmail.properties
   ```

3. **Reemplaza**:
   - `TU_EMAIL@gmail.com` con tu email de Gmail
   - `TU_CONTRASEÑA_DE_APLICACION` con la contraseña generada

4. **Copia el archivo**:
   ```bash
   mkdir -p ~/.Culturarte
   cp email-config-gmail.properties ~/.Culturarte/soap-service.properties
   ```

5. **Reinicia los servicios**: `./open-jar.sh`

### Opción 3: DevNull (Solo para Ver Logs)

### Configuración en Culturarte

La configuración de email ya está incluida en `soap-service/src/main/resources/application.properties`:

```properties
# Configuración de Email (JavaMail)
spring.mail.host=${MAIL_HOST:localhost}
spring.mail.port=${MAIL_PORT:25}
spring.mail.username=${MAIL_USERNAME:}
spring.mail.password=${MAIL_PASSWORD:}
spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.mail.smtp.starttls.enable=false
spring.mail.properties.mail.smtp.starttls.required=false
spring.mail.properties.mail.debug=true

# URL base de la aplicación web (para links en emails)
app.base-url=${APP_BASE_URL:http://localhost:8080}
```

### Uso

1. **Inicia el servidor SMTP** (DevNull) en el puerto 25
2. **Reinicia los servicios de Culturarte** con `./open-jar.sh`
3. **Realiza un pago** en la aplicación
4. **Verifica los logs** en `soap.log` para ver los emails que se intentaron enviar

### Emails que se envían

Cuando se confirma un pago, se envían **2 emails**:

1. **Email al Colaborador** (quien realizó el pago):
   - Asunto: "✅ Confirmación de Pago - [Nombre de la Propuesta]"
   - Contenido: Detalles del pago y link para descargar la constancia
   
2. **Email al Proponente** (dueño de la propuesta):
   - Asunto: "💰 Nuevo Pago Recibido - [Nombre de la Propuesta]"
   - Contenido: Notificación del nuevo aporte recibido

### Formato de los Emails

Los emails se envían en formato HTML con un diseño profesional que incluye:
- Header con gradiente de colores
- Detalles del pago en una tabla
- Botón para descargar constancia (solo para el colaborador)
- Footer con información de la aplicación

### Troubleshooting

Si los emails no se envían:

1. **Verifica que DevNull esté corriendo**:
   ```bash
   docker ps | grep devnull-smtp
   ```

2. **Revisa los logs del servicio SOAP**:
   ```bash
   tail -f soap.log | grep -i "email\|mail"
   ```

3. **Verifica la configuración**:
   - El puerto 25 debe estar disponible
   - El servicio SOAP debe tener acceso a localhost:25

### Configuración para Producción

Para usar un servidor SMTP real en producción, actualiza las variables de entorno:

```bash
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=tu-email@gmail.com
export MAIL_PASSWORD=tu-password
export APP_BASE_URL=https://tu-dominio.com
```

Y modifica las propiedades en `application.properties`:

```properties
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```
