<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Pagar Colaboraciones - Culturarte</title>

                <!-- Bootstrap CSS -->
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
                <link rel="stylesheet"
                    href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">

                <style>
                    body {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        min-height: 100vh;
                        padding: 20px;
                    }

                    .colaboracion-card {
                        transition: transform 0.2s, box-shadow 0.2s;
                        border-left: 4px solid #667eea;
                    }

                    .colaboracion-card:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
                    }

                    .propuesta-imagen {
                        width: 60px;
                        height: 60px;
                        border-radius: 8px;
                        object-fit: cover;
                    }

                    .monto {
                        font-size: 24px;
                        font-weight: bold;
                        color: #667eea;
                    }

                    .btn-pagar {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        border: none;
                        transition: transform 0.2s, box-shadow 0.2s;
                    }

                    .btn-pagar:hover {
                        transform: scale(1.05);
                        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
                    }
                </style>
            </head>

            <body>
                <div class="container" style="max-width: 600px;">
                    <!-- Header -->
                    <div class="card shadow-sm mb-2">
                        <div class="card-body">
                            <h1 class="h4 text-primary mb-1">
                                <i class="bi bi-credit-card me-2"></i>Pagar Colaboraciones
                            </h1>
                            <p class="text-muted mb-0 small">Selecciona una colaboración para realizar el pago</p>
                        </div>
                    </div>

                    <!-- Content -->
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${empty colaboraciones}">
                                    <!-- Empty State -->
                                    <div class="text-center py-5">
                                        <i class="bi bi-file-earmark-text text-muted"
                                            style="font-size: 4rem; opacity: 0.5;"></i>
                                        <h5 class="mt-3">No hay colaboraciones pendientes de pago</h5>
                                        <p class="text-muted">Todas tus colaboraciones están al día</p>
                                        <a href="${pageContext.request.contextPath}/" class="btn btn-primary mt-3">
                                            <i class="bi bi-house me-2"></i>Volver al inicio
                                        </a>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <!-- Colaboraciones List -->
                                    <c:forEach items="${colaboraciones}" var="colab" varStatus="status">
                                        <div class="card colaboracion-card bg-light mb-3 ${status.last ? 'mb-0' : ''}">
                                            <div class="card-body">
                                                <div class="d-flex align-items-center mb-3">
                                                    <!-- Imagen de la propuesta -->
                                                    <c:choose>
                                                        <c:when test="${not empty colab.propuesta.imagen}">
                                                            <img src="${pageContext.request.contextPath}/uploads/${colab.propuesta.imagen}"
                                                                alt="${colab.tituloPropuesta}"
                                                                class="propuesta-imagen me-3"
                                                                onerror="this.src='${pageContext.request.contextPath}/images/default-propuesta.png'">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="propuesta-imagen me-3 d-flex align-items-center justify-content-center"
                                                                style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                                                                <i class="bi bi-image text-white fs-4"></i>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>

                                                    <!-- Info de la propuesta -->
                                                    <div class="flex-grow-1">
                                                        <h5 class="mb-1">${colab.tituloPropuesta}</h5>
                                                        <p class="text-muted small mb-0">
                                                            <i class="bi bi-calendar3 me-1"></i>
                                                            <% com.culturarte.logica.datatypes.DTColaboracion
                                                                dtc=(com.culturarte.logica.datatypes.DTColaboracion)
                                                                pageContext.getAttribute("colab"); if (dtc !=null &&
                                                                dtc.getFecha() !=null) {
                                                                out.print(dtc.getFecha().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                                                                } %> -
                                                                <i class="bi bi-clock ms-2 me-1"></i>
                                                                <% if (dtc !=null && dtc.getHora() !=null) {
                                                                    out.print(dtc.getHora().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
                                                                    } %>
                                                        </p>
                                                    </div>
                                                </div>

                                                <!-- Detalles y acción -->
                                                <div
                                                    class="d-flex justify-content-between align-items-center pt-3 border-top">
                                                    <div class="monto">
                                                        $
                                                        <fmt:formatNumber value="${colab.monto}" pattern="#,##0.00" />
                                                    </div>
                                                    <c:url value="/colaboraciones/pago/formulario" var="pagoUrl">
                                                        <c:param name="tituloPropuesta"
                                                            value="${colab.tituloPropuesta}" />
                                                    </c:url>
                                                    <a href="${pagoUrl}" class="btn btn-primary btn-pagar px-4">
                                                        <i class="bi bi-credit-card me-2"></i>Pagar Ahora
                                                    </a>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Back Button -->
                    <div class="text-center mt-3">
                        <a href="${pageContext.request.contextPath}/" class="btn btn-outline-light">
                            <i class="bi bi-arrow-left me-2"></i>Volver al inicio
                        </a>
                    </div>
                </div>

                <!-- Bootstrap JS -->
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
            </body>

            </html>