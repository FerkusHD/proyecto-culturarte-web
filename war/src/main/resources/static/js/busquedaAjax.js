document.addEventListener("DOMContentLoaded", () => {
    // Seleccionamos el input (index o buscar)
    const input = document.querySelector("input[name='query'], input[name='texto']");
    const sugerenciasBox = document.getElementById("sugerencias");

    if (!input || !sugerenciasBox) return;

    const basePath = window.location.origin + window.location.pathname.split('/')[1] === 'propuestas'
        ? '/propuestas/buscar/sugerencias'
        : '/propuestas/buscar/sugerencias';

    let timeout = null;

    input.addEventListener("keyup", () => {
        const query = input.value.trim();
        if (!query) {
            sugerenciasBox.innerHTML = "";
            return;
        }

        clearTimeout(timeout);
        timeout = setTimeout(() => {
            fetch(`${window.location.origin}${basePath}?q=${encodeURIComponent(query)}`)
                .then(resp => resp.json())
                .then(data => {
                    sugerenciasBox.innerHTML = "";
                    if (!data.length) return;

                    data.forEach(titulo => {
                        const item = document.createElement("a");
                        item.className = "list-group-item list-group-item-action";
                        item.textContent = titulo;
                        // redirigir al buscar con el query
                        item.href = `${window.location.origin}/propuestas/buscar?query=${encodeURIComponent(titulo)}`;
                        item.addEventListener("click", e => {
                            e.preventDefault();
                            input.value = titulo;
                            sugerenciasBox.innerHTML = "";
                            input.closest("form").submit();
                        });
                        sugerenciasBox.appendChild(item);
                    });
                })
                .catch(err => console.error(err));
        }, 300);
    });

    document.addEventListener("click", e => {
        if (!sugerenciasBox.contains(e.target) && e.target !== input) {
            sugerenciasBox.innerHTML = "";
        }
    });
});
