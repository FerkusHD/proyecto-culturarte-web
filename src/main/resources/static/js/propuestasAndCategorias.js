let todasLasPropuestas = [];

// === CATEGORÍAS ===
fetch('/categorias/lista')
  .then(response => response.json())
  .then(data => {
    const contenedor = document.getElementById('categorias');
    contenedor.innerHTML = '';

    contenedor.style.display = 'flex';
    contenedor.style.flexWrap = 'wrap';
    contenedor.style.justifyContent = 'center';
    contenedor.style.gap = '10px';
    contenedor.style.margin = '20px';
    contenedor.style.textAlign = 'center';

    data.forEach(nombre => {
      const label = document.createElement('label');
      label.style.display = 'flex';
      label.style.alignItems = 'center';
      label.style.gap = '5px';
      label.style.cursor = 'pointer';

      const checkbox = document.createElement('input');
      checkbox.type = 'checkbox';
      checkbox.name = 'categorias';
      checkbox.value = nombre;

      checkbox.addEventListener('change', filtrarPropuestas);

      const texto = document.createElement('span');
      texto.textContent = nombre;

      label.appendChild(checkbox);
      label.appendChild(texto);
      contenedor.appendChild(label);
    });
  });

// === PROPUESTAS ===
fetch('/propuestas/listar')
  .then(response => response.json())
  .then(data => {
    todasLasPropuestas = data; 
    mostrarPropuestas(data); 
  })
  .catch(err => console.error("Error cargando propuestas:", err));

function mostrarPropuestas(lista) {
  const contenedor = document.getElementById('tarjetas');
  contenedor.innerHTML = '';

  lista.forEach(p => {
    const card = document.createElement('div');
    card.classList.add('card');
    card.innerHTML = `
      <img src="${p.imagen}">
      <h3>${p.titulo}</h3>
      <p>${p.descripcion}</p>
      <p><strong>Categoría:</strong> ${p.categoria}</p>
      <p><strong>Estado:</strong> ${p.estadoActual}</p>
      <p><strong>Colaboradores:</strong> ${p.nicknameColaboradores}</p>
      <p><strong>Monto recaudado:</strong> ${p.montoRecaudado}</p>
      <p><strong>Monto necesario:</strong> ${p.montoNecesario}</p>
      <p><strong>Fecha prevista:</strong> ${p.fechaPrevista}</p>
    `;
    contenedor.appendChild(card);
  });
}

function filtrarPropuestas() {
  const seleccionadas = Array.from(document.querySelectorAll('input[name="categorias"]:checked'))
                             .map(c => c.value);

  if (seleccionadas.length === 0) {
    mostrarPropuestas(todasLasPropuestas);
  } else {
    const filtradas = todasLasPropuestas.filter(p => seleccionadas.includes(p.categoria));
    mostrarPropuestas(filtradas);
  }
}
