<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Recomendaciones - Culturarte</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
                <link rel="stylesheet"
                    href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
            </head>

            <body class="bg-light">
                <header>
                    <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom">
                        <div class="container-fluid">
                            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">Culturarte</a>
                            <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                                data-bs-target="#navbarContent">
                                <span class="navbar-toggler-icon"></span>
                            </button>
                            <div class="collapse navbar-collapse" id="navbarContent">
                                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                                    <li class="nav-item">
                                        <a class="nav-link text-primary fw-normal"
                                            href="${pageContext.request.contextPath}/usuarios/buscar">Buscar
                                            usuarios</a>
                                    </li>
                                    <c:if test="${sessionScope.usuarioLogueado.tipo eq 'proponente'}">
                                        <li class="nav-item d-flex align-items-center"><span
                                                class="px-2 text-dark">|</span></li>
                                        <li class="nav-item">
                                            <a class="nav-link text-primary fw-normal"
                                                href="${pageContext.request.contextPath}/propuestas/alta">Tengo una
                                                propuesta</a>
                                        </li>
                                    </c:if>
                                    <li class="nav-item d-flex align-items-center"><span class="px-2 text-dark">|</span>
                                    </li>
                                    <li class="nav-item">
                                        <a class="nav-link text-primary fw-normal"
                                            href="${pageContext.request.contextPath}/usuarios/ranking">Ranking de
                                            usuarios</a>
                                    </li>
                                    <c:if test="${sessionScope.usuarioLogueado.tipo eq 'colaborador'}">
                                        <li class="nav-item d-flex align-items-center"><span
                                                class="px-2 text-dark">|</span></li>
                                        <li class="nav-item">
                                            <a class="nav-link text-primary fw-normal active"
                                                href="${pageContext.request.contextPath}/propuestas/recomendaciones">Recomendaciones</a>
                                        </li>
                                    </c:if>
                                </ul>

                                <form class="d-flex me-3 flex-grow-1 position-relative" style="max-width: 400px;"
                                    action="${pageContext.request.contextPath}/propuestas/buscar" method="get">
                                    <input class="form-control form-control-sm me-2 w-100" type="search" id="buscador"
                                        name="query" placeholder="Título, descripción, lugar" aria-label="Buscar"
                                        autocomplete="off" value="${query != null ? query : ''}" />
                                    <button class="btn btn-sm btn-outline-primary" type="submit">Buscar</button>
                                    <div id="sugerencias" class="list-group position-absolute w-100"
                                        style="top: 38px; z-index: 1000;"></div>
                                </form>

                                <c:choose>
                                    <c:when test="${sessionScope.usuarioLogueado.tipo ne 'visitante'}">
                                        <div class="d-flex align-items-start gap-2">
                                            <c:choose>
                                                <c:when test="${not empty sessionScope.usuarioLogueado.imagen}">
                                                    <img src="${pageContext.request.contextPath}/${sessionScope.usuarioLogueado.imagen}"
                                                        class="rounded-circle" alt="Usuario"
                                                        style="width: 45px; height: 45px; object-fit: cover;">
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="rounded-circle bg-dark text-white d-flex justify-content-center align-items-center"
                                                        style="width: 45px; height: 45px;">
                                                        <i class="bi bi-person-fill" style="font-size: 28px;"></i>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                            <div class="d-flex flex-column lh-sm">
                                                <span
                                                    style="font-size: 16px; color: #333;">${sessionScope.usuarioLogueado.nombre}
                                                    ${sessionScope.usuarioLogueado.apellido}</span>
                                                <div class="d-flex align-items-center" style="font-size: 13px;">
                                                    <a href="${pageContext.request.contextPath}/usuarios/${sessionScope.usuarioLogueado.nickname}"
                                                        class="text-primary text-decoration-underline me-1">Perfil</a>
                                                    <span class="text-muted">|</span>
                                                    <a href="${pageContext.request.contextPath}/logout"
                                                        class="text-primary text-decoration-underline ms-1">Salir</a>
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

                <div class="container mt-4">
                    <div class="row">
                        <div class="col-12">
                            <h2 class="mb-4">
                                <i class="bi bi-stars text-warning"></i> Propuestas Recomendadas para Ti
                            </h2>
                            <p class="text-muted">Basadas en tus intereses y colaboraciones anteriores</p>

                            <c:if test="${not empty mensajeError}">
                                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                    ${mensajeError}
                                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                </div>
                            </c:if>

                            <c:choose>
                                <c:when test="${not empty recomendaciones}">
                                    <div class="row g-4">
                                        <c:forEach var="propuesta" items="${recomendaciones}">
                                            <div class="col-md-6 col-lg-4">
                                                <div class="card h-100 shadow-sm border-0 rounded-3 overflow-hidden hover-shadow"
                                                    style="transition: transform 0.2s;">
                                                    <!-- Imagen de la propuesta -->
                                                    <c:choose>
                                                        <c:when test="${not empty propuesta.imagen}">
                                                            <img src="${pageContext.request.contextPath}/${propuesta.imagen}"
                                                                class="card-img-top" alt="${propuesta.titulo}"
                                                                style="height: 200px; object-fit: cover;">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="d-flex justify-content-center align-items-center bg-light"
                                                                style="height: 200px;">
                                                                <i class="bi bi-image fs-1 text-muted"></i>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>

                                                    <div class="card-body">
                                                        <h5 class="card-title fw-bold">${propuesta.titulo}</h5>
                                                        <p class="card-text text-muted">
                                                            <c:choose>
                                                                <c:when
                                                                    test="${fn:length(propuesta.descripcion) > 150}">
                                                                    ${propuesta.descripcion.substring(0, 150)}...
                                                                </c:when>
                                                                <c:otherwise>
                                                                    ${propuesta.descripcion}
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </p>
                                                        <div
                                                            class="d-flex justify-content-between align-items-center mb-2">
                                                            <span
                                                                class="badge bg-primary">${propuesta.estadoActual}</span>
                                                            <small class="text-muted">
                                                                <i class="bi bi-people-fill"></i>
                                                                ${propuesta.cantColaboradores} colaboradores
                                                            </small>
                                                        </div>
                                                        <div class="progress mb-2" style="height: 8px;">
                                                            <c:set var="porcentaje"
                                                                value="${(propuesta.montoRecaudado / propuesta.montoNecesario) * 100}" />
                                                            <div class="progress-bar bg-success" role="progressbar"
                                                                style="width: ${porcentaje > 100 ? 100 : porcentaje}%">
                                                            </div>
                                                        </div>
                                                        <small class="text-muted">
                                                            $${propuesta.montoRecaudado} / $${propuesta.montoNecesario}
                                                        </small>
                                                    </div>

                                                    <div class="card-footer bg-white border-0 text-center">
                                                        <a href="${pageContext.request.contextPath}/propuestas/${propuesta.titulo}"
                                                            class="btn btn-sm btn-primary w-100">
                                                            Ver Propuesta
                                                        </a>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="text-center py-5">
                                        <i class="bi bi-inbox fs-1 text-muted mb-3"></i>
                                        <h5 class="text-muted">No hay recomendaciones disponibles en este momento</h5>
                                        <p class="text-muted">Comienza a seguir usuarios y colaborar en propuestas para
                                            recibir recomendaciones personalizadas</p>
                                        <a href="${pageContext.request.contextPath}/"
                                            class="btn btn-primary mt-3">Explorar Propuestas</a>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
                <script>window.CTX = '${pageContext.request.contextPath}';</script>
                <script src="${pageContext.request.contextPath}/js/busquedaAjax.js"></script>

                <style>
                    .hover-shadow:hover {
                        transform: translateY(-5px);
                        box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
                    }
                </style>
            </body>

            </html>