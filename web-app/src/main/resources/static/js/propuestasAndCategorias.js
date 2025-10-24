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

function splitCategoryPath(name) {
    if (!name) return [''];
    if (name.includes('>')) return name.split('>').map(s => s.trim());
    if (name.includes(',')) return name.split(',').map(s => s.trim());
    if (name.includes('/')) return name.split('/').map(s => s.trim());
    return [name.trim()];
}

function buildTreeFromList(list) {
    const root = { children: {}, name: '' };
    (list || []).forEach(full => {
        if (!full) return;
        const parts = splitCategoryPath(full);
        let node = root;
        let pathAccum = [];
        parts.forEach(part => {
            if (!part) return;
            pathAccum.push(part);
            if (!node.children[part]) {
                node.children[part] = { name: part, children: {}, fullPath: pathAccum.join(' > ') };
            }
            node = node.children[part];
        });
    });
    return root;
}

function renderTree(node, container) {
    const ul = document.createElement('ul');
    Object.keys(node.children).sort((a,b)=>a.localeCompare(b, 'es', {sensitivity:'base'})).forEach(key => {
        const child = node.children[key];
        const li = document.createElement('li');

        const hasChildren = Object.keys(child.children).length > 0;


        let toggle = null;
        if (hasChildren) {
            toggle = document.createElement('span');
            toggle.className = 'categoria-toggle collapsed';
            toggle.tabIndex = 0;
            toggle.addEventListener('click', (e) => {
                e.stopPropagation();
                const expanded = toggle.classList.toggle('expanded');
                toggle.classList.toggle('collapsed', !expanded);
                const nestedUl = li.querySelector('ul');
                if (nestedUl) nestedUl.style.display = expanded ? 'block' : 'none';
            });

            toggle.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    toggle.click();
                }
            });
            li.appendChild(toggle);
        } else {
            const spacer = document.createElement('span');
            spacer.className = 'categoria-toggle';
            spacer.style.visibility = 'hidden';
            li.appendChild(spacer);
        }

        const span = document.createElement('span');
        span.className = 'categoria-node';
        span.textContent = child.name;
        span.dataset.fullpath = child.fullPath || child.name;


        if (hasChildren) {
            span.style.fontWeight = '600';
            span.tabIndex = 0;
            span.addEventListener('click', (e) => {
                e.stopPropagation();

                if (toggle) {
                    const expanded = toggle.classList.toggle('expanded');
                    toggle.classList.toggle('collapsed', !expanded);
                }
                const nestedUl = li.querySelector('ul');
                if (nestedUl) nestedUl.style.display = nestedUl.style.display === 'block' ? 'none' : 'block';
            });

            span.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    span.click();
                }
            });
        } else {

            span.tabIndex = 0;
            span.addEventListener('click', (e) => {
                e.stopPropagation();
                const isSelected = span.classList.toggle('selected');
                if (!e.ctrlKey && !e.metaKey) {

                    document.querySelectorAll('.categoria-node.selected').forEach(n => {
                        if (n !== span) n.classList.remove('selected');
                    });
                }
                aplicarFiltrosCombinados();
            });

            span.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    span.click();
                }
            });
        }

        li.appendChild(span);


        if (hasChildren) {
            const childContainer = document.createElement('div');
            childContainer.style.marginLeft = '0.5rem';
            renderTree(child, childContainer);
            const nested = childContainer.querySelector('ul');
            if (nested) nested.style.display = 'none';
            li.appendChild(childContainer);
        }

        ul.appendChild(li);
    });
    container.appendChild(ul);
}

// New renderer that consumes DTOs from /categorias/tree (objects: { nombre, hijos[] })
function renderTreeFromDTO(list, container) {
    const ul = document.createElement('ul');
    (list || []).forEach(node => {
        const li = document.createElement('li');
        const hasChildren = node.hijos && node.hijos.length > 0;

        let toggle = null;
        if (hasChildren) {
            toggle = document.createElement('span');
            toggle.className = 'categoria-toggle collapsed';
            toggle.tabIndex = 0;
            toggle.addEventListener('click', (e) => {
                e.stopPropagation();
                const expanded = toggle.classList.toggle('expanded');
                toggle.classList.toggle('collapsed', !expanded);
                const nested = li.querySelector('ul');
                if (nested) nested.style.display = expanded ? 'block' : 'none';
            });
            toggle.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); toggle.click(); }
            });
            li.appendChild(toggle);
        } else {
            const spacer = document.createElement('span');
            spacer.className = 'categoria-toggle';
            spacer.style.visibility = 'hidden';
            li.appendChild(spacer);
        }

        const span = document.createElement('span');
        span.className = 'categoria-node';
        span.textContent = node.nombre || node.name || '';
        span.dataset.fullpath = node.nombre || node.name || '';
        span.tabIndex = 0;

        if (hasChildren) {
            span.style.fontWeight = '600';
            span.addEventListener('click', (e) => {
                e.stopPropagation();
                if (toggle) {
                    const expanded = toggle.classList.toggle('expanded');
                    toggle.classList.toggle('collapsed', !expanded);
                }
                const nested = li.querySelector('ul');
                if (nested) nested.style.display = nested.style.display === 'block' ? 'none' : 'block';
            });
            span.addEventListener('keydown', (e) => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); span.click(); } });
        } else {
            span.addEventListener('click', (e) => {
                e.stopPropagation();
                span.classList.toggle('selected');
                if (!e.ctrlKey && !e.metaKey) {
                    document.querySelectorAll('.categoria-node.selected').forEach(n => { if (n !== span) n.classList.remove('selected'); });
                }
                aplicarFiltrosCombinados();
            });
            span.addEventListener('keydown', (e) => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); span.click(); } });
        }

        li.appendChild(span);

        if (hasChildren) {
            const childContainer = document.createElement('div');
            childContainer.style.marginLeft = '0.5rem';
            renderTreeFromDTO(node.hijos, childContainer);
            const nestedUl = childContainer.querySelector('ul');
            if (nestedUl) nestedUl.style.display = 'none';
            li.appendChild(childContainer);
        }

        ul.appendChild(li);
    });
    container.appendChild(ul);
}


// Base path (injected by JSP as window.CTX). Falls back to empty string.
const BASE = (typeof window !== 'undefined' && window.CTX) ? window.CTX : '';

// === CATEGORÍAS ===
fetch(BASE + '/categorias/tree')
    .then(response => response.json())
    .then(data => {
        console.debug('categorias/tree data:', data);
        const contenedor = document.getElementById('categorias');
        if (!contenedor) return;
        contenedor.innerHTML = '';

        // Header
        const header = document.createElement('h6');
        header.className = 'fw-bold text-uppercase mb-2';
        header.textContent = 'CATEGORÍAS';
        contenedor.appendChild(header);

        // If the server returned an array of objects with 'nombre' use the DTO renderer
        if (Array.isArray(data) && data.length > 0 && (typeof data[0] === 'object')) {
            const treeDiv = document.createElement('div');
            treeDiv.className = 'categoria-tree';
            renderTreeFromDTO(data, treeDiv);
            contenedor.appendChild(treeDiv);
            // ensure children are collapsed by default
            collapseAllTreeNodes(treeDiv);
        } else {
            // fallback to previous behavior (flat list)
            const treeRoot = buildTreeFromList(data || []);
            const treeDiv = document.createElement('div');
            treeDiv.className = 'categoria-tree';
            renderTree(treeRoot, treeDiv);
            contenedor.appendChild(treeDiv);
            collapseAllTreeNodes(treeDiv);
        }
    })
    .catch(err => {
        console.error('Error cargando categorías:', err);
        // fallback to lista endpoint if tree fails
        fetch(BASE + '/categorias/lista')
            .then(r => r.json())
            .then(list => {
                console.debug('categorias/lista data:', list);
                const contenedor = document.getElementById('categorias');
                if (!contenedor) return;
                contenedor.innerHTML = '';
                const header = document.createElement('h6');
                header.className = 'fw-bold text-uppercase mb-2';
                header.textContent = 'CATEGORÍAS';
                contenedor.appendChild(header);
                const treeRoot = buildTreeFromList(list || []);
                const treeDiv = document.createElement('div');
                treeDiv.className = 'categoria-tree';
                renderTree(treeRoot, treeDiv);
                contenedor.appendChild(treeDiv);
                collapseAllTreeNodes(treeDiv);
            })
            .catch(e => console.error('Fallback error cargando lista de categorias:', e));
    });

// === PROPUESTAS ===
document.addEventListener('DOMContentLoaded', () => {
    // 1. Cargar todas las propuestas
    fetch(BASE + '/propuestas/listar')
        .then(response => response.json())
        .then(data => {
            todasLasPropuestas = data || [];

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


const imagenPorDefecto = '/uploads/imagenes/noimg.jpg'; // Ajusta según tu estructura

function mostrarPropuestas(lista) {
    const contenedor = document.getElementById('tarjetas');
    const row = document.createElement('div');
    row.classList.add('row', 'row-cols-1', 'row-cols-md-3', 'g-4');
    contenedor.innerHTML = '';

    if (!lista || lista.length === 0) {
        contenedor.innerHTML = '<p class="text-center w-100 mt-4">No se encontraron propuestas con los filtros seleccionados.</p>';
        return;
    }

    lista.forEach(p => {
        const porcentaje = (p.montoRecaudado / p.montoNecesario) * 100;
        const porcentajeRedondeado = Math.min(Math.round(porcentaje), 100);
        const diasRestantes = calcularDiasRestantes(p.fechaPrevista);

        // Si no tiene imagen, usar la default
        const imgSrc = p.imagen && p.imagen.trim() !== ''
            ? p.imagen
            : imagenPorDefecto;

        const col = document.createElement('div');
        col.classList.add('col');

        col.innerHTML = `
        <a href="/propuestas/${encodeURIComponent(p.titulo)}" class="text-decoration-none text-dark">
            <div class="card h-100 border p-2 shadow-sm hover-shadow">
                <img src="${imgSrc}" class="card-img-top" alt="${p.titulo}" style="height: 150px; object-fit: cover;">
                <div class="card-body p-2">
                    <h6 class="card-title fw-bold mb-1" style="font-size: 14px;">${p.titulo}</h6>
                    <p class="card-text text-muted mb-2" style="font-size: 12px;">${(p.descripcion || '').substring(0, 120)}...</p>

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
        </a>
        `;
        row.appendChild(col);
    });

    contenedor.appendChild(row);
}


let estadoActivo = 'PUBLICADA';

function inicializarFiltros() {
    const activeTab = document.getElementById('proposalTabs') ? document.getElementById('proposalTabs').querySelector('.nav-link.active') : null;
    if (activeTab) {
        estadoActivo = activeTab.getAttribute('data-estado');
    }

    aplicarFiltrosCombinados();
}

function aplicarFiltrosCombinados() {
    const activeTabElement = document.getElementById('proposalTabs') ? document.getElementById('proposalTabs').querySelector('.nav-link.active') : null;
    const estadoActivo = activeTabElement ? activeTabElement.getAttribute('data-estado') : null;

    // get selected categories (can be multiple with Ctrl/Cmd)
    const categoriasSeleccionadas = Array.from(document.querySelectorAll('.categoria-node.selected'))
        .map(n => n.dataset.fullpath);

    let listaFiltrada = todasLasPropuestas || [];

    if (estadoActivo) {
        listaFiltrada = listaFiltrada.filter(p => p.estadoActual === estadoActivo);
    }

    if (categoriasSeleccionadas.length > 0) {
        listaFiltrada = listaFiltrada.filter(p => {
            const propCat = p.categoria || '';

            return categoriasSeleccionadas.some(sel => propCat.includes(sel) || sel.includes(propCat));
        });
    }

    mostrarPropuestas(listaFiltrada);
}

function filtrarPropuestas() {
    aplicarFiltrosCombinados();
}

// Utility to collapse all nested nodes (hide all ULs that are not the root level)
function collapseAllTreeNodes(container) {
    const root = container || document;
    // hide all nested ULs (any UL inside another UL)
    root.querySelectorAll('.categoria-tree ul ul').forEach(u => {
        u.style.display = 'none';
    });
    // mark all toggles as collapsed and remove expanded
    root.querySelectorAll('.categoria-toggle').forEach(t => {
        t.classList.remove('expanded');
        t.classList.add('collapsed');
    });
}
