<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Culturarte</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/altaUsuario.css">

    <link href="https://fonts.googleapis.com/css2?family=Pacifico&display=swap" rel="stylesheet">
</head>
<body>
    <h1>Bienvenido/a a Culturarte</h1>
    <div class="culturarte">Culturarte</div>
    <form action="${pageContext.request.contextPath}/usuarios/alta" method="post">
         <div class="formulario">
            <label for="nickname">Nickname</label>
            <input id="nickname" name="nickname" type="text" maxlength="30" placeholder="nickname" value="${nickname}" required>
         </div>

         <div class="formulario">
            <label for="nombre">Nombre</label>
            <input id="nombre"  name="nombre" type="text" placeholder="nombre" value="${nombre}" required>
         </div>

         <div class="formulario">
            <label for="apellido">Apellido</label>
            <input id="apellido"  name="apellido" type="text" placeholder="apellido" value="${apellido}" required>
         </div>

         <div class="formulario">
            <label for="password">Contraseña</label>
            <input id="password"  name="password" type="password" placeholder="contraseña" value="${password}" required>
         </div>

        <div class="formulario">
            <label for="confirmar">Confirmar contraseña</label>
            <input id="confirmar" name="confirmar" type="password" placeholder="confirmar" value="${confirmar}" required>
        </div>

        <div id="mensaje"></div>

        <div class="formulario">
            <label for="email">Email</label>
            <input id="email" name="email" type="email" placeholder="email" value="${email}" required>
        </div>

         <div class="formulario">
            <label for="fecha">Fecha de nacimiento</label>
            <input id="fecha"  name="fecha" type="date" value="${date}" required>
         </div>

         <div class="formulario">
          <label for="imagen">Subir imagen:</label>
          <input type="file" id="imagen" name="imagen" accept="image/*">
         </div>

         <fieldset class="checkbox-group">
          <legend>Selecciona tu rol</legend>
          <label>
            <input id="colaborador" onclick="cambiarPanelProponente()" type="radio" name="rol" value="colaborador"
                   ${rol == 'colaborador' ? 'checked' : ''}required>
            Colaborador
          </label>
          <label>
            <input id="proponente" onclick="cambiarPanelProponente()" type="radio" name="rol" value="proponente"
            ${rol == 'proponente' ? 'checked' : ''}>
            Proponente
          </label>
        </fieldset>

        <div class="campo-extra">
            <label for="direccion">Dirección</label>
            <input id="direccion" name="direccion" type="text" placeholder="direccion" value="${direccion}">
         </div>

         <div class="campo-extra">
            <label for="biografia">Biografia</label>
            <textarea id="biografia" name="biografia" type="textarea" placeholder="biografia">${biografia}</textarea>
         </div>

         <div class="campo-extra">
            <label for="web">Sitio web</label>
            <input id="web" name="web" type="text" placeholder="sitio web" value="${web}">
         </div>

        <c:if test="${not empty mensaje}">
            <p>${mensaje}</p>
        </c:if>

         <button type="submit">Registrarse</button>
   </form>

    <script>
        // Cambiar camposExtras
       function cambiarPanelProponente() {
           const proponente = document.getElementById('proponente');
           const extras = document.querySelectorAll('.campo-extra');

           if (proponente.checked) {
               extras.forEach(div => div.style.display = 'block');
           } else {
               extras.forEach(div => div.style.display = 'none');
           }
       }

       // Ocultar al cargar la página
       window.addEventListener('DOMContentLoaded', () => {
       const extras = document.querySelectorAll('.campo-extra');
       extras.forEach(div => div.style.display = 'none');
       });
    </script>

    <script src="${pageContext.request.contextPath}/js/validarContraseña.js"></script>

</body>
</html>