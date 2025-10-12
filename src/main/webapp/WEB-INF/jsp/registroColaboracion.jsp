<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>

    <title>Culturarte</title>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/principal.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Pacifico&display=swap" rel="stylesheet">
</head>

<body>
    <div class="container mt-3">
    <c:if test="${not empty mensajeExito}">
        <div class="alert alert-success" role="alert">${mensajeExito}</div>
    </c:if>

    <c:if test="${not empty mensajeError}">
        <div class="alert alert-danger" role="alert">${mensajeError}</div>
    </c:if>
    </div>
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
                        <a class="nav-link text-primary fw-normal"  href="#">Tengo una Propuesta</a>
                    </li>
                    <li class="nav-item d-flex align-items-center">
                        <span class="px-2 text-dark">|</span>
                    </li>
                </ul>

                <form class="d-flex me-3 flex-grow-1" style="max-width: 400px;"
                      action="${pageContext.request.contextPath}/propuestas/buscar" method="get">
                    <input class="form-control form-control-sm me-2 w-100" type="search" name="query"
                           placeholder="Título, descripción, lugar" aria-label="Buscar"
                           value="${query != null ? query : ''}" />
                    <button class="btn btn-sm btn-outline-primary" type="submit">Buscar</button>
                </form>

                <c:choose>
                    <c:when test="${sessionScope.usuarioLogueado.tipo ne 'visitante'}">
                        <div class="d-flex align-items-start gap-2">
                            <div class="rounded-circle bg-dark text-white d-flex justify-content-center align-items-center" style="width: 45px; height: 45px; flex-shrink: 0;">
                                <i class="bi bi-person-fill" style="font-size: 28px;"></i>
                            </div>

                            <div class="d-flex flex-column lh-sm">
                                <span style="font-size: 16px; color: #333;">${sessionScope.usuarioLogueado.nombre} ${sessionScope.usuarioLogueado.apellido}</span>

                                <div class="d-flex align-items-center" style="font-size: 13px;">
                                    <a href="${pageContext.request.contextPath}/usuarios/perfil/${sessionScope.usuarioLogueado.nickname}" class="text-primary text-decoration-underline me-1">Perfil</a>

                                    <span class="text-muted">|</span>

                                    <a href="${pageContext.request.contextPath}/logout" class="text-primary text-decoration-underline ms-1">Salir</a>
                                </div>
                            </div>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <div class="d-flex align-items-center">
                            <a class="nav-link d-inline p-0 text-dark"
                               href="${pageContext.request.contextPath}/usuarios/alta">REGISTRARSE</a>
                            <span class="mx-2 text-dark">|</span>
                            <a class="nav-link d-inline p-0 text-dark"
                               href="${pageContext.request.contextPath}/login">ENTRAR</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </nav>
</header>

<form class="row g-3 mt-3 mb-3" 
      action="${pageContext.request.contextPath}/propuestas/altaColaboracion" 
      method="post">

    <div class="col-md-6">
        <label for="inputRetorno" class="form-label">Tipo de retorno</label>
        <select class="form-select" id="inputRetorno" name="tipoRetorno" required>
            <option value="" selected disabled>Seleccione un tipo de retorno</option>
            <option value="ENTRADAGRATIS">Entradas</option>
            <option value="PORCENTAJEGANANCIA">Porcentaje de ganancias</option>
        </select>
    </div>

    <div class="col-md-6">
        <label for="inputMonto" class="form-label">Monto</label>
        <input type="number" class="form-control" id="inputMonto" name="monto" required>
    </div>

    <input type="hidden" name="tituloPropuesta" value="${tituloPropuesta}">
    <input type="hidden" name="nickColaborador" value="${sessionScope.usuarioLogueado.nickname}">

    <div class="col-12">
        <button type="submit" class="btn btn-primary">Registrar Colaboración</button>
    </div>
    </form>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>