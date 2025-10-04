document.addEventListener('DOMContentLoaded', () => {
    const password = document.getElementById('password');
    const confirmar = document.getElementById('confirmar');
    const mensaje = document.getElementById('mensaje');

    function validarContraseña() {
        if (!password.value || !confirmar.value) {
            mensaje.textContent = "";
            return;
        }

        if (password.value === confirmar.value) {
            mensaje.textContent = "✅ Las contraseñas coinciden";
            mensaje.style.color = "green";
        } else {
            mensaje.textContent = "❌ Las contraseñas no coinciden";
            mensaje.style.color = "red";
        }
    }

    password.addEventListener('input', validarContraseña);
    confirmar.addEventListener('input', validarContraseña);
});
