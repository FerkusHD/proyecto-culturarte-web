<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Pago Exitoso - Culturarte</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }

                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        min-height: 100vh;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        padding: 20px;
                    }

                    .success-card {
                        background: white;
                        border-radius: 20px;
                        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
                        max-width: 500px;
                        width: 100%;
                        overflow: hidden;
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

                    .success-header {
                        background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
                        color: white;
                        padding: 40px 30px;
                        text-align: center;
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

                    .success-icon svg {
                        width: 50px;
                        height: 50px;
                        stroke: #28a745;
                    }

                    .success-header h1 {
                        font-size: 28px;
                        margin-bottom: 10px;
                    }

                    .success-header p {
                        font-size: 16px;
                        opacity: 0.9;
                    }

                    .success-body {
                        padding: 30px;
                    }

                    .payment-details {
                        background: #f8f9fa;
                        border-radius: 12px;
                        padding: 20px;
                        margin-bottom: 25px;
                    }

                    .detail-row {
                        display: flex;
                        justify-content: space-between;
                        padding: 12px 0;
                        border-bottom: 1px solid #e0e0e0;
                    }

                    .detail-row:last-child {
                        border-bottom: none;
                    }

                    .detail-label {
                        color: #666;
                        font-size: 14px;
                    }

                    .detail-value {
                        color: #333;
                        font-weight: 600;
                        font-size: 14px;
                    }

                    .monto-total {
                        font-size: 32px;
                        color: #28a745;
                        font-weight: bold;
                    }

                    .info-box {
                        background: #e7f3ff;
                        border-left: 4px solid #2196F3;
                        padding: 15px;
                        border-radius: 8px;
                        margin-bottom: 25px;
                    }

                    .info-box p {
                        color: #1976D2;
                        font-size: 14px;
                        line-height: 1.6;
                    }

                    .btn-container {
                        display: flex;
                        gap: 10px;
                    }

                    .btn {
                        flex: 1;
                        padding: 15px;
                        border-radius: 10px;
                        font-size: 16px;
                        font-weight: 600;
                        text-align: center;
                        text-decoration: none;
                        transition: all 0.3s;
                        cursor: pointer;
                    }

                    .btn-primary {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                    }

                    .btn-primary:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
                    }

                    .btn-secondary {
                        background: #f8f9fa;
                        color: #667eea;
                        border: 2px solid #667eea;
                    }

                    .btn-secondary:hover {
                        background: #667eea;
                        color: white;
                    }
                </style>
            </head>

            <body>
                <div class="success-card">
                    <div class="success-header">
                        <div class="success-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="3"
                                stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
                            </svg>
                        </div>
                        <h1>¡Pago Exitoso!</h1>
                        <p>Tu pago ha sido procesado correctamente</p>
                    </div>

                    <div class="success-body">
                        <div class="payment-details">
                            <div class="detail-row">
                                <span class="detail-label">Propuesta</span>
                                <span class="detail-value">${colaboracion.tituloPropuesta}</span>
                            </div>
                            <div class="detail-row">
                                <span class="detail-label">Fecha de Pago</span>
                                <span class="detail-value">
                                    <fmt:formatDate value="${pago.fechaPago}" pattern="dd/MM/yyyy" />
                                </span>
                            </div>
                            <div class="detail-row">
                                <span class="detail-label">Método de Pago</span>
                                <span class="detail-value">${pago.tipoPago}</span>
                            </div>
                            <div class="detail-row">
                                <span class="detail-label">Monto Pagado</span>
                                <span class="detail-value monto-total">
                                    $
                                    <fmt:formatNumber value="${pago.monto}" pattern="#,##0.00" />
                                </span>
                            </div>
                        </div>

                        <div class="info-box">
                            <p>
                                📧 Hemos enviado un correo electrónico de confirmación con los detalles del pago.
                                Puedes descargar tu constancia de pago desde el enlace incluido en el email.
                            </p>
                        </div>

                        <div class="btn-container">
                            <a href="${pageContext.request.contextPath}/colaboraciones/pago/listar"
                                class="btn btn-secondary">
                                Ver Colaboraciones
                            </a>
                            <a href="${pageContext.request.contextPath}/" class="btn btn-primary">
                                Ir al Inicio
                            </a>
                        </div>
                    </div>
                </div>
            </body>

            </html>