<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${propuesta.titulo} - Culturarte</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
</head>
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
                        <a class="nav-link text-primary fw-normal"  href="${pageContext.request.contextPath}/usuarios/buscar">Buscar usuarios</a>
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
                            <div class="rounded-circle bg-dark text-white d-flex justify-content-center align-items-center" style="width: 45px; height: 45px; flex-shrink: 0;">
                                <i class="bi bi-person-fill" style="font-size: 28px;"></i>
                            </div>

                            <div class="d-flex flex-column lh-sm">
                                <span style="font-size: 16px; color: #333;">${sessionScope.usuarioLogueado.nombre} ${sessionScope.usuarioLogueado.apellido}</span>

                                <div class="d-flex align-items-center" style="font-size: 13px;">
                                    <a href="${pageContext.request.contextPath}/usuarios/${sessionScope.usuarioLogueado.nickname}" class="text-primary text-decoration-underline me-1">Perfil</a>

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

<body class="bg-light">

<!-- Alertas para mensajes de éxito/error -->
<c:if test="${not empty mensajeExito}">
    <div class="alert alert-success alert-dismissible fade show m-3" role="alert">
        <i class="bi bi-check-circle-fill me-2"></i>${mensajeExito}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<div class="container-fluid py-4">
    <div class="row">
        <div class="col-lg-4 col-md-5 mb-4">
            <div class="card shadow-sm p-4">
                <div class="text-center">

                    <!-- Imagen de perfil -->
                    <c:choose>
                        <c:when test="${not empty perfilVisitado.imagen}">
                            <img src="${perfilVisitado.imagen}" class="rounded-circle mb-3"
                                 alt="Imagen de perfil" style="width: 130px; height: 130px; object-fit: cover;">
                        </c:when>
                        <c:otherwise>
                            <div class="d-flex justify-content-center align-items-center rounded-circle bg-light border mb-3 mx-auto"
                                 style="width: 130px; height: 130px;">
                                <i class="bi bi-person-circle fs-1 text-primary"></i>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <!-- Datos básicos -->
                    <h3 class="fw-bold mb-1">${perfilVisitado.nickname}</h3>
                    <span class="badge bg-secondary text-uppercase" style="font-size: 0.7em;">${perfilVisitado.tipo}</span>
                    <p class="text-muted mb-1">${perfilVisitado.nombre} ${perfilVisitado.apellido}</p>
                    <p class="text-secondary">${perfilVisitado.email}</p>
                    <p class="text-secondary">Nacido el: ${perfilVisitado.fechaNacimiento}</p>

                    <!-- Botón de seguir -->
                    <c:choose>
                        <c:when test="${usuarioLogueado.tipo eq 'visitante' || esMiPropioPerfil}">
                            <button type="button" class="btn btn-secondary btn-sm mt-2" disabled>
                                <i class="bi bi-person-plus"></i> Seguir
                            </button>
                        </c:when>

                        <c:when test="${loSigo}">
                            <form action="${pageContext.request.contextPath}/usuarios/dejarDeSeguir" method="post" class="d-inline-block mt-2">
                                <input type="hidden" name="nickSeguido" value="${perfilVisitado.nickname}"/>
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <button type="submit" class="btn btn-outline-danger btn-sm">
                                    <i class="bi bi-person-dash"></i> Dejar de seguir
                                </button>
                            </form>
                        </c:when>

                        <c:otherwise>
                            <form action="${pageContext.request.contextPath}/usuarios/seguir" method="post" class="d-inline-block mt-2">
                                <input type="hidden" name="nickSeguido" value="${perfilVisitado.nickname}"/>
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <button type="submit" class="btn btn-primary btn-sm">
                                    <i class="bi bi-person-plus"></i> Seguir
                                </button>
                            </form>
                        </c:otherwise>
                    </c:choose>

                </div>

                <hr>

                <!-- Botones de seguidores y seguidos -->
                <div class="d-flex justify-content-center gap-3 mt-2">
                    <button type="button" class="btn btn-outline-primary btn-sm" data-bs-toggle="modal" data-bs-target="#seguidoresModal">
                        Seguidores <span class="badge bg-primary">${perfilVisitado.usuariosSeguidores.size()}</span>
                    </button>
                    <button type="button" class="btn btn-outline-primary btn-sm" data-bs-toggle="modal" data-bs-target="#seguidosModal">
                        Seguidos <span class="badge bg-primary">${perfilVisitado.usuariosSeguidos.size()}</span>
                    </button>
                </div>
            </div>
        </div>

        <!-- Modal Seguidores -->
        <div class="modal fade" id="seguidoresModal" tabindex="-1" aria-labelledby="seguidoresModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-scrollable">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="seguidoresModalLabel">Seguidores</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                    </div>
                    <div class="modal-body">
                        <c:choose>
                            <c:when test="${not empty perfilVisitado.usuariosSeguidores}">
                                <ul class="list-group">
                                    <c:forEach var="seguidor" items="${perfilVisitado.usuariosSeguidores}">
                                        <li class="list-group-item d-flex justify-content-between align-items-center">
                                            <a href="${pageContext.request.contextPath}/usuarios/${seguidor.nickname}" class="text-decoration-none text-dark d-flex align-items-center">
                                                <i class="bi bi-person-circle me-2 text-primary"></i>
                                                <span class="fw-semibold">${seguidor.nickname}</span>
                                            </a>

                                            <!-- Botón de seguir -->
                                            <c:choose>
                                                <c:when test="${usuarioLogueado.tipo eq 'visitante' || usuarioLogueado.nickname eq seguidor.nickname}">
                                                    <button type="button" class="btn btn-secondary btn-sm mt-2" disabled>
                                                        <i class="bi bi-person-plus"></i> Seguir
                                                    </button>
                                                </c:when>

                                                <c:when test="${usuarioLogueado.buscarUsuarioSeguido(seguidor.nickname)}">
                                                    <form action="${pageContext.request.contextPath}/usuarios/dejarDeSeguir" method="post" class="d-inline-block mt-2">
                                                        <input type="hidden" name="nickSeguido" value="${seguidor.nickname}"/>
                                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                                        <button type="submit" class="btn btn-outline-danger btn-sm">
                                                            <i class="bi bi-person-dash"></i> Dejar de seguir
                                                        </button>
                                                    </form>
                                                </c:when>

                                                <c:otherwise>
                                                    <form action="${pageContext.request.contextPath}/usuarios/seguir" method="post" class="d-inline-block mt-2">
                                                        <input type="hidden" name="nickSeguido" value="${seguidor.nickname}"/>
                                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                                        <button type="submit" class="btn btn-primary btn-sm">
                                                            <i class="bi bi-person-plus"></i> Seguir
                                                        </button>
                                                    </form>
                                                </c:otherwise>
                                            </c:choose>
                                            <span class="badge bg-secondary">${seguidor.tipo}</span>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:when>
                            <c:otherwise>
                                <p class="text-center text-muted">Aún no tiene seguidores.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Seguidos -->
        <div class="modal fade" id="seguidosModal" tabindex="-1" aria-labelledby="seguidosModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-scrollable">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="seguidosModalLabel">Usuarios Seguidos</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                    </div>
                    <div class="modal-body">
                        <c:choose>
                            <c:when test="${not empty perfilVisitado.usuariosSeguidos}">
                                <ul class="list-group">
                                    <c:forEach var="seguido" items="${perfilVisitado.usuariosSeguidos}">
                                        <li class="list-group-item d-flex justify-content-between align-items-center">
                                            <a href="${pageContext.request.contextPath}/usuarios/${seguido.nickname}" class="text-decoration-none text-dark d-flex align-items-center">
                                                <i class="bi bi-person-circle me-2 text-primary"></i>
                                                <span class="fw-semibold">${seguido.nickname}</span>
                                            </a>
                                            <!-- Botón de seguir -->
                                            <c:choose>
                                                <c:when test="${usuarioLogueado.tipo eq 'visitante' || usuarioLogueado.nickname eq seguido.nickname}">
                                                    <button type="button" class="btn btn-secondary btn-sm mt-2" disabled>
                                                        <i class="bi bi-person-plus"></i> Seguir
                                                    </button>
                                                </c:when>

                                                <c:when test="${usuarioLogueado.buscarUsuarioSeguido(seguido.nickname)}">
                                                    <form action="${pageContext.request.contextPath}/usuarios/dejarDeSeguir" method="post" class="d-inline-block mt-2">
                                                        <input type="hidden" name="nickSeguido" value="${seguido.nickname}"/>
                                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                                        <button type="submit" class="btn btn-outline-danger btn-sm">
                                                            <i class="bi bi-person-dash"></i> Dejar de seguir
                                                        </button>
                                                    </form>
                                                </c:when>

                                                <c:otherwise>
                                                    <form action="${pageContext.request.contextPath}/usuarios/seguir" method="post" class="d-inline-block mt-2">
                                                        <input type="hidden" name="nickSeguido" value="${seguido.nickname}"/>
                                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                                        <button type="submit" class="btn btn-primary btn-sm">
                                                            <i class="bi bi-person-plus"></i> Seguir
                                                        </button>
                                                    </form>
                                                </c:otherwise>
                                            </c:choose>
                                            <span class="badge bg-secondary">${seguido.tipo}</span>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:when>
                            <c:otherwise>
                                <p class="text-center text-muted">No sigue a nadie aún.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>


        <div class="col-lg-8 col-md-7">
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-white">
                    <ul class="nav nav-tabs card-header-tabs" id="perfilTabs" role="tablist">
                        <li class="nav-item" role="presentation">
                            <button class="nav-link active" id="propuestasFav-tab" data-bs-toggle="tab" data-bs-target="#propuestasFav" type="button">Propuestas Favoritas</button>
                        </li>
                        <!-- Tabs de proponente -->
                        <c:if test="${not empty proponente}">
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="info-tab" data-bs-toggle="tab" data-bs-target="#info" type="button">Información</button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="publicadas-tab" data-bs-toggle="tab" data-bs-target="#propuestasCreadas" type="button">Propuestas Publicadas</button>
                            </li>
                            <c:if test="${esMiPropioPerfil}">
                                <li class="nav-item" role="presentation">
                                    <button class="nav-link" id="ingresadas-tab" data-bs-toggle="tab" data-bs-target="#propuestasIngresadas" type="button">Propuestas Ingresadas</button>
                                </li>
                            </c:if>
                        </c:if>
                        <!-- Tabs de colaborador -->
                        <c:if test="${not empty colaborador}">
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="colaborador-tab" data-bs-toggle="tab" data-bs-target="#colaboraciones" type="button">Colaboraciones</button>
                            </li>
                        </c:if>



                    </ul>
                </div>

                <div class="tab-content p-3" id="perfilTabsContent" style="min-height: 400px;">

                    <!-- Propuestas Seguidas -->
                    <div class="tab-pane fade show active" id="propuestasFav" role="tabpanel">
                        <h4 class="text-primary mb-3">Propuestas Favoritas</h4>
                        <c:choose>
                            <c:when test="${not empty perfilVisitado.propuestasSeguidas && perfilVisitado.propuestasSeguidas.size() > 0}">
                                <div class="row">
                                    <c:forEach var="propuesta" items="${perfilVisitado.propuestasSeguidas}">
                                        <div class="col-md-6 mb-3">
                                            <div class="card border-0 shadow-sm h-100">
                                                <div class="card-body text-center">
                                                    <img src="${p.imagen}" class="card-img-top" alt="${p.titulo}" style="height: 150px; object-fit: cover;">
                                                    <h5 class="card-title">${propuesta.titulo}</h5>
                                                    <p class="card-text text-muted">${propuesta.descripcion}</p>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-5">
                                    <i class="bi bi-people fs-1 text-muted mb-3"></i>
                                    <h5 class="text-muted">Aún no hay propuestas seguidas</h5>
                                </div>
                            </c:otherwise>
                        </c:choose>

                    </div>

                    <!-- Si es proponente -->
                    <c:if test="${not empty proponente}">
                        <!-- Info de proponente -->
                        <div class="tab-pane fade" id="info" role="tabpanel">
                            <h4 class="text-primary mb-3">Información del Proponente</h4>
                            <p><strong>Dirección:</strong> ${proponente.direccion}</p>
                            <p><strong>Biografía:</strong> ${proponente.biografia}</p>
                            <p><strong>Web:</strong> ${proponente.linkWeb}</p>
                        </div>

                        <!-- Propuestas Creadas -->
                        <div class="tab-pane fade" id="propuestasCreadas" role="tabpanel">
                            <h4 class="text-primary mb-3">Propuestas Publicadas</h4>
                            <c:choose>
                                <c:when test="${not empty proponente.propuestas && proponente.propuestas.size() > 0}">
                                    <div class="row g-3">
                                        <c:forEach var="propuesta" items="${proponente.propuestas}">
                                            <c:if test="${propuesta.estadoActual ne 'INGRESADA'}">
                                                <div class="col-md-6 col-lg-4">
                                                    <div class="card h-100 shadow-sm border-0 rounded-3 overflow-hidden hover-shadow">
                                                        <!-- Imagen de la propuesta -->
                                                        <c:choose>
                                                            <c:when test="${not empty propuesta.imagen}">
                                                                <img src="${propuesta.imagen}" class="card-img-top" alt="${propuesta.titulo}" style="height: 150px; object-fit: cover;">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <div class="d-flex justify-content-center align-items-center bg-light" style="height: 150px;">
                                                                    <i class="bi bi-image fs-1 text-muted"></i>
                                                                </div>
                                                            </c:otherwise>
                                                        </c:choose>

                                                        <div class="card-body">
                                                            <h6 class="card-title fw-bold mb-2" style="font-size: 14px;">${propuesta.titulo} - ${propuesta.estadoActual}</h6>
                                                            <p class="card-text text-muted mb-0" style="font-size: 12px;">
                                                                <c:choose>
                                                                    <c:when test="${fn:length(propuesta.descripcion) > 120}">
                                                                        ${propuesta.descripcion.substring(0, 120)}...
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        ${propuesta.descripcion}
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </p>
                                                        </div>

                                                        <div class="card-footer bg-white border-0 text-center">
                                                            <a href="${pageContext.request.contextPath}/propuestas/${propuesta.titulo}" class="btn btn-sm btn-primary w-100">Ver Propuesta</a>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:if>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="text-center py-5">
                                        <i class="bi bi-people fs-1 text-muted mb-3"></i>
                                        <h5 class="text-muted">Aún no hay propuestas publicadas</h5>
                                    </div>
                                </c:otherwise>
                            </c:choose>


                        </div>

                        <!-- Propuestas Ingresadas si soy el proponente -->
                        <c:if test="${esMiPropioPerfil}">
                            <div class="tab-pane fade" id="propuestasIngresadas" role="tabpanel">
                                <h4 class="text-primary mb-3">Propuestas Esperando Aprobación</h4>
                                <c:choose>
                                    <c:when test="${not empty proponente.propuestas && proponente.propuestas.size() > 0}">
                                        <div class="row g-3">
                                            <c:forEach var="propuesta" items="${proponente.propuestas}">
                                                <c:if test="${propuesta.estadoActual eq 'INGRESADA'}">
                                                    <div class="col-md-6 col-lg-4">
                                                        <div class="card h-100 shadow-sm border-0 rounded-3 overflow-hidden hover-shadow">
                                                            <!-- Imagen de la propuesta -->
                                                            <c:choose>
                                                                <c:when test="${not empty propuesta.imagen}">
                                                                    <img src="${propuesta.imagen}" class="card-img-top" alt="${propuesta.titulo}" style="height: 150px; object-fit: cover;">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <div class="d-flex justify-content-center align-items-center bg-light" style="height: 150px;">
                                                                        <i class="bi bi-image fs-1 text-muted"></i>
                                                                    </div>
                                                                </c:otherwise>
                                                            </c:choose>

                                                            <div class="card-body">
                                                                <h6 class="card-title fw-bold mb-2" style="font-size: 14px;">${propuesta.titulo} - ${propuesta.estadoActual}</h6>
                                                                <p class="card-text text-muted mb-0" style="font-size: 12px;">
                                                                    <c:choose>
                                                                        <c:when test="${fn:length(propuesta.descripcion) > 120}">
                                                                            ${propuesta.descripcion.substring(0, 120)}...
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            ${propuesta.descripcion}
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </p>
                                                            </div>

                                                            <div class="card-footer bg-white border-0 text-center">
                                                                <a href="${pageContext.request.contextPath}/propuestas/${propuesta.titulo}" class="btn btn-sm btn-primary w-100">Ver Propuesta</a>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:if>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-center py-5">
                                            <i class="bi bi-people fs-1 text-muted mb-3"></i>
                                            <h5 class="text-muted">Aún no hay propuestas publicadas</h5>
                                        </div>
                                    </c:otherwise>
                                </c:choose>


                            </div>
                        </c:if>
                    </c:if>

                    <!-- Propuestas Colaboradas -->
                    <c:if test="${not empty colaborador}">
                        <div class="tab-pane fade" id="colaboraciones" role="tabpanel">
                            <h4 class="text-primary mb-3">Propuestas a las que colaboró</h4>
                            <c:choose>
                                <c:when test="${not empty colaborador.colaboraciones && colaborador.colaboraciones.size() > 0}">
                                    <div class="row g-3">
                                        <c:forEach var="colaboracion" items="${colaborador.colaboraciones}">
                                            <c:if test="${colaboracion.propuesta.estadoActual ne 'INGRESADA'}">
                                                <div class="col-md-6 col-lg-4">
                                                    <div class="card h-100 shadow-sm border-0 rounded-3 overflow-hidden hover-shadow">
                                                        <!-- Imagen de la propuesta -->
                                                        <c:choose>
                                                            <c:when test="${not empty colaboracion.propuesta.imagen}">
                                                                <img src="${colaboracion.propuesta.imagen}" class="card-img-top" alt="${colaboracion.propuesta.titulo}" style="height: 150px; object-fit: cover;">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <div class="d-flex justify-content-center align-items-center bg-light" style="height: 150px;">
                                                                    <i class="bi bi-image fs-1 text-muted"></i>
                                                                </div>
                                                            </c:otherwise>
                                                        </c:choose>

                                                        <div class="card-body">
                                                            <h6 class="card-title fw-bold mb-2" style="font-size: 14px;">${colaboracion.propuesta.titulo} - ${colaboracion.propuesta.estadoActual}</h6>
                                                            <p class="card-text text-muted mb-2" style="font-size: 12px;">
                                                                <c:choose>
                                                                    <c:when test="${fn:length(colaboracion.propuesta.descripcion) > 120}">
                                                                        ${colaboracion.propuesta.descripcion.substring(0, 120)}...
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        ${colaboracion.propuesta.descripcion}
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </p>
                                                            <c:if test="${esMiPropioPerfil}">
                                                                <span></span>
                                                                <p class="card-text text-muted mb-0" style="font-size: 12px;"><strong>Fecha: </strong>${colaboracion.fecha}</p>
                                                                <p class="card-text text-muted mb-0" style="font-size: 12px;"><strong>Monto: </strong>${colaboracion.monto}</p>
                                                                <p class="card-text text-muted mb-0" style="font-size: 12px;"><strong>Tipo Retorno: </strong>${colaboracion.tipoRetorno}</p>
                                                            </c:if>
                                                        </div>

                                                        <div class="card-footer bg-white border-0 text-center">
                                                            <a href="${pageContext.request.contextPath}/propuestas/${colaboracion.propuesta.titulo}" class="btn btn-sm btn-primary w-100">Ver Propuesta</a>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:if>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="text-center py-5">
                                        <i class="bi bi-people fs-1 text-muted mb-3"></i>
                                        <h5 class="text-muted">Aún no hay colaboraciones</h5>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>

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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
