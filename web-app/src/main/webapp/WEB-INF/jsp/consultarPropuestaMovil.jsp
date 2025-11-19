<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${propuesta.titulo} - Culturarte</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
</head>
<body class="bg-light">
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

                    <c:if test="${sessionScope.usuarioLogueado.tipo eq 'proponente'}">
                        <li class="nav-item d-flex align-items-center"><span class="px-2 text-dark">|</span></li>
                        <li class="nav-item">
                            <a class="nav-link text-primary fw-normal" href="${pageContext.request.contextPath}/propuestas/alta">Tengo una propuesta</a>
                        </li>
                    </c:if>
                </ul>

                <form class="d-flex me-3 flex-grow-1 position-relative" style="max-width: 400px;"
                      action="${pageContext.request.contextPath}/propuestas/buscar" method="get">
                    <input class="form-control form-control-sm me-2 w-100" type="search"
                           name="query" placeholder="Título, descripción, lugar"
                           autocomplete="off" value="${query != null ? query : ''}" />
                    <button class="btn btn-sm btn-outline-primary" type="submit">Buscar</button>
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
                                <span style="font-size: 16px; color: #333;">
                                    ${sessionScope.usuarioLogueado.nombre} ${sessionScope.usuarioLogueado.apellido}
                                </span>
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

<div class="container-fluid py-4">
    <div class="row mb-4">
        <div class="col-12 text-center">
            <h1 class="fw-bold" style="text-decoration: underline;">${propuesta.titulo}</h1>
        </div>
    </div>

    <div class="row">
        <!-- Columna izquierda -->
        <div class="col-lg-4 col-md-5 mb-4">
            <c:if test="${not empty propuesta.imagen}">
                <div class="card shadow-sm">
                    <img src="${pageContext.request.contextPath}/${propuesta.imagen}" class="card-img-top" alt="${propuesta.titulo}">
                    <div class="card-body text-center">
                        <h5 class="card-title">${propuesta.titulo}</h5>
                        <span class="badge
                            <c:choose>
                                <c:when test="${propuesta.estadoActual.toString() == 'CONFIRMADA'}">bg-success</c:when>
                                <c:when test="${propuesta.estadoActual.toString() == 'PUBLICADA'}">bg-primary</c:when>
                                <c:when test="${propuesta.estadoActual.toString() == 'FINALIZADA'}">bg-secondary</c:when>
                                <c:when test="${propuesta.estadoActual.toString() == 'CANCELADA'}">bg-danger</c:when>
                                <c:otherwise>bg-warning</c:otherwise>
                            </c:choose> rounded-pill">
                            ${propuesta.estadoActual}
                        </span>
                    </div>
                </div>
            </c:if>

            <div class="card mt-3 shadow-sm">
                <div class="card-header bg-primary text-white">
                    <h6 class="mb-0">Información General</h6>
                </div>
                <div class="card-body">
                    <div class="d-flex justify-content-between mb-2">
                        <span><i class="bi bi-calendar-event text-primary"></i> Fecha:</span>
                        <strong>${propuesta.fechaPrevista}</strong>
                    </div>
                    <c:if test="${not empty propuesta.lugar}">
                        <div class="d-flex justify-content-between mb-2">
                            <span><i class="bi bi-geo-alt text-primary"></i> Lugar:</span>
                            <strong>${propuesta.lugar}</strong>
                        </div>
                    </c:if>
                    <div class="d-flex justify-content-between mb-2">
                        <span><i class="bi bi-ticket-perforated text-primary"></i> Entrada:</span>
                        <strong>$${propuesta.montoEntrada}</strong>
                    </div>
                    <div class="d-flex justify-content-between">
                        <span><i class="bi bi-tags text-primary"></i> Categoría:</span>
                        <strong>${propuesta.categoria}</strong>
                    </div>
                </div>
            </div>

            <div class="card mt-3 shadow-sm">
                <div class="card-header bg-primary text-white">
                    <h6 class="mb-0"><i class="bi bi-person-badge"></i> Proponente</h6>
                </div>
                <div class="card-body text-center">
                    <h4 class="text-primary mb-1">${propuesta.proponente}</h4>
                    <p class="text-muted">Creador de esta propuesta</p>
                </div>
            </div>
        </div>

        <!-- Columna derecha -->
        <div class="col-lg-8 col-md-7">
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-white">
                    <ul class="nav nav-tabs card-header-tabs">
                        <li class="nav-item"><button class="nav-link active" data-bs-toggle="tab" data-bs-target="#descripcion" type="button">Descripción</button></li>
                        <li class="nav-item"><button class="nav-link" data-bs-toggle="tab" data-bs-target="#colaboradores" type="button">Colaboradores</button></li>
                        <li class="nav-item"><button class="nav-link" data-bs-toggle="tab" data-bs-target="#financiacion" type="button">Financiación</button></li>
                    </ul>
                </div>

                <div class="card-body p-0">
                    <div class="tab-content p-3" style="min-height: 400px;">

                        <!-- DESCRIPCIÓN -->
                        <div class="tab-pane fade show active" id="descripcion">
                            <h4 class="text-primary mb-3">Descripción de la Propuesta</h4>
                            <div class="p-3 rounded bg-light border-start border-4 border-primary">
                                <p class="mb-0 lh-base">${propuesta.descripcion}</p>
                            </div>
                        </div>

                        <!-- COLABORADORES -->
                        <div class="tab-pane fade" id="colaboradores">
                            <h4 class="text-primary mb-3">Colaboradores</h4>
                            <c:choose>
                                <c:when test="${not empty colaboradores}">
                                    <div class="row">
                                        <c:forEach var="colaborador" items="${colaboradores}">
                                            <div class="col-md-6 mb-3">
                                                <a href="${pageContext.request.contextPath}/usuarios/${colaborador.nickname}" style="text-decoration: none; color: inherit;">
                                                    <div class="card h-100 shadow-sm border-0 rounded-3 overflow-hidden hover-shadow">
                                                        <div class="card-body text-center">
                                                            <c:choose>
                                                                <c:when test="${not empty colaborador.imagen}">
                                                                    <img src="${pageContext.request.contextPath}/${colaborador.imagen}"
                                                                         class="rounded-circle mb-3"
                                                                         style="width: 80px; height: 80px; object-fit: cover;">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <img src="${pageContext.request.contextPath}/uploads/imagenes/noimgperfil.jpg"
                                                                         class="rounded-circle mb-3"
                                                                         style="width: 80px; height: 80px; object-fit: cover;">
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <h5 class="card-title mb-0">${colaborador.nombre} ${colaborador.apellido}</h5>
                                                            <p class="text-muted mb-1">@${colaborador.nickname}</p>
                                                            <p class="card-text text-muted">Colaborador</p>
                                                        </div>
                                                    </div>
                                                </a>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="text-center py-5">
                                        <i class="bi bi-people fs-1 text-muted mb-3"></i>
                                        <h5 class="text-muted">Aún no hay colaboradores</h5>
                                        <p class="text-muted">Sé el primero en colaborar</p>
                                        <c:if test="${sessionScope.usuarioLogueado.tipo eq 'colaborador'}">
                                            <a href="${pageContext.request.contextPath}/propuestas/registroColaboracion?titulo=${propuesta.titulo}"
                                               class="btn btn-success btn-lg mt-3">
                                                Ser el primer colaborador
                                            </a>
                                        </c:if>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- FINANCIACIÓN -->
                        <div class="tab-pane fade" id="financiacion">
                            <h4 class="text-primary mb-3">Estado de Financiación</h4>

                            <div class="alert alert-success text-center fw-bold">
                                Monto total recaudado: $${propuesta.montoRecaudado}
                            </div>

                            <div class="row text-center">
                                <div class="col-md-4 mb-3">
                                    <div class="card border-0 bg-light">
                                        <div class="card-body">
                                            <h6 class="text-muted">Monto Solicitado</h6>
                                            <h3 class="text-dark">$${propuesta.montoNecesario}</h3>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <div class="card border-0 bg-success text-white">
                                        <div class="card-body">
                                            <h6>Monto Recaudado</h6>
                                            <h3>$${propuesta.montoRecaudado}</h3>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-4 mb-3">
                                    <div class="card border-0 bg-info text-white">
                                        <div class="card-body">
                                            <h6>Progreso</h6>
                                            <c:set var="porcentaje" value="${(propuesta.montoRecaudado / propuesta.montoNecesario) * 100}" />
                                            <h3><fmt:formatNumber value="${porcentaje}" pattern="#.##"/>%</h3>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="mt-3">
                                <div class="progress" style="height: 25px;">
                                    <div class="progress-bar bg-success" role="progressbar"
                                         style="width: ${porcentaje}%;"
                                         aria-valuenow="${porcentaje}" aria-valuemin="0" aria-valuemax="100">
                                        <fmt:formatNumber value="${porcentaje}" pattern="#.##"/>% Completado
                                    </div>
                                </div>
                            </div>

                            <c:if test="${sessionScope.usuarioLogueado.tipo eq 'colaborador' && not haColaborado}">
                                <div class="text-center mt-4">
                                    <a href="${pageContext.request.contextPath}/propuestas/registroColaboracion?titulo=${propuesta.titulo}"
                                       class="btn btn-success btn-lg">
                                        Colaborar con esta propuesta
                                    </a>
                                </div>
                            </c:if>
                        </div>

                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
    .hover-shadow:hover {
        box-shadow: 0 0.5rem 1rem rgba(0,0,0,0.15) !important;
        transform: translateY(-3px);
        transition: all 0.3s ease-in-out;
    }
</style>

<script src="${pageContext.request.contextPath}/js/busquedaAjax.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
