fetch('/propuestas/listar')
  .then(response => response.json())
  .then(data => {
      const contenedor = document.getElementById('tarjetas');
      contenedor.innerHTML = '';

      data.forEach(p => {
        const card = document.createElement('div');
        card.classList.add('card');
        card.innerHTML = `
          <img src="${p.imagen}">
          <h3>${p.titulo}</h3>
          <p>${p.descripcion}</p>
          <p><strong>Estado:</strong> ${p.estadoActual}</p>
          <p><strong>Colaboradores:</strong> ${p.nicknameColaboradores}</p>
          <p><strong>Monto recaudado:</strong> ${p.montoRecaudado}</p>
          <p><strong>Monto necesario:</strong> ${p.montoNecesario}</p>
          <p><strong>Fecha prevista:</strong> ${p.fechaPrevista}</p>
        `;
        contenedor.appendChild(card);
      });
    })
    .catch(err => console.error("Error cargando propuestas:", err));


