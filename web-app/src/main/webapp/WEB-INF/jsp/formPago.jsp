<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Formulario de Pago - Culturarte</title>
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
                        padding: 20px;
                    }

                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                    }

                    .card {
                        background: white;
                        border-radius: 15px;
                        box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
                        overflow: hidden;
                    }

                    .card-header {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        padding: 25px;
                    }

                    .card-header h1 {
                        font-size: 24px;
                        margin-bottom: 10px;
                    }

                    .monto-total {
                        font-size: 32px;
                        font-weight: bold;
                        margin-top: 10px;
                    }

                    .card-body {
                        padding: 25px;
                    }

                    .form-group {
                        margin-bottom: 20px;
                    }

                    .form-group label {
                        display: block;
                        color: #333;
                        font-weight: 600;
                        margin-bottom: 8px;
                        font-size: 14px;
                    }

                    .form-group input,
                    .form-group select {
                        width: 100%;
                        padding: 12px 15px;
                        border: 2px solid #e0e0e0;
                        border-radius: 8px;
                        font-size: 16px;
                        transition: border-color 0.3s;
                    }

                    .form-group input:focus,
                    .form-group select:focus {
                        outline: none;
                        border-color: #667eea;
                    }

                    .payment-methods {
                        display: flex;
                        gap: 10px;
                        margin-bottom: 20px;
                    }

                    .payment-method {
                        flex: 1;
                        padding: 15px;
                        border: 2px solid #e0e0e0;
                        border-radius: 10px;
                        text-align: center;
                        cursor: pointer;
                        transition: all 0.3s;
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

                    .payment-method-icon {
                        font-size: 24px;
                        margin-bottom: 5px;
                    }

                    .payment-method-name {
                        font-size: 12px;
                        font-weight: 600;
                    }

                    .payment-fields {
                        display: none;
                    }

                    .payment-fields.active {
                        display: block;
                    }

                    .btn-submit {
                        width: 100%;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        border: none;
                        padding: 15px;
                        border-radius: 10px;
                        font-size: 18px;
                        font-weight: 600;
                        cursor: pointer;
                        transition: transform 0.2s, box-shadow 0.2s;
                    }

                    .btn-submit:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
                    }

                    .btn-submit:active {
                        transform: translateY(0);
                    }

                    .propuesta-info {
                        background: #f8f9fa;
                        padding: 15px;
                        border-radius: 10px;
                        margin-bottom: 20px;
                    }

                    .propuesta-info h3 {
                        color: #667eea;
                        margin-bottom: 5px;
                    }

                    .propuesta-info p {
                        color: #666;
                        font-size: 14px;
                    }
                </style>
            </head>

            <body>
                <div class="container">
                    <div class="card">
                        <div class="card-header">
                            <h1>💳 Pagar Colaboración</h1>
                            <div class="monto-total">$
                                <fmt:formatNumber value="${colaboracion.monto}" pattern="#,##0.00" />
                            </div>
                        </div>

                        <div class="card-body">
                            <div class="propuesta-info">
                                <h3>${colaboracion.tituloPropuesta}</h3>
                                <p>Colaboración realizada el
                                    <fmt:formatDate value="${colaboracion.fecha}" pattern="dd/MM/yyyy" />
                                </p>
                            </div>

                            <form action="${pageContext.request.contextPath}/colaboraciones/pago/procesar" method="post"
                                id="pagoForm">
                                <input type="hidden" name="tituloPropuesta" value="${colaboracion.tituloPropuesta}">
                                <input type="hidden" name="monto" value="${colaboracion.monto}">

                                <!-- Método de Pago -->
                                <div class="form-group">
                                    <label>Método de Pago</label>
                                    <div class="payment-methods">
                                        <label class="payment-method active" data-method="TARJETA">
                                            <input type="radio" name="tipoPago" value="TARJETA" checked>
                                            <div class="payment-method-icon">💳</div>
                                            <div class="payment-method-name">Tarjeta</div>
                                        </label>
                                        <label class="payment-method" data-method="TRANSFERENCIA">
                                            <input type="radio" name="tipoPago" value="TRANSFERENCIA">
                                            <div class="payment-method-icon">🏦</div>
                                            <div class="payment-method-name">Transferencia</div>
                                        </label>
                                        <label class="payment-method" data-method="PAYPAL">
                                            <input type="radio" name="tipoPago" value="PAYPAL">
                                            <div class="payment-method-icon">📱</div>
                                            <div class="payment-method-name">PayPal</div>
                                        </label>
                                    </div>
                                </div>

                                <!-- Nombre del Titular -->
                                <div class="form-group">
                                    <label for="nombreTitular">Nombre del Titular *</label>
                                    <input type="text" id="nombreTitular" name="nombreTitular" required
                                        placeholder="Juan Pérez">
                                </div>

                                <!-- Campos de Tarjeta -->
                                <div id="tarjetaFields" class="payment-fields active">
                                    <div class="form-group">
                                        <label for="tipoTarjeta">Tipo de Tarjeta *</label>
                                        <select id="tipoTarjeta" name="tipoTarjeta">
                                            <option value="OCA">OCA</option>
                                            <option value="VISA">VISA</option>
                                            <option value="MASTER">MasterCard</option>
                                        </select>
                                    </div>

                                    <div class="form-group">
                                        <label for="numeroTarjeta">Número de Tarjeta *</label>
                                        <input type="text" id="numeroTarjeta" name="numeroTarjeta"
                                            placeholder="1234 5678 9012 3456" maxlength="19">
                                    </div>

                                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 15px;">
                                        <div class="form-group">
                                            <label for="fechaVencimiento">Vencimiento *</label>
                                            <input type="text" id="fechaVencimiento" name="fechaVencimiento"
                                                placeholder="MM/YY" maxlength="5">
                                        </div>

                                        <div class="form-group">
                                            <label for="cvc">CVC *</label>
                                            <input type="text" id="cvc" name="cvc" placeholder="123" maxlength="4">
                                        </div>
                                    </div>
                                </div>

                                <!-- Campos de Transferencia -->
                                <div id="transferenciaFields" class="payment-fields">
                                    <div class="form-group">
                                        <label for="nombreBanco">Nombre del Banco *</label>
                                        <input type="text" id="nombreBanco" name="nombreBanco"
                                            placeholder="Banco República">
                                    </div>

                                    <div class="form-group">
                                        <label for="numeroCuentaTransf">Número de Cuenta *</label>
                                        <input type="text" id="numeroCuentaTransf" name="numeroCuenta"
                                            placeholder="1234567890">
                                    </div>
                                </div>

                                <!-- Campos de PayPal -->
                                <div id="paypalFields" class="payment-fields">
                                    <div class="form-group">
                                        <label for="numeroCuentaPaypal">Cuenta de PayPal *</label>
                                        <input type="email" id="numeroCuentaPaypal" name="numeroCuenta"
                                            placeholder="usuario@paypal.com">
                                    </div>
                                </div>

                                <button type="submit" class="btn-submit">Confirmar Pago</button>
                            </form>
                        </div>
                    </div>
                </div>

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