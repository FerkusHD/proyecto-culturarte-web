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

    try {
        // Manejar diferentes formatos de fecha
        let fechaPrevista;
        if (typeof fechaPrevistaString === 'string') {
            // Si viene como string, puede ser ISO (2025-11-15) o con tiempo
            // Remover tiempo si existe
            const fechaStr = fechaPrevistaString.split('T')[0];
            fechaPrevista = new Date(fechaStr);
        } else if (fechaPrevistaString instanceof Date) {
            fechaPrevista = fechaPrevistaString;
        } else {
            // Si es un objeto con propiedades (XMLGregorianCalendar serializado)
            if (fechaPrevistaString.year && fechaPrevistaString.month && fechaPrevistaString.day) {
                fechaPrevista = new Date(
                    fechaPrevistaString.year,
                    fechaPrevistaString.month - 1, // Los meses en JS son 0-indexed
                    fechaPrevistaString.day
                );
            } else {
                return 0;
            }
        }

        // Verificar que la fecha es válida
        if (isNaN(fechaPrevista.getTime())) {
            console.warn('Fecha inválida:', fechaPrevistaString);
            return 0;
        }

        const fechaHoy = new Date();
        fechaHoy.setHours(0, 0, 0, 0); // Resetear a medianoche para comparar solo fechas
        fechaPrevista.setHours(0, 0, 0, 0);

        // Calcular la diferencia en milisegundos
        const diffTime = fechaPrevista.getTime() - fechaHoy.getTime();

        // Si la fecha ya pasó
        if (diffTime <= 0) return 0;

        // Convertir milisegundos a días (redondeando hacia arriba)
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        return diffDays;
    } catch (e) {
        console.warn('Error al calcular días restantes:', e, 'Fecha:', fechaPrevistaString);
        return 0;
    }
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
function cargarCategorias() {
    const contenedor = document.getElementById('categorias');
    if (!contenedor) {
        console.warn('Contenedor de categorías no encontrado');
        return;
    }

    const url = BASE + '/categorias/tree';
    console.log('Iniciando carga de categorías desde:', url);
    fetch(url)
        .then(response => {
            console.log('Respuesta categorías recibida, status:', response.status, response.statusText);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status} - ${response.statusText}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Datos de categorías recibidos:', data);
            console.log('Tipo de datos:', Array.isArray(data) ? 'Array' : typeof data);
            console.log('Cantidad de elementos:', Array.isArray(data) ? data.length : 'N/A');
            if (Array.isArray(data) && data.length > 0) {
                console.log('Primer elemento:', data[0]);
            }
            contenedor.innerHTML = '';

            // Header
            const header = document.createElement('h6');
            header.className = 'fw-bold text-uppercase mb-2';
            header.textContent = 'CATEGORÍAS';
            contenedor.appendChild(header);

            // Si no hay datos, mostrar mensaje
            if (!Array.isArray(data) || data.length === 0) {
                const mensaje = document.createElement('p');
                mensaje.className = 'text-muted small';
                mensaje.textContent = 'No hay categorías disponibles';
                contenedor.appendChild(mensaje);
                console.warn('No hay categorías para mostrar');
                return;
            }

            // If the server returned an array of objects with 'nombre' use the DTO renderer
            if (typeof data[0] === 'object' && data[0].nombre !== undefined) {
                console.log('Usando renderTreeFromDTO');
                const treeDiv = document.createElement('div');
                treeDiv.className = 'categoria-tree';
                renderTreeFromDTO(data, treeDiv);
                contenedor.appendChild(treeDiv);
                collapseAllTreeNodes(treeDiv);
            } else {
                console.log('Usando buildTreeFromList (fallback)');
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
            console.error('=== ERROR cargando categorías (tree) ===', err);
            console.error('Error completo:', err);
            console.error('Stack:', err.stack);
            // fallback to lista endpoint if tree fails
            console.log('Intentando fallback a /categorias/lista');
            fetch(BASE + '/categorias/lista')
                .then(r => {
                    if (!r.ok) {
                        throw new Error(`HTTP error! status: ${r.status}`);
                    }
                    return r.json();
                })
                .then(list => {
                    console.debug('categorias/lista data:', list);
                    const contenedor = document.getElementById('categorias');
                    if (!contenedor) return;
                    contenedor.innerHTML = '';
                    const header = document.createElement('h6');
                    header.className = 'fw-bold text-uppercase mb-2';
                    header.textContent = 'CATEGORÍAS';
                    contenedor.appendChild(header);
                    
                    if (!Array.isArray(list) || list.length === 0) {
                        const mensaje = document.createElement('p');
                        mensaje.className = 'text-muted small';
                        mensaje.textContent = 'No hay categorías disponibles';
                        contenedor.appendChild(mensaje);
                        return;
                    }
                    
                    const treeRoot = buildTreeFromList(list || []);
                    const treeDiv = document.createElement('div');
                    treeDiv.className = 'categoria-tree';
                    renderTree(treeRoot, treeDiv);
                    contenedor.appendChild(treeDiv);
                    collapseAllTreeNodes(treeDiv);
                })
                .catch(e => {
                    console.error('=== ERROR en fallback de categorías ===', e);
                    console.error('Error completo:', e);
                    console.error('Stack:', e.stack);
                    const contenedor = document.getElementById('categorias');
                    if (contenedor) {
                        contenedor.innerHTML = '<div class="alert alert-warning" role="alert"><p class="text-muted small mb-0">Error al cargar categorías. Por favor, recarga la página.</p></div>';
                    }
                });
        });
}

// Cargar categorías cuando el DOM esté listo
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', cargarCategorias);
} else {
    cargarCategorias();
}

// === PROPUESTAS ===
document.addEventListener('DOMContentLoaded', () => {
    // 1. Cargar todas las propuestas
    console.log('Iniciando carga de propuestas desde:', BASE + '/propuestas/listar');
    fetch(BASE + '/propuestas/listar')
        .then(response => {
            console.log('Respuesta recibida, status:', response.status, response.statusText);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status} - ${response.statusText}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Datos recibidos:', data);
            todasLasPropuestas = Array.isArray(data) ? data : [];
            console.log('Propuestas cargadas:', todasLasPropuestas.length);
            if (todasLasPropuestas.length > 0) {
                console.log('Primera propuesta ejemplo:', todasLasPropuestas[0]);
            }
            inicializarFiltros();
        })
        .catch(err => {
            console.error("=== ERROR cargando propuestas ===", err);
            console.error("Error completo:", err);
            console.error("Stack:", err.stack);
            todasLasPropuestas = [];
            const contenedor = document.getElementById('tarjetas');
            if (contenedor) {
                contenedor.innerHTML = '<div class="alert alert-danger" role="alert">Error al cargar las propuestas. Por favor, recarga la página o contacta al administrador.</div>';
            }
            inicializarFiltros();
        });

    const tabsContainer = document.getElementById('proposalTabs');

    if (tabsContainer) {
        tabsContainer.addEventListener('shown.bs.tab', (event) => {
            // Cuando una pestaña es seleccionada, actualiza el contenido
            aplicarFiltrosCombinados();
        });
    }
});


const imagenPorDefecto = BASE + '/uploads/imagenes/noimg.jpg'; // Ajusta según tu estructura

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
        // Normalizar propiedades (pueden venir con getters de SOAP o como propiedades directas)
        const titulo = p.titulo || (typeof p.getTitulo === 'function' ? p.getTitulo() : '') || '';
        const descripcion = p.descripcion || (typeof p.getDescripcion === 'function' ? p.getDescripcion() : '') || '';
        const montoRecaudado = parseFloat(p.montoRecaudado || (typeof p.getMontoRecaudado === 'function' ? p.getMontoRecaudado() : 0) || 0);
        const montoNecesario = parseFloat(p.montoNecesario || (typeof p.getMontoNecesario === 'function' ? p.getMontoNecesario() : 1) || 1);
        const fechaPrevista = p.fechaPrevista || (typeof p.getFechaPrevista === 'function' ? p.getFechaPrevista() : null);
        const cantColaboradores = parseInt(p.cantColaboradores || (typeof p.getCantColaboradores === 'function' ? p.getCantColaboradores() : 0) || 0);
        const estadoActual = p.estadoActual || (typeof p.getEstado === 'function' ? p.getEstado() : '') || p.estado || '';
        const imagenBase64 = p.imagenBase64 || (typeof p.getImagenBase64 === 'function' ? p.getImagenBase64() : '') || '';
        const imagen = p.imagen || (typeof p.getImagen === 'function' ? p.getImagen() : '') || '';
        const categoria = p.categoria || (typeof p.getCategoria === 'function' ? p.getCategoria() : '') || '';
        
        const porcentaje = (montoRecaudado / montoNecesario) * 100;
        const porcentajeRedondeado = Math.min(Math.round(porcentaje), 100);
        const diasRestantes = calcularDiasRestantes(fechaPrevista);

        // Si no tiene imagen, usar la default
        let imgSrc = imagenPorDefecto;
        if (imagenBase64 && imagenBase64.trim() !== '') {
            // Si es base64, usar data URI
            imgSrc = 'data:image/jpeg;base64,' + imagenBase64;
        } else if (imagen && imagen.trim() !== '') {
            // Si es una ruta, usar BASE para construir la URL completa
            imgSrc = imagen.startsWith('http') ? imagen : (BASE + '/' + imagen);
        }

        const col = document.createElement('div');
        col.classList.add('col');

        col.innerHTML = `
        <a href="${BASE}/propuestas/${encodeURIComponent(titulo)}" class="text-decoration-none text-dark">
            <div class="card h-100 border p-2 shadow-sm hover-shadow">
                <img src="${imgSrc}" class="card-img-top" alt="${titulo}" style="height: 150px; object-fit: cover;">
                <div class="card-body p-2">
                    <h6 class="card-title fw-bold mb-1" style="font-size: 14px;">${titulo}</h6>
                    <p class="card-text text-muted mb-2" style="font-size: 12px;">${(descripcion || '').substring(0, 120)}...</p>

                    <div class="d-flex align-items-center mb-1">
                        <i class="bi bi-wallet-fill me-1" style="font-size: 14px;"></i>
                        <span class="fw-bold" style="font-size: 14px;">Recaudado: ${formatMonto(montoRecaudado)}</span>
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
                            <div class="fw-bold fs-5">${cantColaboradores}</div>
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
        listaFiltrada = listaFiltrada.filter(p => {
            const estado = p.estadoActual || p.getEstado?.() || p.estado || '';
            return estado === estadoActivo;
        });
    }

    if (categoriasSeleccionadas.length > 0) {
        listaFiltrada = listaFiltrada.filter(p => {
            const propCat = p.categoria || p.getCategoria?.() || '';

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
