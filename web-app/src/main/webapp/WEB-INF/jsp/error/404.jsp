<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Error 404 - Culturarte</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/principal.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Pacifico&display=swap" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-6 text-center">
                <h1 class="display-1">404</h1>
                <h2 class="mb-4">Página no encontrada</h2>
                <c:if test="${not empty mensajeError}">
                    <div class="alert alert-warning" role="alert">
                        ${mensajeError}
                    </div>
                </c:if>
                <p class="text-muted mb-4">Lo sentimos, la página que buscas no existe o ha sido movida.</p>
                <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Volver al inicio</a>
            </div>
        </div>
    </div>
</body>
</html>

