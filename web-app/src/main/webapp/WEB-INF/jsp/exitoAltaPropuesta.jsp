<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Propuesta Creada - Culturarte</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/altaUsuario.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Pacifico&display=swap" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container my-5">
    <div class="text-center bg-white p-4 rounded shadow" style="max-width: 600px; margin:auto;">
        <h1 class="culturarte">Culturarte</h1>
        <h2>${mensaje}</h2>
        <p>Tu propuesta fue creada correctamente.</p>
        <div class="mt-4">
            <a href="${pageContext.request.contextPath}/propuestas/alta" class="btn btn-primary me-2">Crear otra propuesta</a>
            <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">Ir al inicio</a>
        </div>
    </div>
</div>
</body>
</html>