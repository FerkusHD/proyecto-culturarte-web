<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<title>Culturarte</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/principal.css">

<link href="https://fonts.googleapis.com/css2?family=Pacifico&display=swap" rel="stylesheet">
</head>

<body>
<header>
    <div class="culturarte">Culturarte</div>
    <nav>
        <a href="#">Tengo una Propuesta</a>
        <a href="#">Quiero ver Propuestas</a>
        <input type="text" placeholder="Título, descripción, lugar">
        <button>Buscar</button>
        <a href="${pageContext.request.contextPath}/altaUsuario">Registrarse</a>
        <a href="${pageContext.request.contextPath}/inicioSesion">Entrar</a>
    </nav>
</header>

<!-- Pestañas -->
<div class="tabs">
    <button>Propuestas Creadas</button>
    <button>Propuestas en Financiación</button>
    <button>Propuestas Financiadas</button>
    <button>Propuestas No Financiadas</button>
    <button>Propuestas Canceladas</button>
</div>

<!-- Tarjetas -->
<section class="tarjetas">
    <div class="card">
        <img src="evento1.jpg" alt="Evento 1">
        <h3>Cirque du Soleil</h3>
        <p>Escribí grupos de fans...</p>
        <p><strong>Recaudado:</strong> $2.175.000 UYI</p>
        <p>26 días | 350 aportes</p>
    </div>

    <div class="card">
        <img src="evento2.jpg" alt="Evento 2">
        <h3>La Vida Puerca en el MOVIE</h3>
        <p>La banda como nunca la viste...</p>
        <p><strong>Recaudado:</strong> $801.500 UYI</p>
        <p>7 días | 450 aportes</p>
    </div>

    <div class="card">
        <img src="evento3.jpg" alt="Evento 3">
        <h3>El Perenne Infinito</h3>
        <p>Una obra inmortal...</p>
        <p><strong>Recaudado:</strong> $2.890.000 UYI</p>
        <p>12 días | 2 aportes</p>
    </div>
</section>

<!-- Categorías -->
<section class="categorias">
    <h4>Categorías</h4>
    <label><input type="checkbox"> Teatro</label>
    <label><input type="checkbox"> Comedia</label>
    <label><input type="checkbox"> Literatura</label>
    <label><input type="checkbox"> Música</label>
    <label><input type="checkbox"> Cine</label>
    <label><input type="checkbox"> Danza</label>
    <label><input type="checkbox"> Carnaval</label>
</section>
</body>
</html>