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
                        <a class="nav-link text-primary fw-normal"  href="${pageContext.request.contextPath}/propuestas/alta">Tengo una Propuesta</a>
                    </li>
                    <li class="nav-item d-flex align-items-center">
                        <span class="px-2 text-dark">|</span>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link text-primary fw-normal" href="${pageContext.request.contextPath}/propuestas/registrarColaboracionProp">Quiero ver Propuestas</a>
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

<div class="tabs mb-0">
    <ul class="nav nav-tabs border-bottom-0" role="tablist" id="proposalTabs">

        <li class="nav-item" role="presentation">
            <a class="nav-link active" data-bs-toggle="tab" href="#creadas" role="tab"
               data-estado="PUBLICADA">Propuestas Creadas</a>
        </li>
        <li class="nav-item" role="presentation">
            <a class="nav-link" data-bs-toggle="tab" href="#financiacion" role="tab"
               data-estado="ENFINANCIACION">Propuestas en Financiación</a>
        </li>
        <li class="nav-item" role="presentation">
            <a class="nav-link" data-bs-toggle="tab" href="#financiadas" role="tab"
               data-estado="FINANCIADA">Propuestas Financiadas</a>
        </li>
        <li class="nav-item" role="presentation">
            <a class="nav-link" data-bs-toggle="tab" href="#no-financiadas" role="tab"
               data-estado="NOFINANCIADA">Propuestas NO Financiadas</a>
        </li>
        <li class="nav-item" role="presentation">
            <a class="nav-link" data-bs-toggle="tab" href="#canceladas" role="tab"
               data-estado="CANCELADA">Propuestas Canceladas</a>
        </li>
    </ul>
</div>

<hr class="mt-0 mb-4" style="border-top: 2px solid #ced4da; opacity: 1;">

<div class="tab-content">
</div>

<!-- Lista las propuestas -->
 <section id="tarjetas" class="tarjetas"></section>

<!-- Categorías -->
<div id="categorias" class="categorias"></div>

    <script src="${pageContext.request.contextPath}/js/propuestasAndCategorias.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>