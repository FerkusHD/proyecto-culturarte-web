<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
    <title>Registro exitoso</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/exitoAltaUsuario.css">
    <link href="https://fonts.googleapis.com/css2?family=Pacifico&display=swap" rel="stylesheet">
</head>
<body>
    <div class="culturarte">Culturarte</div>
    <div class="contenedor">
        <h2>${mensaje}</h2>
        <p>Tu cuenta fue creada correctamente.</p>
        <a href="${pageContext.request.contextPath}/login">
            <button>Ir al login</button>
        </a>
    </div>
</body>

</html>