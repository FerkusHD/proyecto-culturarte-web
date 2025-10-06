<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div id="perfil-info">
    <h1>Perfil de ${perfilVisitado.nickname}</h1>

    <p>Nombre: ${perfilVisitado.nombre}</p>
    <p>Bio: ${perfilVisitado.apellido}</p>

    <hr>

    <c:if test="${sessionScope.usuarioLogueado.nickname eq perfilVisitado.nickname}">
        <h2>Funciones de Mi Perfil</h2>

        <a href="/perfil/editar" class="btn btn-primary">Editar Perfil</a>

        <h3>Mis Propuestas Creadas:</h3>
        <h3>Mi Historial de Colaboraciones:</h3>
    </c:if>

    <c:if test="${!esMiPropioPerfil}">
        <h2>Propuestas de ${perfilVisitado.nickname}</h2>
        <a href="/contacto/${perfilVisitado.nickname}" class="btn btn-secondary">Contactar</a>
    </c:if>

</div>