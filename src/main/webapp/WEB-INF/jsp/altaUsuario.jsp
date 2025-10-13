<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registro - Culturarte</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/principal.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Pacifico&display=swap" rel="stylesheet">
</head>
<body>

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

<main class="container mt-5" style="max-width: 600px;">
    <div class="card shadow-sm p-4">
        <h2 class="text-center mb-4">Registro de Usuario</h2>

        <form action="${pageContext.request.contextPath}/usuarios/alta" method="post" enctype="multipart/form-data">
            <div class="mb-3">
                <label for="nickname" class="form-label">Nickname</label>
                <input id="nickname" name="nickname" type="text" maxlength="30" placeholder="nickname"
                       value="${nickname}" required class="form-control">
            </div>

            <div class="mb-3">
                <label for="nombre" class="form-label">Nombre</label>
                <input id="nombre" name="nombre" type="text" placeholder="nombre" value="${nombre}" required class="form-control">
            </div>

            <div class="mb-3">
                <label for="apellido" class="form-label">Apellido</label>
                <input id="apellido" name="apellido" type="text" placeholder="apellido" value="${apellido}" required class="form-control">
            </div>

            <div class="mb-3">
                <label for="email" class="form-label">Email</label>
                <input id="email" name="email" type="email" placeholder="email" value="${email}" required class="form-control">
            </div>

            <div class="mb-3">
                <label for="password" class="form-label">Contraseña</label>
                <input id="password" name="password" type="password" placeholder="contraseña" required class="form-control">
            </div>

            <div class="mb-3">
                <label for="confirmar" class="form-label">Confirmar Contraseña</label>
                <input id="confirmar" name="confirmar" type="password" placeholder="confirmar" required class="form-control">
            </div>

            <div class="mb-3">
                <label for="fecha" class="form-label">Fecha de nacimiento</label>
                <input id="fecha" name="fecha" type="date" value="${date}" required class="form-control">
            </div>

            <div class="mb-3">
                <label for="imagen" class="form-label">Subir imagen</label>
                <input type="file" id="imagen" name="imagen" accept="image/*" class="form-control">
            </div>

            <fieldset class="mb-3">
                <legend>Selecciona tu rol</legend>
                <div class="form-check">
                    <input id="colaborador" onclick="cambiarPanelProponente()" type="radio" name="rol" value="colaborador"
                           class="form-check-input" ${rol == 'colaborador' ? 'checked' : ''} required>
                    <label class="form-check-label" for="colaborador">Colaborador</label>
                </div>
                <div class="form-check">
                    <input id="proponente" onclick="cambiarPanelProponente()" type="radio" name="rol" value="proponente"
                           class="form-check-input" ${rol == 'proponente' ? 'checked' : ''}>
                    <label class="form-check-label" for="proponente">Proponente</label>
                </div>
            </fieldset>

            <div class="mb-3 campo-extra">
                <label for="direccion" class="form-label">Dirección</label>
                <input id="direccion" name="direccion" type="text" placeholder="direccion" value="${direccion}" class="form-control">
            </div>

            <div class="mb-3 campo-extra">
                <label for="biografia" class="form-label">Biografía</label>
                <textarea id="biografia" name="biografia" placeholder="biografia" class="form-control">${biografia}</textarea>
            </div>

            <div class="mb-3 campo-extra">
                <label for="web" class="form-label">Sitio web</label>
                <input id="web" name="web" type="text" placeholder="sitio web" value="${web}" class="form-control">
            </div>

            <c:if test="${not empty mensaje}">
                <div class="alert alert-danger">${mensaje}</div>
            </c:if>

            <button type="submit" class="btn btn-primary w-100">Registrarse</button>
        </form>
    </div>
</main>

<script>
    // Mostrar campos extra si es proponente
    function cambiarPanelProponente() {
        const proponente = document.getElementById('proponente');
        const extras = document.querySelectorAll('.campo-extra');
        extras.forEach(div => div.style.display = proponente.checked ? 'block' : 'none');
    }

    window.addEventListener('DOMContentLoaded', () => {
        const extras = document.querySelectorAll('.campo-extra');
        extras.forEach(div => div.style.display = 'none');
    });
</script>

<script src="${pageContext.request.contextPath}/js/validarContraseña.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
