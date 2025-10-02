
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Culturarte</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/inicioSesion.css">

    <link href="https://fonts.googleapis.com/css2?family=Pacifico&display=swap" rel="stylesheet">

</head>
<body>
    <h1>Iniciar sesión</h1>
    <div class="culturarte">Culturarte</div>
    <form action="" method="post">
     <div class="formulario">
        <label for="nickOemail">Nickname o email</label>
        <input id="nickOemail" name="nickOemail" type="text" maxlength="30" placeholder="nickname o email" required>
     </div>

     <div class="formulario">
        <label for="contraseña">Contraseña</label>            
        <input id="contraseña"  name="contraseña" type="password" placeholder="contraseña" required>
     </div>

    <button type="submit">Registrarse</button>
    </form>
    
</body>
</html>