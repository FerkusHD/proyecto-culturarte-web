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
                        <a class="nav-link text-primary fw-normal"  href="${pageContext.request.contextPath}/usuarios/perfiles">Buscar usuarios</a>
                    </li>
                    <c:if test="${sessionScope.usuarioLogueado.tipo eq 'proponente'}">
                        <li class="nav-item d-flex align-items-center">
                            <span class="px-2 text-dark">|</span>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link text-primary fw-normal"  href="${pageContext.request.contextPath}/propuestas/alta">Tengo una propuesta</a>
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
                            <c:choose>
                                <c:when test="${not empty sessionScope.usuarioLogueado.imagen}">
                                    <img src="${pageContext.request.contextPath}/${sessionScope.usuarioLogueado.imagen}"
                                         class="rounded-circle"
                                         alt="Usuario" style="width: 45px; height: 45px; object-fit: cover;">
                                </c:when>
                                <c:otherwise>
                                    <div class="rounded-circle bg-dark text-white d-flex justify-content-center align-items-center" style="width: 45px; height: 45px;">
                                        <i class="bi bi-person-fill" style="font-size: 28px;"></i>
                                    </div>
                                </c:otherwise>
                            </c:choose>

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

<!-- Alertas para mensajes de éxito/error -->
<c:if test="${not empty mensajeExito}">
    <div class="alert alert-success alert-dismissible fade show m-3" role="alert">
        <i class="bi bi-check-circle-fill me-2"></i>${mensajeExito}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<c:if test="${not empty mensajeError}">
    <div class="alert alert-danger alert-dismissible fade show m-3" role="alert">
        <i class="bi bi-exclamation-triangle-fill me-2"></i>${mensajeError}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<div class="container-fluid py-4">
    <div class="row mb-4">
        <div class="col-12 text-center">
            <h1 class="fw-bold" style="text-decoration: underline;">${propuesta.titulo}</h1>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-4 col-md-5 mb-4">
            <c:if test="${not empty propuesta.imagen}">
                <div class="card shadow-sm">
                    <img src="${pageContext.request.contextPath}/${propuesta.imagen}"
                         class="card-img-top propuesta-img"
                         alt="${propuesta.titulo}">
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
                    <div class="d-flex justify-content-between mb-2">
                        <span><i class="bi bi-geo-alt text-primary"></i> Lugar:</span>
                        <strong>${propuesta.lugar}</strong>
                    </div>
                    <div class="d-flex justify-content-between mb-2">
                        <span><i class="bi bi-geo-alt text-primary"></i> Estado:</span>
                        <strong>${propuesta.estadoActual}</strong>
                    </div>
                    <div class="d-flex justify-content-between mb-2">
                        <span><i class="bi bi-ticket-perforated text-primary"></i> Entrada:</span>
                        <strong>$${propuesta.precioEntrada}</strong>
                    </div>
                    <div class="d-flex justify-content-between">
                        <span><i class="bi bi-tags text-primary"></i> Categoría:</span>
                        <strong>${propuesta.categoria}</strong>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-lg-8 col-md-7">
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-white">
                    <ul class="nav nav-tabs card-header-tabs" id="propuestaTabs" role="tablist">
                        <li class="nav-item" role="presentation">
                            <button class="nav-link active" id="descripcion-tab" data-bs-toggle="tab"
                                    data-bs-target="#descripcion" type="button" role="tab">Descripción
                            </button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="colaboradores-tab" data-bs-toggle="tab"
                                    data-bs-target="#colaboradores" type="button" role="tab">Colaboradores
                            </button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="financiacion-tab" data-bs-toggle="tab"
                                    data-bs-target="#financiacion" type="button" role="tab">Financiación
                            </button>
                        </li>
                        <li class="nav-item" role="presentation">
                            <button class="nav-link" id="comentarios-tab" data-bs-toggle="tab"
                                    data-bs-target="#comentarios" type="button" role="tab">Comentarios
                            </button>
                        </li>
                    </ul>
                </div>

                <div class="card-body p-0">
                    <div class="tab-content p-3" id="propuestaTabsContent" style="min-height: 400px;">
                        <div class="tab-pane fade show active" id="descripcion" role="tabpanel">
                            <h4 class="text-primary mb-3">Descripción de la Propuesta</h4>
                            <div class="p-3 rounded bg-light border-start border-4 border-primary">
                                <p class="mb-0 lh-base">${propuesta.descripcion}</p>
                            </div>
                        </div>

                        <div class="tab-pane fade" id="colaboradores" role="tabpanel">
                            <h4 class="text-primary mb-3">Colaboradores de la Propuesta</h4>
                            <c:choose>
                                <c:when test="${not empty propuesta.colaboradores && propuesta.colaboradores.size() > 0}">
                                    <div class="row">
                                        <c:forEach var="colaborador" items="${propuesta.colaboradores}">
                                            <div class="col-md-6 mb-3">
                                                <div class="card border-0 shadow-sm h-100">
                                                    <div class="card-body text-center">
                                                        <i class="bi bi-person-circle fs-1 text-primary mb-3"></i>
                                                        <h5 class="card-title">${colaborador}</h5>
                                                        <p class="card-text text-muted">Colaborador</p>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="text-center py-5">
                                        <i class="bi bi-people fs-1 text-muted mb-3"></i>
                                        <h5 class="text-muted">Aún no hay colaboradores para esta propuesta</h5>
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

                        <div class="tab-pane fade" id="financiacion" role="tabpanel">
                            <h4 class="text-primary mb-3">Estado de Financiación</h4>
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
                                            <h3>
                                                <c:set var="porcentaje" value="${(propuesta.montoRecaudado / propuesta.montoNecesario) * 100}"/>
                                                <fmt:formatNumber value="${porcentaje}" pattern="#.##"/>%
                                            </h3>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="mt-3">
                                <div class="progress" style="height: 25px;">
                                    <div class="progress-bar bg-success"
                                         role="progressbar"
                                         style="width: ${porcentaje}%;"
                                         aria-valuenow="${porcentaje}"
                                         aria-valuemin="0"
                                         aria-valuemax="100">
                                        <fmt:formatNumber value="${porcentaje}" pattern="#.##"/>% Completado
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="tab-pane fade" id="comentarios" role="tabpanel">
                            <h4 class="text-primary mb-3">Comentarios</h4>

                            <!-- Formulario para agregar comentario -->
                            <c:if test="${sessionScope.usuarioLogueado.tipo eq 'colaborador' && propuesta.colaboradores.contains(sessionScope.usuarioLogueado.nickname)}">
                                <div class="card mb-4 border-primary">
                                    <div class="card-header bg-primary text-white">
                                        <h6 class="mb-0"><i class="bi bi-chat-left-text"></i> Agregar Comentario</h6>
                                    </div>
                                    <div class="card-body">
                                        <form action="${pageContext.request.contextPath}/propuestas/agregarComentario" method="post">
                                            <input type="hidden" name="tituloPropuesta" value="${propuesta.titulo}">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                            <div class="mb-3">
                                                <label for="comentarioTexto" class="form-label">Tu comentario:</label>
                                                <textarea class="form-control" id="comentarioTexto" name="texto" rows="3" maxlength="500" placeholder="Escribe tu comentario aquí..." required></textarea>
                                            </div>
                                            <button type="submit" class="btn btn-primary">
                                                <i class="bi bi-send"></i> Publicar Comentario
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </c:if>

                            <!-- Lista de comentarios -->
                            <div class="comentarios-lista">
                                <c:choose>
                                    <c:when test="${not empty propuesta.comentarios && propuesta.comentarios.size() > 0}">
                                        <c:forEach var="comentario" items="${propuesta.comentarios}">
                                            <div class="card mb-3 border-light shadow-sm">
                                                <div class="card-body">
                                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                                        <div class="d-flex align-items-center">
                                                            <i class="bi bi-person-circle text-primary me-2 fs-5"></i>
                                                            <strong class="me-2">${comentario.colaborador}</strong>
                                                            <span class="text-muted small">
                                                                ${comentario.fecha}
                                                            </span>
                                                        </div>
                                                    </div>
                                                    <p class="card-text mb-0">${comentario.texto}</p>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-center py-5">
                                            <i class="bi bi-chat-left-text fs-1 text-muted mb-3"></i>
                                            <h5 class="text-muted">Aún no hay comentarios</h5>
                                            <p class="text-muted">Sé el primero en comentar sobre esta propuesta</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <c:if test="${sessionScope.usuarioLogueado.tipo eq 'colaborador' && propuesta.estadoActual ne 'CANCELADA'}">
                <div class="text-center mb-3">
                    <a href="${pageContext.request.contextPath}/propuestas/registroColaboracion?titulo=${propuesta.titulo}"
                    class="btn btn-success btn-lg">Colaborar con esta propuesta</a>
                </div>
            </c:if>

            <c:if test="${sessionScope.usuarioLogueado.tipo eq 'proponente' && sessionScope.usuarioLogueado.nickname eq propuesta.proponente && propuesta.estadoActual ne 'CANCELADA'}">
                <div class="text-center mb-3">
                    <form method="post" action="/propuestas/cancelar/${propuesta.titulo}">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <button type="submit" class="btn btn-danger btn-lg"
                                onclick="return confirm('¿Estás seguro de cancelar ${propuesta.titulo}?')">
                            <i class="bi bi-x-circle"></i> Cancelar Propuesta
                        </button>
                    </form>
                </div>

                <c:if test="${propuesta.estadoActual.toString() eq 'PUBLICADA' || propuesta.estadoActual.toString() eq 'EN_FINANCIACION'}">
                    <div class="text-center">
                         <form method="post" action="/propuestas/extender/${propuesta.titulo}">
                             <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                             <button type="submit" class="btn btn-warning btn-lg"
                                     onclick="return confirm('¿Estás seguro de extender la fecha prevista de ${propuesta.titulo} por 30 días?')">
                                 <i class="bi bi-clock-history"></i> Extender Financiacion 30 dias
                             </button>
                         </form>
                     </div>
                </c:if>
            </c:if>

            <div class="text-center mb-3">
                <a class="btn btn-outline-danger">
                    <i class="bi bi-heart"></i> Agregar a Favoritos
                </a>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>