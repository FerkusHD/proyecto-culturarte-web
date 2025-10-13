<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${propuesta.titulo} - Culturarte</title>
    <link href="https://fonts.googleapis.com/css2?family=Pacifico&display=swap" rel="stylesheet">
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

                <!-- Buscador -->
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

                <!-- Usuario logueado / visitante -->
                <c:choose>
                    <c:when test="${sessionScope.usuarioLogueado.tipo ne 'visitante'}">
                        <div class="d-flex align-items-start gap-2">
                            <div class="rounded-circle bg-light text-primary d-flex justify-content-center align-items-center border"
                                 style="width: 45px; height: 45px; flex-shrink: 0;">
                                <c:choose>
                                    <c:when test="${not empty sessionScope.usuarioLogueado.imagen}">
                                        <img src="${sessionScope.usuarioLogueado.imagen}"
                                             alt="Foto perfil"
                                             class="rounded-circle"
                                             style="width: 45px; height: 45px; object-fit: cover;">
                                    </c:when>
                                    <c:otherwise>
                                        <i class="bi bi-person-circle fs-3"></i>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="d-flex flex-column lh-sm">
                                <span style="font-size: 16px; color: #333;">
                                    ${sessionScope.usuarioLogueado.nombre} ${sessionScope.usuarioLogueado.apellido}
                                </span>
                                <div class="d-flex align-items-center" style="font-size: 13px;">
                                    <a href="${pageContext.request.contextPath}/usuarios/perfil/${sessionScope.usuarioLogueado.nickname}"
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

<!-- PERFIL -->
<div class="container py-5">
    <div class="row">
        <!-- Columna izquierda -->
        <div class="col-md-4 mb-4">
            <div class="profile-card text-center p-4 bg-white rounded shadow-sm">

                <c:choose>
                    <c:when test="${not empty perfilVisitado.imagen}">
                        <img src="${perfilVisitado.imagen}"
                             class="profile-img mb-3 rounded-circle"
                             alt="Imagen de perfil"
                             style="width: 130px; height: 130px; object-fit: cover;">
                    </c:when>
                    <c:otherwise>
                        <div class="d-flex justify-content-center align-items-center rounded-circle bg-light border mb-3"
                             style="width: 130px; height: 130px;">
                            <i class="bi bi-person-circle fs-1 text-primary"></i>
                        </div>
                    </c:otherwise>
                </c:choose>

                <h4>${perfilVisitado.nombre} ${perfilVisitado.apellido}</h4>
                <p class="text-muted">${perfilVisitado.tipo}</p>

                <c:if test="${!esMiPropioPerfil}">
                    <button class="btn btn-primary btn-sm mb-2">Seguir</button>
                </c:if>

                <div class="info-box mt-3 text-start">
                    <p><strong>Nickname:</strong> ${perfilVisitado.nickname}</p>
                    <p><strong>Email:</strong> ${perfilVisitado.email}</p>
                    <p><strong>Fecha de Nacimiento:</strong> ${perfilVisitado.fechaNacimiento}</p>
                </div>
            </div>
        </div>

        <!-- Columna derecha -->
        <div class="col-md-8">
            <!-- Seguidores y seguidos -->
            <div class="row mb-4">
                <div class="col-md-6">
                    <div class="info-box bg-white p-3 rounded shadow-sm">
                        <p class="fw-bold">Seguidores</p>
                        <ul class="list-unstyled mb-0">
                            <c:forEach var="seguidor" items="${perfilVisitado.usuariosSeguidores}">
                                <li>
                                    <a href="/usuarios/${seguidor.nickname}">${seguidor.nickname}</a>
                                    <span class="text-muted small">(${seguidor.tipo})</span>
                                </li>
                            </c:forEach>
                            <c:if test="${empty perfilVisitado.usuariosSeguidores}">
                                <li class="text-muted">Sin seguidores</li>
                            </c:if>
                        </ul>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="info-box bg-white p-3 rounded shadow-sm">
                        <p class="fw-bold">Siguiendo</p>
                        <ul class="list-unstyled mb-0">
                            <c:forEach var="seguido" items="${perfilVisitado.usuariosSeguidos}">
                                <li>
                                    <a href="/usuarios/${seguido.nickname}">${seguido.nickname}</a>
                                    <span class="text-muted small">(${seguido.tipo})</span>
                                </li>
                            </c:forEach>
                            <c:if test="${empty perfilVisitado.usuariosSeguidos}">
                                <li class="text-muted">No sigue a nadie</li>
                            </c:if>
                        </ul>
                    </div>
                </div>
            </div>

            <!-- Propuestas -->
            <div class="info-box bg-white p-3 rounded shadow-sm">
                <p class="fw-bold">Propuestas</p>

                <c:choose>
                    <c:when test="${perfilVisitado.tipo eq 'Proponente'}">
                        <h6>Publicadas (no “Ingresada”):</h6>
                        <c:forEach var="prop" items="${perfilVisitado.propuestasPublicadas}">
                            <div><a href="/propuestas/${prop.id}" class="text-decoration-none">${prop.titulo}</a></div>
                        </c:forEach>

                        <c:if test="${esMiPropioPerfil}">
                            <hr>
                            <h6>Mis propuestas ingresadas:</h6>
                            <c:forEach var="prop" items="${perfilVisitado.propuestasIngresadas}">
                                <div class="text-muted">${prop.titulo} <span class="small">(Ingresada)</span></div>
                            </c:forEach>
                        </c:if>
                    </c:when>

                    <c:when test="${perfilVisitado.tipo eq 'Colaborador'}">
                        <h6>Propuestas con las que colaboró:</h6>
                        <c:forEach var="colab" items="${perfilVisitado.colaboraciones}">
                            <div>
                                <a href="/propuestas/${colab.propuesta.id}" class="text-decoration-none">${colab.propuesta.titulo}</a>
                                <c:if test="${esMiPropioPerfil}">
                                    <div class="small text-muted">Monto: $${colab.monto} | Fecha: ${colab.fecha}</div>
                                </c:if>
                            </div>
                        </c:forEach>
                    </c:when>

                    <c:otherwise>
                        <p class="text-muted">Los visitantes no tienen propuestas.</p>
                    </c:otherwise>
                </c:choose>

                <hr>
                <h6>Favoritos:</h6>
                <c:forEach var="fav" items="${perfilVisitado.propuestasSeguidas}">
                    <div><a href="/propuestas/${fav.id}" class="text-decoration-none">${fav.titulo}</a></div>
                </c:forEach>
                <c:if test="${empty perfilVisitado.propuestasSeguidas}">
                    <p class="text-muted">Sin propuestas favoritas</p>
                </c:if>
            </div>
        </div>
    </div>
</div>

</body>
</html>
