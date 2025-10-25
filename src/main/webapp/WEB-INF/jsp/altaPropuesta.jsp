<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Crear Propuesta - Culturarte</title>
    <link href="https://fonts.googleapis.com/css2?family=Pacifico&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

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

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <div class="text-center mb-4">
                        <h1 class="h3">Crear Nueva Propuesta</h1>
                        <p class="text-muted">Complete todos los campos para crear su propuesta</p>
                    </div>

                    <form action="${pageContext.request.contextPath}/propuestas/alta" method="post" enctype="multipart/form-data"
                    >
                        <div class="mb-3">
                            <label for="titulo" class="form-label">Título</label>
                            <input id="titulo" name="titulo" type="text" maxlength="50" class="form-control" value="${titulo != null ? titulo : ''}" required>
                        </div>

                        <div class="mb-3">
                            <label for="descripcion" class="form-label">Descripción</label>
                            <textarea id="descripcion" name="descripcion" maxlength="500" class="form-control" rows="4" required>${descripcion != null ? descripcion : ''}</textarea>
                        </div>

                        <div class="mb-3">
                            <label for="lugar" class="form-label">Lugar</label>
                            <input id="lugar" name="lugar" type="text" maxlength="100" class="form-control" value="${lugar != null ? lugar : ''}" required>
                        </div>

                        <div class="mb-3">
                            <label for="fecha" class="form-label">Fecha</label>
                            <input id="fecha" name="fechaPrevista" type="date" class="form-control" value="${date}" required>
                        </div>

                        <div class="mb-3">
                            <label for="categoria" class="form-label">Categoría</label>
                            <select id="categoria" name="categoria" class="form-select" required>
                                <option value="" selected disabled>Selecciona una categoría</option>
                                <c:forEach var="cat" items="${categorias}">
                                    <option value="${cat}">${cat}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label for="tiposRetorno" class="form-label">Tipo de retorno</label>
                            <select class="form-select" id="tiposRetorno" name="tiposRetorno[]" multiple required>
                                <option value="" selected disabled>Selecciona un tipo de retorno</option>
                                <c:forEach var="tipo" items="${tiposRetorno}">
                                    <option value="${tipo}">${tipo}</option>
                                </c:forEach>
                            </select>
                            <div class="form-text">Mantén presionada la tecla Ctrl (o Cmd en Mac) para seleccionar múltiples opciones</div>
                        </div>

                        <div class="mb-3">
                            <label for="montoEntrada" class="form-label">Precio por Entrada (UYU)</label>
                            <input id="montoEntrada" name="montoEntrada" type="number" min="1" class="form-control" value="${montoEntrada != null ? montoEntrada : ''}" required>
                        </div>

                        <div class="mb-3">
                            <label for="montoNecesario" class="form-label">Monto Total Necesario (UYU)</label>
                            <input id="montoNecesario" name="montoNecesario" type="number" min="1" class="form-control" value="${montoNecesario != null ? montoNecesario : ''}" required>
                        </div>

                        <div class="mb-3">
                            <label for="imagen" class="form-label">Subir imagen:</label>
                            <input type="file" id="imagen" name="imagenFile" accept="image/*" class="form-control">
                        </div>

                        <c:if test="${not empty mensaje}">
                            <div class="alert alert-info" role="alert">
                                ${mensaje}
                            </div>
                        </c:if>

                        <button type="submit" class="btn btn-success w-100 py-2">Crear Propuesta</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/busquedaAjax.js"></script>
</body>
</html>