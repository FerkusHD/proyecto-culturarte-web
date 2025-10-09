<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="esMiPropioPerfil" value="${usuarioLogueado != null && usuarioLogueado.nickname == perfilVisitado.nickname}" />

<div class="container mt-4">
    <div class="row">

        <div class="col-md-4 text-center">
            <div class="card p-3 mb-4">

                <img src="${perfilVisitado.imagen}" alt="Imagen de perfil" class="img-fluid rounded-circle mx-auto mb-3" style="width: 150px; height: 150px; object-fit: cover;">

                <h2 class="h4">${perfilVisitado.nickname}</h2>
                <p class="text-muted">${perfilVisitado.tipo}</p>

                <c:if test="${!esMiPropioPerfil}">
                    <button class="btn btn-primary btn-sm mt-2">Seguir</button>
                </c:if>

                <hr>

                <c:if test="${esMiPropioPerfil}">
                    <div class="d-flex justify-content-center gap-3">
                        <a href="/perfil/editar" class="text-primary">Editar Perfil</a>
                        <span class="text-secondary">|</span>
                        <a href="/logout" class="text-danger">Cerrar Sesión</a>
                    </div>
                </c:if>

            </div>

            <div class="card p-3">
                <h5 class="text-start mb-3">Conexiones</h5>

<%--                <div class="text-start mb-3">--%>
<%--                    <p class="fw-bold mb-1">Seguidores (${perfilVisitado.seguidores.size()})</p>--%>
<%--                    <c:forEach var="s" items="${perfilVisitado.seguidores}" begin="0" end="4">--%>
<%--                        <a href="/perfil/${s.nickname}" class="badge bg-secondary text-decoration-none">${s.nickname} (${s.tipo})</a>--%>
<%--                    </c:forEach>--%>
<%--                    <c:if test="${perfilVisitado.seguidores.size() > 5}">...</c:if>--%>
<%--                </div>--%>

                <div class="text-start">
                    <p class="fw-bold mb-1">Siguiendo (${perfilVisitado.usuariosSeguidos.size()})</p>
                    <c:forEach var="s" items="${perfilVisitado.usuariosSeguidos}" begin="0" end="4">
                        <a href="/perfil/${s.nickname}" class="badge bg-secondary text-decoration-none">${s.nickname} (${s.tipo})</a>
                    </c:forEach>
                    <c:if test="${perfilVisitado.usuariosSeguidos.size() > 5}">...</c:if>
                </div>
            </div>

        </div>

        <div class="col-md-8">

<%--            <div class="mb-5">--%>
<%--                <h3 class="border-bottom pb-2">Propuestas Favoritas</h3>--%>
<%--                <c:choose>--%>
<%--                    <c:when test="${not empty perfilVisitado.propuestasFavoritas}">--%>
<%--                        <p class="text-muted">Lista de ${perfilVisitado.propuestasFavoritas.size()} propuestas favoritas.</p>--%>
<%--                    </c:when>--%>
<%--                    <c:otherwise>--%>
<%--                        <p class="text-muted">Este usuario no tiene propuestas marcadas como favoritas.</p>--%>
<%--                    </c:otherwise>--%>
<%--                </c:choose>--%>
<%--            </div>--%>

<%--            <c:if test="${perfilVisitado.tipo == 'proponente'}">--%>
<%--                <div class="mb-5">--%>
<%--                    <h3 class="border-bottom pb-2">Propuestas Publicadas</h3>--%>
<%--                    <c:choose>--%>
<%--                        <c:when test="${not empty perfilVisitado.propuestasPublicadas}">--%>
<%--                            <p class="text-muted">Mostrando ${perfilVisitado.propuestasPublicadas.size()} propuestas publicadas (sin estado Ingresada).</p>--%>
<%--                        </c:when>--%>
<%--                        <c:otherwise><p class="text-muted">No tiene propuestas publicadas.</p></c:otherwise>--%>
<%--                    </c:choose>--%>
<%--                </div>--%>
<%--            </c:if>--%>

<%--            <c:if test="${perfilVisitado.tipo == 'colaborador'}">--%>
<%--                <div class="mb-5">--%>
<%--                    <h3 class="border-bottom pb-2">Historial de Colaboraciones</h3>--%>
<%--                    <c:choose>--%>
<%--                        <c:when test="${not empty perfilVisitado.colaboraciones}">--%>
<%--                            <c:if test="${esMiPropioPerfil}">--%>
<%--                                <ul class="list-group">--%>
<%--                                    <c:forEach var="colab" items="${perfilVisitado.colaboraciones}">--%>
<%--                                        <li class="list-group-item">--%>
<%--                                            <strong>${colab.propuesta.titulo}</strong>: $${colab.monto} - ${colab.fechaColaboracion}--%>
<%--                                            <a href="/propuesta/${colab.propuesta.id}" class="badge bg-info text-decoration-none ms-2">Ver Detalle</a>--%>
<%--                                        </li>--%>
<%--                                    </c:forEach>--%>
<%--                                </ul>--%>
<%--                            </c:if>--%>
<%--                            <c:if test="${!esMiPropioPerfil}"><p class="text-muted">El usuario ha colaborado en ${perfilVisitado.colaboraciones.size()} propuestas.</p></c:if>--%>
<%--                        </c:when>--%>
<%--                        <c:otherwise><p class="text-muted">No ha realizado colaboraciones.</p></c:otherwise>--%>
<%--                    </c:choose>--%>
<%--                </div>--%>
<%--            </c:if>--%>

<%--            <c:if test="${esMiPropioPerfil && perfilVisitado.tipo == 'proponente'}">--%>
<%--                <div class="mb-5">--%>
<%--                    <h3 class="border-bottom pb-2 text-danger">Propuestas en Estado "Ingresada" (Privadas)</h3>--%>
<%--                    <c:choose>--%>
<%--                        <c:when test="${not empty perfilVisitado.propuestasIngresadas}">--%>
<%--                            <p class="text-danger">Estas propuestas solo son visibles para ti:</p>--%>
<%--                        </c:when>--%>
<%--                        <c:otherwise><p class="text-muted">No tiene propuestas en estado ingresado.</p></c:otherwise>--%>
<%--                    </c:choose>--%>
<%--                </div>--%>
<%--            </c:if>--%>

        </div>
    </div>
</div>