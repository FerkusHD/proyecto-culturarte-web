<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Formulario de Pago - Culturarte</title>

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

                    .payment-method {
                        cursor: pointer;
                        transition: all 0.3s;
                        border: 2px solid #dee2e6;
                    }

                    .payment-method:hover {
                        border-color: #667eea;
                        background: #f8f9ff;
                    }

                    .payment-method.active {
                        border-color: #667eea;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                    }

                    .payment-method input[type="radio"] {
                        display: none;
                    }

                    .payment-fields {
                        display: none;
                    }

                    .payment-fields.active {
                        display: block;
                    }

                    .btn-submit {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        border: none;
                        transition: transform 0.2s, box-shadow 0.2s;
                    }

                    .btn-submit:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
                    }
                </style>
            </head>

            <body>
                <div class="container" style="max-width: 600px;">
                    <div class="card shadow-lg">
                        <!-- Header -->
                        <div class="card-header text-white text-center py-4"
                            style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                            <h1 class="h4 mb-2">
                                <i class="bi bi-credit-card me-2"></i>Pagar Colaboración
                            </h1>
                            <div class="display-6 fw-bold">
                                $
                                <fmt:formatNumber value="${colaboracion.monto}" pattern="#,##0.00" />
                            </div>
                        </div>

                        <!-- Body -->
                        <div class="card-body p-4">
                            <!-- Propuesta Info -->
                            <div class="alert alert-light border mb-4">
                                <h5 class="text-primary mb-2">
                                    <i class="bi bi-lightbulb me-2"></i>${colaboracion.tituloPropuesta}
                                </h5>
                                <p class="text-muted small mb-0">
                                    <i class="bi bi-calendar3 me-1"></i>
                                    Colaboración realizada el
                                    <% com.culturarte.logica.datatypes.DTColaboracion
                                        dtc=(com.culturarte.logica.datatypes.DTColaboracion)
                                        request.getAttribute("colaboracion"); if (dtc !=null && dtc.getFecha() !=null) {
                                        out.print(dtc.getFecha().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                                        } %>
                                </p>
                            </div>

                            <!-- Form -->
                            <form action="${pageContext.request.contextPath}/colaboraciones/pago/procesar" method="post"
                                id="pagoForm">
                                <input type="hidden" name="tituloPropuesta" value="${colaboracion.tituloPropuesta}">
                                <input type="hidden" name="monto" value="${colaboracion.monto}">

                                <!-- Método de Pago -->
                                <div class="mb-4">
                                    <label class="form-label fw-bold">Método de Pago</label>
                                    <div class="row g-2">
                                        <div class="col-4">
                                            <label class="payment-method card h-100 text-center p-3 active"
                                                data-method="TARJETA">
                                                <input type="radio" name="tipoPago" value="TARJETA" checked>
                                                <i class="bi bi-credit-card-2-front fs-1 d-block mb-2"></i>
                                                <small class="fw-semibold">Tarjeta</small>
                                            </label>
                                        </div>
                                        <div class="col-4">
                                            <label class="payment-method card h-100 text-center p-3"
                                                data-method="TRANSFERENCIA">
                                                <input type="radio" name="tipoPago" value="TRANSFERENCIA">
                                                <i class="bi bi-bank fs-1 d-block mb-2"></i>
                                                <small class="fw-semibold">Transferencia</small>
                                            </label>
                                        </div>
                                        <div class="col-4">
                                            <label class="payment-method card h-100 text-center p-3"
                                                data-method="PAYPAL">
                                                <input type="radio" name="tipoPago" value="PAYPAL">
                                                <i class="bi bi-paypal fs-1 d-block mb-2"></i>
                                                <small class="fw-semibold">PayPal</small>
                                            </label>
                                        </div>
                                    </div>
                                </div>

                                <!-- Nombre del Titular -->
                                <div class="mb-3">
                                    <label for="nombreTitular" class="form-label fw-semibold">
                                        Nombre del Titular <span class="text-danger">*</span>
                                    </label>
                                    <input type="text" class="form-control" id="nombreTitular" name="nombreTitular"
                                        required placeholder="Juan Pérez">
                                </div>

                                <!-- Campos de Tarjeta -->
                                <div id="tarjetaFields" class="payment-fields active">
                                    <div class="mb-3">
                                        <label for="tipoTarjeta" class="form-label fw-semibold">
                                            Tipo de Tarjeta <span class="text-danger">*</span>
                                        </label>
                                        <select class="form-select" id="tipoTarjeta" name="tipoTarjeta">
                                            <option value="OCA">OCA</option>
                                            <option value="VISA">VISA</option>
                                            <option value="MASTER">MasterCard</option>
                                        </select>
                                    </div>

                                    <div class="mb-3">
                                        <label for="numeroTarjeta" class="form-label fw-semibold">
                                            Número de Tarjeta <span class="text-danger">*</span>
                                        </label>
                                        <div class="input-group">
                                            <span class="input-group-text">
                                                <i class="bi bi-credit-card"></i>
                                            </span>
                                            <input type="text" class="form-control" id="numeroTarjeta"
                                                name="numeroTarjeta" placeholder="1234 5678 9012 3456" maxlength="19">
                                        </div>
                                    </div>

                                    <div class="row">
                                        <div class="col-6 mb-3">
                                            <label for="fechaVencimiento" class="form-label fw-semibold">
                                                Vencimiento <span class="text-danger">*</span>
                                            </label>
                                            <input type="text" class="form-control" id="fechaVencimiento"
                                                name="fechaVencimiento" placeholder="MM/YY" maxlength="5">
                                        </div>
                                        <div class="col-6 mb-3">
                                            <label for="cvc" class="form-label fw-semibold">
                                                CVC <span class="text-danger">*</span>
                                            </label>
                                            <input type="text" class="form-control" id="cvc" name="cvc"
                                                placeholder="123" maxlength="4">
                                        </div>
                                    </div>
                                </div>

                                <!-- Campos de Transferencia -->
                                <div id="transferenciaFields" class="payment-fields">
                                    <div class="mb-3">
                                        <label for="nombreBanco" class="form-label fw-semibold">
                                            Nombre del Banco <span class="text-danger">*</span>
                                        </label>
                                        <div class="input-group">
                                            <span class="input-group-text">
                                                <i class="bi bi-bank"></i>
                                            </span>
                                            <input type="text" class="form-control" id="nombreBanco" name="nombreBanco"
                                                placeholder="Banco República">
                                        </div>
                                    </div>

                                    <div class="mb-3">
                                        <label for="numeroCuentaTransf" class="form-label fw-semibold">
                                            Número de Cuenta <span class="text-danger">*</span>
                                        </label>
                                        <input type="text" class="form-control" id="numeroCuentaTransf"
                                            name="numeroCuenta" placeholder="1234567890">
                                    </div>
                                </div>

                                <!-- Campos de PayPal -->
                                <div id="paypalFields" class="payment-fields">
                                    <div class="mb-3">
                                        <label for="numeroCuentaPaypal" class="form-label fw-semibold">
                                            Cuenta de PayPal <span class="text-danger">*</span>
                                        </label>
                                        <div class="input-group">
                                            <span class="input-group-text">
                                                <i class="bi bi-paypal"></i>
                                            </span>
                                            <input type="email" class="form-control" id="numeroCuentaPaypal"
                                                name="numeroCuenta" placeholder="usuario@paypal.com">
                                        </div>
                                    </div>
                                </div>

                                <!-- Submit Button -->
                                <button type="submit" class="btn btn-primary btn-submit w-100 py-3 fw-bold">
                                    <i class="bi bi-check-circle me-2"></i>Confirmar Pago
                                </button>
                            </form>
                        </div>
                    </div>

                    <!-- Back Button -->
                    <div class="text-center mt-3">
                        <a href="${pageContext.request.contextPath}/colaboraciones/pago/listar"
                            class="btn btn-outline-light">
                            <i class="bi bi-arrow-left me-2"></i>Volver
                        </a>
                    </div>
                </div>

                <!-- Bootstrap JS -->
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

                <script>
                    // Manejo de métodos de pago
                    const paymentMethods = document.querySelectorAll('.payment-method');
                    const paymentFields = document.querySelectorAll('.payment-fields');

                    paymentMethods.forEach(method => {
                        method.addEventListener('click', function () {
                            // Remover active de todos
                            paymentMethods.forEach(m => m.classList.remove('active'));
                            paymentFields.forEach(f => f.classList.remove('active'));

                            // Activar el seleccionado
                            this.classList.add('active');
                            const methodType = this.dataset.method;
                            const radio = this.querySelector('input[type="radio"]');
                            radio.checked = true;

                            // Mostrar campos correspondientes
                            const fieldsId = methodType.toLowerCase() + 'Fields';
                            document.getElementById(fieldsId).classList.add('active');

                            // Actualizar required en campos
                            updateRequiredFields(methodType);
                        });
                    });

                    function updateRequiredFields(method) {
                        // Remover required de todos los campos opcionales
                        document.querySelectorAll('.payment-fields input, .payment-fields select').forEach(input => {
                            input.removeAttribute('required');
                        });

                        // Agregar required solo a los campos activos
                        const activeFields = document.getElementById(method.toLowerCase() + 'Fields');
                        activeFields.querySelectorAll('input, select').forEach(input => {
                            input.setAttribute('required', 'required');
                        });
                    }

                    // Formateo de número de tarjeta
                    const numeroTarjeta = document.getElementById('numeroTarjeta');
                    if (numeroTarjeta) {
                        numeroTarjeta.addEventListener('input', function (e) {
                            let value = e.target.value.replace(/\s/g, '');
                            let formattedValue = value.match(/.{1,4}/g)?.join(' ') || value;
                            e.target.value = formattedValue;
                        });
                    }

                    // Formateo de fecha de vencimiento
                    const fechaVencimiento = document.getElementById('fechaVencimiento');
                    if (fechaVencimiento) {
                        fechaVencimiento.addEventListener('input', function (e) {
                            let value = e.target.value.replace(/\D/g, '');
                            if (value.length >= 2) {
                                value = value.substring(0, 2) + '/' + value.substring(2, 4);
                            }
                            e.target.value = value;
                        });
                    }

                    // Inicializar required fields
                    updateRequiredFields('TARJETA');
                </script>
            </body>

            </html>