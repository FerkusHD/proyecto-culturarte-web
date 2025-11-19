/**
 * Script para verificación automática de disponibilidad de nickname y email
 * mediante AJAX mientras el usuario escribe en el formulario.
 * 
 * Requisito 7.5: Verificación de Email y Nick
 */

// Timeout para evitar demasiadas peticiones mientras el usuario escribe
let timeoutNickname = null;
let timeoutEmail = null;

// Delay en milisegundos antes de hacer la petición AJAX
const DELAY_VERIFICACION = 500;

/**
 * Inicializa la verificación automática cuando se carga la página.
 */
document.addEventListener('DOMContentLoaded', function() {
    const nicknameInput = document.getElementById('nickname');
    const emailInput = document.getElementById('email');

    if (nicknameInput) {
        // Verificar nickname mientras el usuario escribe
        nicknameInput.addEventListener('input', function() {
            const nickname = this.value.trim();
            
            // Limpiar timeout anterior
            if (timeoutNickname) {
                clearTimeout(timeoutNickname);
            }

            // Si está vacío, limpiar mensaje
            if (nickname === '') {
                mostrarMensajeNickname('', '');
                return;
            }

            // Esperar un poco antes de hacer la petición (debounce)
            timeoutNickname = setTimeout(function() {
                verificarNickname(nickname);
            }, DELAY_VERIFICACION);
        });

        // Verificar también cuando el campo pierde el foco
        nicknameInput.addEventListener('blur', function() {
            const nickname = this.value.trim();
            if (nickname !== '') {
                verificarNickname(nickname);
            }
        });
    }

    if (emailInput) {
        // Verificar email mientras el usuario escribe
        emailInput.addEventListener('input', function() {
            const email = this.value.trim();
            
            // Limpiar timeout anterior
            if (timeoutEmail) {
                clearTimeout(timeoutEmail);
            }

            // Si está vacío, limpiar mensaje
            if (email === '') {
                mostrarMensajeEmail('', '');
                return;
            }

            // Esperar un poco antes de hacer la petición (debounce)
            timeoutEmail = setTimeout(function() {
                verificarEmail(email);
            }, DELAY_VERIFICACION);
        });

        // Verificar también cuando el campo pierde el foco
        emailInput.addEventListener('blur', function() {
            const email = this.value.trim();
            if (email !== '') {
                verificarEmail(email);
            }
        });
    }
});

/**
 * Verifica la disponibilidad del nickname mediante AJAX.
 */
function verificarNickname(nickname) {
    if (!nickname || nickname.trim() === '') {
        return;
    }

    // Mostrar indicador de carga
    mostrarMensajeNickname('Verificando...', 'info');

    // Hacer petición AJAX
    const xhr = new XMLHttpRequest();
    xhr.open('GET', '/usuarios/verificar-nickname?nickname=' + encodeURIComponent(nickname), true);
    xhr.setRequestHeader('Content-Type', 'application/json');

    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    if (response.disponible) {
                        mostrarMensajeNickname(response.mensaje, 'success');
                    } else {
                        mostrarMensajeNickname(response.mensaje, 'error');
                    }
                } catch (e) {
                    mostrarMensajeNickname('Error al procesar la respuesta', 'error');
                }
            } else {
                mostrarMensajeNickname('Error al verificar disponibilidad', 'error');
            }
        }
    };

    xhr.onerror = function() {
        mostrarMensajeNickname('Error de conexión', 'error');
    };

    xhr.send();
}

/**
 * Verifica la disponibilidad del email mediante AJAX.
 */
function verificarEmail(email) {
    if (!email || email.trim() === '') {
        return;
    }

    // Mostrar indicador de carga
    mostrarMensajeEmail('Verificando...', 'info');

    // Hacer petición AJAX
    const xhr = new XMLHttpRequest();
    xhr.open('GET', '/usuarios/verificar-email?email=' + encodeURIComponent(email), true);
    xhr.setRequestHeader('Content-Type', 'application/json');

    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                try {
                    const response = JSON.parse(xhr.responseText);
                    if (response.disponible) {
                        mostrarMensajeEmail(response.mensaje, 'success');
                    } else {
                        mostrarMensajeEmail(response.mensaje, 'error');
                    }
                } catch (e) {
                    mostrarMensajeEmail('Error al procesar la respuesta', 'error');
                }
            } else {
                mostrarMensajeEmail('Error al verificar disponibilidad', 'error');
            }
        }
    };

    xhr.onerror = function() {
        mostrarMensajeEmail('Error de conexión', 'error');
    };

    xhr.send();
}

/**
 * Muestra el mensaje de disponibilidad para el campo nickname.
 */
function mostrarMensajeNickname(mensaje, tipo) {
    let mensajeDiv = document.getElementById('mensaje-nickname');
    
    // Crear el div si no existe
    if (!mensajeDiv) {
        mensajeDiv = document.createElement('div');
        mensajeDiv.id = 'mensaje-nickname';
        mensajeDiv.className = 'mt-1';
        
        const nicknameInput = document.getElementById('nickname');
        if (nicknameInput && nicknameInput.parentNode) {
            nicknameInput.parentNode.appendChild(mensajeDiv);
        }
    }

    // Limpiar clases anteriores
    mensajeDiv.className = 'mt-1';
    
    // Establecer contenido y estilo según el tipo
    if (mensaje === '') {
        mensajeDiv.innerHTML = '';
        mensajeDiv.style.display = 'none';
    } else {
        mensajeDiv.style.display = 'block';
        
        if (tipo === 'success') {
            mensajeDiv.className += ' text-success';
            mensajeDiv.innerHTML = '<small><i class="bi bi-check-circle"></i> ' + mensaje + '</small>';
        } else if (tipo === 'error') {
            mensajeDiv.className += ' text-danger';
            mensajeDiv.innerHTML = '<small><i class="bi bi-x-circle"></i> ' + mensaje + '</small>';
        } else if (tipo === 'info') {
            mensajeDiv.className += ' text-info';
            mensajeDiv.innerHTML = '<small><i class="bi bi-hourglass-split"></i> ' + mensaje + '</small>';
        } else {
            mensajeDiv.innerHTML = '<small>' + mensaje + '</small>';
        }
    }
}

/**
 * Muestra el mensaje de disponibilidad para el campo email.
 */
function mostrarMensajeEmail(mensaje, tipo) {
    let mensajeDiv = document.getElementById('mensaje-email');
    
    // Crear el div si no existe
    if (!mensajeDiv) {
        mensajeDiv = document.createElement('div');
        mensajeDiv.id = 'mensaje-email';
        mensajeDiv.className = 'mt-1';
        
        const emailInput = document.getElementById('email');
        if (emailInput && emailInput.parentNode) {
            emailInput.parentNode.appendChild(mensajeDiv);
        }
    }

    // Limpiar clases anteriores
    mensajeDiv.className = 'mt-1';
    
    // Establecer contenido y estilo según el tipo
    if (mensaje === '') {
        mensajeDiv.innerHTML = '';
        mensajeDiv.style.display = 'none';
    } else {
        mensajeDiv.style.display = 'block';
        
        if (tipo === 'success') {
            mensajeDiv.className += ' text-success';
            mensajeDiv.innerHTML = '<small><i class="bi bi-check-circle"></i> ' + mensaje + '</small>';
        } else if (tipo === 'error') {
            mensajeDiv.className += ' text-danger';
            mensajeDiv.innerHTML = '<small><i class="bi bi-x-circle"></i> ' + mensaje + '</small>';
        } else if (tipo === 'info') {
            mensajeDiv.className += ' text-info';
            mensajeDiv.innerHTML = '<small><i class="bi bi-hourglass-split"></i> ' + mensaje + '</small>';
        } else {
            mensajeDiv.innerHTML = '<small>' + mensaje + '</small>';
        }
    }
}

