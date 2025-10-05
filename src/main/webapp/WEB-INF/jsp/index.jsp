<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<title>Culturarte</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/principal.css">

<link href="https://fonts.googleapis.com/css2?family=Pacifico&display=swap" rel="stylesheet">
</head>

<body>
<header>
    <div class="culturarte">Culturarte</div>
    <nav>
        <a href="#">Tengo una Propuesta</a>
        <a href="#">Quiero ver Propuestas</a>
        <input type="text" placeholder="Título, descripción, lugar">
        <button>Buscar</button>


        <c:choose>
            <c:when test="${not empty sessionScope.usuarioLogueado}">
                Bienvenido ${sessionScope.usuarioLogueado.nombre} (${sessionScope.usuarioLogueado.tipo})
            </c:when>
            <c:otherwise>
                <p>Estás viendo el contenido como visitante.</p>
                <button onclick="window.location.href='${pageContext.request.contextPath}/login'">Iniciar sesión</button>
                <button onclick="window.location.href='${pageContext.request.contextPath}/usuarios/alta'">Registrarse</button>
            </c:otherwise>
        </c:choose>

        <c:if test="${not empty sessionScope.usuarioLogueado}">
            <a href="${pageContext.request.contextPath}/logout">Cerrar sesión</a>
        </c:if>

    </nav>
</header>

<!-- Pestañas -->
<div class="tabs">
    <button>Propuestas Creadas</button>
    <button>Propuestas en Financiación</button>
    <button>Propuestas Financiadas</button>
    <button>Propuestas No Financiadas</button>
    <button>Propuestas Canceladas</button>
</div>

<!-- Lista las propuestas -->
 <section id="tarjetas" class="tarjetas"></section>

<!-- Categorías -->
<div id="categorias" class="categorias"></div>

  <script src="${pageContext.request.contextPath}/js/propuestasAndCategorias.js"></script>

</body>
</html>