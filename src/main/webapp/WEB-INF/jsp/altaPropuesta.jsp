<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Crear Propuesta - Culturarte</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/altaUsuario.css">
    <link href="https://fonts.googleapis.com/css2?family=Pacifico&display=swap" rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</head>
<body class="bg-light">
    <h1>Crear Nueva Propuesta</h1>
    <div class="culturarte">Culturarte</div>
    <form action="${pageContext.request.contextPath}/propuestas/alta" method="post" enctype="multipart/form-data">

        <div class="formulario">
            <label for="titulo">Título de la Propuesta </label>
            <input id="titulo" name="titulo" type="text" maxlength="100"
        </div>

        <div class="formulario">
            <label for="descripcion">Descripción </label>
            <textarea id="descripcion" name="descripcion" maxlength="500" required>${descripcion != null ? descripcion : ''}</textarea>
        </div>

        <div class="formulario">
            <label for="lugar">Lugar</label>
            <input id="lugar" name="lugar" type="text" maxlength="100" value="${lugar != null ? lugar : ''}" required>
        </div>

        <div class="formulario">
        <label for="fecha">Fecha </label>
        <input id="fecha"  name="fecha" type="date" value="${date}" required>
        </div>

        <div class="formulario">
            <label for="categoria">Categoría </label>
            <select id="categoria" name="categoria" required>
                <option value="">Selecciona una categoría</option>
                <option value="CARNAVAL" ${categoria == 'CARNAVAL' ? 'selected' : ''}>Carnaval</option>
                <option value="CINE" ${categoria == 'CINE' ? 'selected' : ''}>Cine</option>
                <option value="DANZA" ${categoria == 'DANZA' ? 'selected' : ''}>Danza</option>
                <option value="LITERATURA"${categoria == 'LITERATURA' ? 'selected' : ''}>Literatura</option>
                <option value="MUSICA" ${categoria == 'MUSICA' ? 'selected' : ''}>Música</option>
                <option value="TEATRO" ${categoria == 'TEATRO' ? 'selected' : ''}>Teatro</option>



            </select>
        </div>

        <div class="formulario">
            <label for="Tipo de retorno">Tipo de retorno </label>
            <select id="tipoRetorno" name="tipoRetorno" required>
                <option value="">Selecciona un tipo de retorno</option>
                <option value="ENTRADA GRATIS" ${tipoRetorno == 'ENTRADA GRATIS' ? 'selected' : ''}>Entrada Gratis</option>
                <option value="PORCENTAJE DE GANANCIAS" ${tipoRetorno == 'PORCENTAJE DE GANANCIAS' ? 'selected' : ''}>Porcentaje de Ganancias</option>
                </select>
        </div>

        <div class="formulario">
            <label for="montoEntrada">Precio por Entrada (UYU) </label>
            <input id="montoEntrada" name="montoEntrada" type="number" min="1" value="${montoEntrada != null ? montoEntrada : ''}" required>
        </div>

        <div class="formulario">
            <label for="montoNecesario">Monto Total Necesario (UYU) </label>
            <input id="montoNecesario" name="montoNecesario" type="number" min="1" value="${montoNecesario != null ? montoNecesario : ''}" required>
        </div>

        <div class="formulario">
            <label for="imagen">Subir imagen:</label>
            <input type="file" id="imagen" name="imagen" accept="image/*">
            </div>
        </div>

        <button type="submit">Crear Propuesta</button>

    </form>
</body>
</html>