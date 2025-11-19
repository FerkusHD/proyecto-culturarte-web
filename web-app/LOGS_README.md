# 📋 Guía de Logs - Culturarte Web

## 📁 Ubicación de los Logs

Los logs se guardan en la carpeta `logs/` en el directorio raíz del proyecto web-app.

### Archivos de Log Disponibles:

1. **`logs/culturarte-web.log`** - Todos los logs de la aplicación
2. **`logs/errors.log`** - Solo errores y warnings (WARN y ERROR)
3. **`logs/soap-errors.log`** - Solo errores relacionados con SOAP
4. **`logs/culturarte-web-YYYY-MM-DD.log`** - Logs diarios (rotación automática)

## 🔍 Cómo Analizar los Errores

### 1. Ver errores recientes:
```bash
# Ver los últimos 50 errores
tail -n 50 logs/errors.log

# Ver errores de SOAP
tail -n 50 logs/soap-errors.log

# Ver todos los logs recientes
tail -n 100 logs/culturarte-web.log
```

### 2. Buscar errores específicos:
```bash
# Buscar errores de propuestas
grep -i "propuestas" logs/errors.log

# Buscar errores de categorías
grep -i "categorias" logs/errors.log

# Buscar errores de perfil
grep -i "perfil\|mostrarPerfil" logs/errors.log
```

### 3. Formato de los Logs:

Cada línea de log tiene el siguiente formato:
```
YYYY-MM-DD HH:mm:ss.SSS [thread] LEVEL logger - mensaje
```

Ejemplo:
```
2025-11-15 01:02:40.123 [http-nio-8080-exec-1] ERROR c.c.w.c.PropuestasController - === ERROR en listarPropuestas ===
```

## 🐛 Problemas Comunes y Cómo Diagnosticarlos

### Problema: No se muestran las propuestas

1. **Revisar logs de propuestas:**
   ```bash
   grep "listarPropuestas" logs/errors.log
   ```

2. **Verificar conexión SOAP:**
   ```bash
   grep "SOAP\|Endpoint\|Error al invocar" logs/soap-errors.log
   ```

3. **Buscar en todos los logs:**
   ```bash
   grep -i "propuestas" logs/culturarte-web.log | tail -20
   ```

### Problema: No se muestran las categorías

1. **Revisar logs de categorías:**
   ```bash
   grep "categorias" logs/errors.log
   ```

2. **Verificar endpoint:**
   ```bash
   grep "categorias/tree\|categorias/lista" logs/culturarte-web.log | tail -20
   ```

### Problema: No se puede ver el perfil de usuario

1. **Revisar logs del perfil:**
   ```bash
   grep "mostrarPerfil" logs/errors.log
   ```

2. **Verificar obtención de usuario:**
   ```bash
   grep "getUsuario\|Usuario no encontrado" logs/culturarte-web.log | tail -20
   ```

## 📊 Niveles de Log

- **ERROR**: Errores críticos que impiden el funcionamiento
- **WARN**: Advertencias (problemas no críticos)
- **INFO**: Información general de operaciones
- **DEBUG**: Información detallada para debugging

## 🔧 Configuración

La configuración de logs está en: `src/main/resources/logback-spring.xml`

Para cambiar el nivel de log, edita el archivo y modifica los niveles en los loggers.

## 💡 Tips

1. **Siempre revisa primero `errors.log`** - Contiene solo los problemas
2. **Usa `soap-errors.log`** para problemas de comunicación SOAP
3. **Los logs se rotan diariamente** - Los archivos antiguos se mantienen 30 días
4. **Los logs incluyen stack traces completos** para facilitar el debugging

## 🚨 Si no encuentras los logs

1. Verifica que la carpeta `logs/` existe en el directorio del proyecto
2. Verifica permisos de escritura en el directorio
3. Revisa la consola de la aplicación para ver si hay errores al crear los archivos

