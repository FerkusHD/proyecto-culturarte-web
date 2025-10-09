<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Culturarte</title>
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
                        <a class="nav-link text-primary fw-normal" href="${pageContext.request.contextPath}/usuarios/perfiles">Buscar usuarios</a>
                    </li>
                    <c:if test="${sessionScope.usuarioLogueado.tipo eq 'proponente'}">
                        <li class="nav-item d-flex align-items-center">
                            <span class="px-2 text-dark">|</span>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link text-primary fw-normal" href="${pageContext.request.contextPath}/propuestas/alta">Tengo una propuesta</a>
                        </li>
                    </c:if>
                    <c:if test="${sessionScope.usuarioLogueado.tipo eq 'colaborador'}">
                        <li class="nav-item d-flex align-items-center">
                            <span class="px-2 text-dark">|</span>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link text-primary fw-normal" href="${pageContext.request.contextPath}/propuestas/registrarColaboracionProp">Quiero colaborar</a>
                        </li>
                    </c:if>
                </ul>

                <form class="d-flex me-3 flex-grow-1 position-relative" style="max-width: 400px;"
                      action="${pageContext.request.contextPath}/propuestas/buscar" method="get">
                    <input class="form-control form-control-sm me-2 w-100" type="search"
                           id="buscador" name="query"
                           placeholder="Título, descripción, lugar" aria-label="Buscar"
                           autocomplete="off" value="${query != null ? query : ''}" />
                    <button class="btn btn-sm btn-outline-primary" type="submit">Buscar</button>
                    <!-- Contenedor de sugerencias -->
                    <div id="sugerencias"
                         class="list-group position-absolute w-100"
                         style="top: 38px; z-index: 1000;"></div>
                </form>

                <c:choose>
                    <c:when test="${sessionScope.usuarioLogueado.tipo ne 'visitante'}">
                        <div class="d-flex align-items-start gap-2">
                            <div class="rounded-circle bg-dark text-white d-flex justify-content-center align-items-center"
                                 style="width: 45px; height: 45px;">
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
                            <a class="nav-link d-inline p-0 text-dark" href="${pageContext.request.contextPath}/usuarios/alta">REGISTRARSE</a>
                            <span class="mx-2 text-dark">|</span>
                            <a class="nav-link d-inline p-0 text-dark" href="${pageContext.request.contextPath}/login">ENTRAR</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </nav>
</header>

<main class="container mt-4">
    <h2 class="mb-3">Resultados de búsqueda (${resultados.size()})</h2>

    <form class="row g-3 align-items-center mb-4"
          action="${pageContext.request.contextPath}/propuestas/buscar"
          method="get">

        <div class="col-md-5 position-relative">
            <input type="text" id="buscador" name="query" class="form-control"
                   placeholder="Título, descripción, lugar"
                   value="${query}" autocomplete="off" />
            <div id="sugerencias"
                 class="list-group position-absolute w-100"
                 style="top: 38px; z-index: 1000;"></div>
        </div>

        <div class="col-md-3">
            <select name="categoria" id="categoria" class="form-select">
                <option value="">-- Filtrar por categoría --</option>
                <c:forEach var="cat" items="${categorias}">
                    <option value="${cat.nombre}" ${categoria == cat.nombre ? 'selected' : ''}>${cat.nombre}</option>
                </c:forEach>
            </select>
        </div>

        <div class="col-md-3">
            <select name="estado" class="form-select">
                <option value="">-- Filtrar por estado --</option>
                <option value="PUBLICADA" ${estado == 'PUBLICADA' ? 'selected' : ''}>Publicada</option>
                <option value="ENFINANCIACION" ${estado == 'ENFINANCIACION' ? 'selected' : ''}>En financiación</option>
                <option value="FINANCIADA" ${estado == 'FINANCIADA' ? 'selected' : ''}>Financiada</option>
                <option value="NOFINANCIADA" ${estado == 'NOFINANCIADA' ? 'selected' : ''}>No financiada</option>
                <option value="CANCELADA" ${estado == 'CANCELADA' ? 'selected' : ''}>Cancelada</option>
            </select>
        </div>

        <div class="col-md-3">
            <select name="orden" class="form-select">
                <option value="tituloAsc" ${orden == 'tituloAsc' ? 'selected' : ''}>Alfabéticamente (A-Z)</option>
                <option value="fechaDesc" ${orden == 'fechaDesc' ? 'selected' : ''}>Fecha creación (descendente)</option>
            </select>
        </div>

        <div class="col-md-1">
            <button type="submit" class="btn btn-primary w-100">Buscar</button>
        </div>
    </form>

    <c:choose>
        <c:when test="${empty resultados}">
            <div class="alert alert-warning">No se encontraron propuestas.</div>
        </c:when>
        <c:otherwise>
            <div class="list-group">
                <c:forEach var="p" items="${resultados}">
                    <a href="${pageContext.request.contextPath}/propuestas/listar/${p.titulo}"
                       class="list-group-item list-group-item-action">
                        <h5 class="mb-1">${p.titulo}</h5>
                        <small class="text-muted">${p.lugar} — ${p.estadoActual}</small>
                        <p class="mb-1">${p.descripcion}</p>
                    </a>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<script src="${pageContext.request.contextPath}/js/busquedaAjax.js"></script>
<script src="${pageContext.request.contextPath}/js/propuestasAndCategorias.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
