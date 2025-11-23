<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Pago Exitoso - Culturarte</title>

                <!-- Bootstrap CSS -->
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
                <link rel="stylesheet"
                    href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">

                <style>
                    body {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        min-height: 100vh;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        padding: 20px;
                    }

                    .success-card {
                        animation: slideUp 0.5s ease-out;
                    }

                    @keyframes slideUp {
                        from {
                            opacity: 0;
                            transform: translateY(30px);
                        }

                        to {
                            opacity: 1;
                            transform: translateY(0);
                        }
                    }

                    .success-icon {
                        width: 80px;
                        height: 80px;
                        background: white;
                        border-radius: 50%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        margin: 0 auto 20px;
                        animation: checkmark 0.6s ease-in-out 0.3s both;
                    }

                    @keyframes checkmark {
                        0% {
                            transform: scale(0);
                        }

                        50% {
                            transform: scale(1.2);
                        }

                        100% {
                            transform: scale(1);
                        }
                    }

                    .monto-total {
                        font-size: 2rem;
                        color: #28a745;
                        font-weight: bold;
                    }

                    .btn-primary-custom {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        border: none;
                        transition: all 0.3s;
                    }

                    .btn-primary-custom:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
                    }
                </style>
            </head>

            <body>
                <div class="container" style="max-width: 500px;">
                    <div class="card success-card shadow-lg">
                        <!-- Header -->
                        <div class="card-header text-white text-center py-5"
                            style="background: linear-gradient(135deg, #28a745 0%, #20c997 100%);">
                            <div class="success-icon">
                                <i class="bi bi-check-lg text-success" style="font-size: 3rem;"></i>
                            </div>
                            <h1 class="h3 mb-2">¡Pago Exitoso!</h1>
                            <p class="mb-0">Tu pago ha sido procesado correctamente</p>
                        </div>

                        <!-- Body -->
                        <div class="card-body p-4">
                            <!-- Payment Details -->
                            <div class="bg-light rounded p-3 mb-4">
                                <div class="d-flex justify-content-between align-items-center py-2 border-bottom">
                                    <span class="text-muted">Propuesta</span>
                                    <span class="fw-semibold">${colaboracion.tituloPropuesta}</span>
                                </div>
                                <div class="d-flex justify-content-between align-items-center py-2 border-bottom">
                                    <span class="text-muted">Fecha de Pago</span>
                                    <span class="fw-semibold">
                                        <fmt:formatDate value="${pago.fechaPago}" pattern="dd/MM/yyyy" />
                                    </span>
                                </div>
                                <div class="d-flex justify-content-between align-items-center py-2 border-bottom">
                                    <span class="text-muted">Método de Pago</span>
                                    <span class="fw-semibold">
                                        <c:choose>
                                            <c:when test="${pago.tipoPago == 'TARJETA'}">
                                                <i class="bi bi-credit-card me-1"></i>Tarjeta
                                            </c:when>
                                            <c:when test="${pago.tipoPago == 'TRANSFERENCIA'}">
                                                <i class="bi bi-bank me-1"></i>Transferencia
                                            </c:when>
                                            <c:when test="${pago.tipoPago == 'PAYPAL'}">
                                                <i class="bi bi-paypal me-1"></i>PayPal
                                            </c:when>
                                            <c:otherwise>
                                                ${pago.tipoPago}
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                <div class="d-flex justify-content-between align-items-center py-2">
                                    <span class="text-muted">Monto Pagado</span>
                                    <span class="monto-total">
                                        $
                                        <fmt:formatNumber value="${pago.monto}" pattern="#,##0.00" />
                                    </span>
                                </div>
                            </div>

                            <!-- Info Box -->
                            <div class="alert alert-info border-start border-4 border-info">
                                <i class="bi bi-envelope me-2"></i>
                                <small>
                                    Hemos enviado un correo electrónico de confirmación con los detalles del pago.
                                    Puedes descargar tu constancia de pago desde el enlace incluido en el email.
                                </small>
                            </div>

                            <!-- Action Buttons -->
                            <div class="row g-2">
                                <div class="col-6">
                                    <a href="${pageContext.request.contextPath}/colaboraciones/pago/listar"
                                        class="btn btn-outline-secondary w-100">
                                        <i class="bi bi-list-ul me-2"></i>Ver Colaboraciones
                                    </a>
                                </div>
                                <div class="col-6">
                                    <a href="${pageContext.request.contextPath}/"
                                        class="btn btn-primary btn-primary-custom w-100">
                                        <i class="bi bi-house me-2"></i>Ir al Inicio
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Bootstrap JS -->
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
            </body>

            </html>