<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Login - Culturarte</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/principal.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Pacifico&display=swap" rel="stylesheet">
</head>
<body>

<c:if test="${!esMovil}">
<header>
    <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom">
        <div class="container-fluid">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">Culturarte</a>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarContent">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <a class="nav-link text-primary fw-normal" href="${pageContext.request.contextPath}/usuarios/buscar">
                            Buscar usuarios
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link text-primary fw-normal" href="${pageContext.request.contextPath}/propuestas/buscar">
                            Buscar propuestas
                        </a>
                    </li>
                </ul>

                <c:choose>
                    <c:when test="${sessionScope.usuarioLogueado.tipo ne 'visitante'}">
                        <div class="d-flex align-items-center">
                            <span class="me-2">${sessionScope.usuarioLogueado.nombre} ${sessionScope.usuarioLogueado.apellido}</span>
                            <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-primary btn-sm">Salir</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/usuarios/alta" class="btn btn-primary btn-sm me-2">Registrarse</a>
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-primary btn-sm">Entrar</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </nav>
</header>
</c:if>

<main class="container mt-5" style="max-width: 450px;">
    <div class="card shadow-sm p-4">
        <h2 class="text-center mb-4">Iniciar sesión</h2>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="mb-3">
                <label for="nickOemail" class="form-label">Nickname o email</label>
                <input id="nickOemail" name="nickOemail" type="text" maxlength="30"
                       placeholder="nickname o email" value="${nickOemail}" required
                       class="form-control">
            </div>

            <div class="mb-3">
                <label for="password" class="form-label">Contraseña</label>
                <input id="password" name="password" type="password" placeholder="contraseña"
                       required class="form-control">
            </div>

            <c:if test="${not empty mensaje}">
                <div class="alert alert-danger">${mensaje}</div>
            </c:if>

            <button type="submit" class="btn btn-primary w-100">Entrar</button>
        </form>

        <div class="mt-3 text-center">
            <span>¿No tienes cuenta? <a href="${pageContext.request.contextPath}/usuarios/alta">Regístrate</a></span>
        </div>
    </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>

