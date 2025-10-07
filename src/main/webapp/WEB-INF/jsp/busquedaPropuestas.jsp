<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <title>Resultados de búsqueda - Culturarte</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/principal.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Pacifico&display=swap" rel="stylesheet">
    <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom">
        <div class="container-fluid">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">Culturarte</a>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarContent">
                <span class="navbar-toggler-icon"></span>
            </button>
        </div>
    </nav>
</head>

<body class="bg-light">
<div class="container mt-4">

    <h2 class="mb-3">Resultados de búsqueda (${resultados.size()})</h2>

    <form class="row g-3 align-items-center mb-4"
          action="${pageContext.request.contextPath}/propuestas/buscar"
          method="get">

        <div class="col-md-5">
            <input type="text" name="query" class="form-control"
                   placeholder="Título, descripción, lugar"
                   value="${query}" />
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

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
