document.addEventListener('DOMContentLoaded', () => {
  const contraseña = document.getElementById('contraseña');
  const confirmar = document.getElementById('confirmar');
  const mensaje = document.getElementById('mensaje');

  confirmar.addEventListener('input', () => {
     if (!contraseña.value || !confirmar.value) {
      mensaje.textContent = "";
      return;
    }
    if (contraseña.value === confirmar.value) {
      mensaje.textContent = "✅ Las contraseñas coinciden";
      mensaje.style.color = "green";
    } else {
      mensaje.textContent = "❌ Las contraseñas no coinciden";
      mensaje.style.color = "red";
    }
  })
});