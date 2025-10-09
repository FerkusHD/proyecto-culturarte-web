let todasLasPropuestas = [];

// --- Cargar y mostrar propuestas ---
document.addEventListener('DOMContentLoaded', async () => {
    try {
        const response = await fetch('/propuestas/listar');
        todasLasPropuestas = await response.json();
        mostrarPropuestas(todasLasPropuestas);
    } catch (err) {
        console.error("Error cargando propuestas:", err);
    }
});

function mostrarPropuestas(lista = []) {
    const contenedor = document.getElementById('tarjetas');
    contenedor.innerHTML = '';

    if (lista.length === 0) {
        contenedor.innerHTML = `<p class="text-center mt-4">No hay propuestas disponibles.</p>`;
        return;
    }

    const row = document.createElement('div');
    row.className = 'row row-cols-1 row-cols-md-3 g-4';

    lista.forEach(p => {
        const col = document.createElement('div');
        col.classList.add('col');

        col.innerHTML = `
            <div class="card h-100 border p-2 clickable">
                <img src="${p.imagen}" class="card-img-top" alt="${p.titulo}" style="height: 150px; object-fit: cover;">
                <div class="card-body p-2">
                    <h1 class="card-title fw-bold mb-1" style="font-size: 14px;">${p.titulo}</h1>
                    ${p.proponenteNick ? `<h2 class="card-title fw-bold mb-1" style="font-size: 14px;">${p.proponenteNick}</h2>` : ''}
                </div>
            </div>
        `;

        col.querySelector('.card').addEventListener('click', () => {
            localStorage.setItem('propuestaSeleccionada', JSON.stringify(p));
            window.location.href = window.contextPath + "/propuestas/registroColaboracion";
        });

        row.appendChild(col);
    });

    contenedor.appendChild(row);
}