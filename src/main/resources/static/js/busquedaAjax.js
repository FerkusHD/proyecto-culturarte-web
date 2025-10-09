document.addEventListener("DOMContentLoaded", () => {
    const input = document.getElementById("buscador");
    const sugerenciasBox = document.getElementById("sugerencias");

    if (!input || !sugerenciasBox) return;

    let timeout = null;

    input.addEventListener("keyup", () => {
        const query = input.value.trim();
        if (query.length === 0) {
            sugerenciasBox.innerHTML = "";
            return;
        }

        clearTimeout(timeout);
        timeout = setTimeout(() => {
            fetch(`${window.location.origin}${window.location.pathname.split("/")[1] ? "/" + window.location.pathname.split("/")[1] : ""}/propuestas/buscar/sugerencias?q=${encodeURIComponent(query)}`)
                .then(resp => resp.json())
                .then(data => {
                    sugerenciasBox.innerHTML = "";

                    if (data.length === 0) return;

                    data.forEach(titulo => {
                        const item = document.createElement("a");
                        item.className = "list-group-item list-group-item-action";
                        item.textContent = titulo;
                        item.href = `${window.location.origin}${window.location.pathname.split("/")[1] ? "/" + window.location.pathname.split("/")[1] : ""}/propuestas/buscar?query=${encodeURIComponent(titulo)}`;

                        item.addEventListener("click", e => {
                            e.preventDefault();
                            input.value = titulo;
                            sugerenciasBox.innerHTML = "";
                            input.closest("form").submit(); // Buscar directamente
                        });

                        sugerenciasBox.appendChild(item);
                    });
                })
                .catch(err => console.error("Error cargando sugerencias:", err));
        }, 300);
    });

    document.addEventListener("click", e => {
        if (!sugerenciasBox.contains(e.target) && e.target !== input) {
            sugerenciasBox.innerHTML = "";
        }
    });
})