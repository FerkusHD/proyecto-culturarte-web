# Revisión de Funciones Web y su Disponibilidad en SOAP

## ✅ Funciones Implementadas en SOAP

1. **Propuestas:**
   - ✅ `getDTPropuestasWeb()` - Listar todas las propuestas
   - ✅ `getDTPropuesta(String titulo)` - Obtener una propuesta específica
   - ✅ `buscarPropuestas(String texto)` - Buscar propuestas (filtra localmente)

2. **Categorías:**
   - ✅ `listarCategoriasWeb()` - Listar categorías
   - ✅ `listarCategoriasWebCompletas()` - Listar categorías completas

3. **Usuarios:**
   - ✅ `getDTUsuario(String nickname)` - Obtener usuario por nickname
   - ✅ `listarUsuarios()` - Listar todos los usuarios
   - ✅ `buscarUsuarios(String nombre)` - Buscar usuarios por nombre/nickname
   - ✅ Verificación de nickname (vía VerificacionSoapService)
   - ✅ Verificación de email (vía VerificacionSoapService)

## ❌ Funciones NO Disponibles en SOAP (lanzan UnsupportedOperationException)

1. **Usuarios:**
   - ❌ `getDTProponente(String nickname)` - Usado en: perfil, propuestas
   - ❌ `getDTColaborador(String nickname)` - Usado en: perfil, propuestas, colaboraciones
   - ❌ `seguirUsuario(String nickSeguidor, String nickSeguido)` - Usado en: usuarios
   - ❌ `dejarDeSeguirUsuario(String nickSeguidor, String nickSeguido)` - Usado en: usuarios
   - ❌ `verificarPassword(String password, String nick)` - Usado en: login (tiene fallback)

2. **Propuestas:**
   - ❌ `altaPropuesta(...)` - Usado en: propuestas/alta
   - ❌ `altaColaboracion(...)` - Usado en: propuestas/altaColaboracion
   - ❌ `agregarComentario(...)` - Usado en: propuestas/agregarComentario
   - ❌ `agregarPropuestaFavorita(...)` - Usado en: propuestas/agregarFavorita
   - ❌ `sacarPropuestaFavorita(...)` - Usado en: propuestas/quitarFavorita

3. **Otros:**
   - ❌ `getNomProponentes()` - No usado en web
   - ❌ `getNickColaboradores()` - No usado en web
   - ❌ `getNomColaboradores()` - No usado en web

## 🔧 Soluciones Implementadas

1. **Cambio de XSD:** Se modificaron los XSD para cambiar `anyType` a tipos vacíos:
   - `listarPropuestasRequest` ahora es un `complexType` vacío
   - `getCategoriasRequest` ahora es un `complexType` vacío

2. **Implementación de endpoints SOAP:**
   - `listarUsuarios` - Implementado en UsuarioEndpoint
   - `buscarUsuarios` - Implementado en UsuarioEndpoint

3. **Implementación en adaptador:**
   - `listarUsuarios()` - Implementado en SoapControladorAdapter
   - `buscarUsuarios()` - Implementado en SoapControladorAdapter

## 📝 Notas

- El endpoint `/propuestas/listar` ahora funciona correctamente
- El endpoint `/usuarios/buscar` ahora funciona correctamente
- Los métodos que no están disponibles en SOAP lanzan `UnsupportedOperationException` que los controladores manejan con try-catch

