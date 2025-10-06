let todasLasPropuestas = [];

// --- Función de Formato de Monto ---
function formatMonto(monto) {
    if (typeof monto !== 'number' || isNaN(monto)) return '$0 U';
    // Utiliza toLocaleString para formatear el número con separadores de miles (para Uruguay/español)
    return `$${monto.toLocaleString('es-UY', { minimumFractionDigits: 0, maximumFractionDigits: 0 })} U`;
}

// --- Función de Cálculo de Días Restantes ---
function calcularDiasRestantes(fechaPrevistaString) {
    // Si la fecha no es válida, devuelve 0 o un valor seguro
    if (!fechaPrevistaString) return 0;

    // Convertir la cadena de fecha a objeto Date
    const fechaPrevista = new Date(fechaPrevistaString);
    const fechaHoy = new Date();

    // Calcular la diferencia en milisegundos
    const diffTime = fechaPrevista.getTime() - fechaHoy.getTime();

    // Si la fecha ya pasó
    if (diffTime <= 0) return 0;

    // Convertir milisegundos a días (redondeando hacia arriba)
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
}

// === CATEGORÍAS ===
fetch('/categorias/lista')
    .then(response => response.json())
    .then(data => {
        const contenedor = document.getElementById('categorias');
        contenedor.innerHTML = '';

        // Asegúrate de que los estilos de Bootstrap se usen aquí para el layout
        contenedor.classList.add('d-flex', 'flex-wrap', 'gap-5', 'mt-3', 'justify-content-start');

        // Encabezado de Categorías
        const header = document.createElement('h6');
        header.classList.add('fw-bold', 'text-uppercase', 'w-100', 'mb-3');
        header.textContent = 'CATEGORÍAS';
        contenedor.appendChild(header);


        // Contenedor para los checkboxes
        const checkboxesContainer = document.createElement('div');
        checkboxesContainer.classList.add('d-flex', 'flex-wrap', 'gap-5');
        contenedor.appendChild(checkboxesContainer);


        data.forEach(nombre => {
            // Usando las clases de Bootstrap para checkboxes para mantener el estilo
            const divCheck = document.createElement('div');
            divCheck.classList.add('form-check');

            const checkbox = document.createElement('input');
            checkbox.classList.add('form-check-input');
            checkbox.type = 'checkbox';
            checkbox.name = 'categorias';
            checkbox.value = nombre;
            checkbox.id = 'check' + nombre.replace(/\s/g, '');

            checkbox.addEventListener('change', filtrarPropuestas);

            const label = document.createElement('label');
            label.classList.add('form-check-label');
            label.setAttribute('for', checkbox.id);
            label.textContent = nombre;

            divCheck.appendChild(checkbox);
            divCheck.appendChild(label);
            checkboxesContainer.appendChild(divCheck);
        });
    });

// === PROPUESTAS ===
document.addEventListener('DOMContentLoaded', () => {
    // 1. Cargar todas las propuestas
    fetch('/propuestas/listar')
        .then(response => response.json())
        .then(data => {
            todasLasPropuestas = data;

            inicializarFiltros();
        })
        .catch(err => console.error("Error cargando propuestas:", err));

    const tabsContainer = document.getElementById('proposalTabs');

    if (tabsContainer) {
        tabsContainer.addEventListener('shown.bs.tab', (event) => {
            // Cuando una pestaña es seleccionada, actualiza el contenido
            aplicarFiltrosCombinados();
        });
    }
});


// --- Función Principal de Renderizado ---
function mostrarPropuestas(lista) {
    const contenedor = document.getElementById('tarjetas');
    // Creamos el contenedor de filas de Bootstrap para las tarjetas
    const row = document.createElement('div');
    row.classList.add('row', 'row-cols-1', 'row-cols-md-3', 'g-4');
    contenedor.innerHTML = ''; // Limpiar el contenedor principal

    if (lista.length === 0) {
        contenedor.innerHTML = '<p class="text-center w-100 mt-4">No se encontraron propuestas con los filtros seleccionados.</p>';
        return;
    }

    lista.forEach(p => {
        // --- CÁLCULOS DENTRO DEL BUCLE ---
        const porcentaje = (p.montoRecaudado / p.montoNecesario) * 100;
        const porcentajeRedondeado = Math.min(Math.round(porcentaje), 100);
        const diasRestantes = calcularDiasRestantes(p.fechaPrevista);

        // El 'card' ahora es solo el contenedor de la columna
        const col = document.createElement('div');
        col.classList.add('col');

        col.innerHTML = `
            <div class="card h-100 border p-2">
                <img src="${p.imagen}" class="card-img-top" alt="${p.titulo}" style="height: 150px; object-fit: cover;">
                <div class="card-body p-2">
                    <h6 class="card-title fw-bold mb-1" style="font-size: 14px;">${p.titulo}</h6>
                    <p class="card-text text-muted mb-2" style="font-size: 12px;">${p.descripcion.substring(0, 120)}...</p>

                    <div class="d-flex align-items-center mb-1">
                        <i class="bi bi-wallet-fill me-1" style="font-size: 14px;"></i>
                        <span class="fw-bold" style="font-size: 14px;">Recaudado: ${formatMonto(p.montoRecaudado)}</span>
                    </div>

                    <div class="progress mb-2" style="height: 18px; border: 1px solid #000;">
                        <div class="progress-bar bg-dark" role="progressbar"
                             style="width: ${porcentajeRedondeado}%;"
                             aria-valuenow="${porcentajeRedondeado}" aria-valuemin="0" aria-valuemax="100">
                        </div>
                        <span class="position-absolute start-0 w-100 text-center text-white" style="font-size: 10px;">
                            ${porcentajeRedondeado}%
                        </span>
                    </div>

                    <div class="d-flex justify-content-between text-center" style="font-size: 12px;">
                        <div>
                            <div class="fw-bold fs-5">${diasRestantes}</div>
                            <small class="text-muted">días restantes</small>
                        </div>
                        <div>
                            <div class="fw-bold fs-5">${p.cantColaboradores}</div>
                            <small class="text-muted">colaborador</small>
                        </div>
                    </div>
                </div>
            </div>
        `;
        row.appendChild(col);
    });

    contenedor.appendChild(row);
}

let estadoActivo = 'PUBLICADA';

function inicializarFiltros() {
    // Obtener el estado inicial de la pestaña activa al cargar la página.
    // Asumiendo que el ID del ul es 'proposalTabs' y la pestaña por defecto es 'Creadas'.
    const activeTab = document.getElementById('proposalTabs').querySelector('.nav-link.active');
    if (activeTab) {
        estadoActivo = activeTab.getAttribute('data-estado');
    }

    aplicarFiltrosCombinados();
}

function aplicarFiltrosCombinados() {

    const activeTabElement = document.getElementById('proposalTabs').querySelector('.nav-link.active');
    let estadoActivo = activeTabElement ? activeTabElement.getAttribute('data-estado') : null;

    const categoriasSeleccionadas = Array.from(document.querySelectorAll('input[name="categorias"]:checked'))
        .map(c => c.value);

    let listaFiltrada = todasLasPropuestas;

    if (estadoActivo) {
        listaFiltrada = listaFiltrada.filter(p => p.estadoActual === estadoActivo);
    }

    // B. Filtrar por Categoría (Checkboxes) - ¡ESTE ES EL CAMBIO!
    if (categoriasSeleccionadas.length > 0) {
        listaFiltrada = listaFiltrada.filter(p => {

            const primeraCategoria = p.categoria ? p.categoria.split(',')[0].trim() : '';

            return categoriasSeleccionadas.includes(primeraCategoria);
        });
    }
    mostrarPropuestas(listaFiltrada);
}

function filtrarPropuestas() {
    aplicarFiltrosCombinados();
}
