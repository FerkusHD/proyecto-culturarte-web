fetch('/categorias/lista')
  .then(response => response.json())
  .then(data => {
    const contenedor = document.getElementById('categorias');
    contenedor.innerHTML = '';

    contenedor.style.display = 'flex';
    contenedor.style.flexWrap = 'wrap';
    contenedor.style.justifyContent= 'center';
    contenedor.style.gap = '10px'; 
    contenedor.style.margin= '20px';
    contenedor.style.textAlign= 'center';
    contenedor.style.margin='10px';

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

      const texto = document.createElement('span');
      texto.textContent = nombre;

      label.appendChild(checkbox);
      label.appendChild(texto);
      contenedor.appendChild(label);
    });
  });

