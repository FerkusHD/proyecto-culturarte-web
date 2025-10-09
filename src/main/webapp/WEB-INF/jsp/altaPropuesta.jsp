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
   <div class="container my-5">
       <div class="text-center mb-4">
           <h1 class="culturarte">Culturarte</h1>
           <h3>Crear Nueva Propuesta</h3>
       </div>
   <form action="${pageContext.request.contextPath}/propuestas/alta" method="post">

        <div class="mb-3">
            <label for="titulo" class="form-label">Título</label>
            <input id="titulo" name="titulo" type="text" maxlength="50" class="form-control " value="${titulo != null ? titulo : ''}" required>
        </div>

        <div class="mb-3">
            <label for="descripcion" class="form-label"  >Descripción </label>
            <textarea id="descripcion" name="descripcion" maxlength="500" class="form-control " required>${descripcion != null ? descripcion : ''}</textarea>
        </div>

        <div class="mb-3">
            <label for="lugar" class="form-label">Lugar</label>
            <input id="lugar" name="lugar" type="text" maxlength="100" class="form-control" value="${lugar != null ? lugar : ''}" required>
        </div>

        <div class="mb-3">
        <label for="fecha">Fecha </label>
        <input id="fecha"  name="fechaPrevista" type="date" class="form-control " value="${date}" required>
        </div>

        <div class="mb-3">
            <label for="categoria" class="form-label">Categoría </label>
            <select id="categoria" name="categoria" class="form-select" required>
                <option value="">Selecciona una categoría</option>
                <option value="CARNAVAL" ${categoria == 'CARNAVAL' ? 'selected' : ''}>Carnaval</option>
                <option value="CINE" ${categoria == 'CINE' ? 'selected' : ''}>Cine</option>
                <option value="DANZA" ${categoria == 'DANZA' ? 'selected' : ''}>Danza</option>
                <option value="LITERATURA"${categoria == 'LITERATURA' ? 'selected' : ''}>Literatura</option>
                <option value="MUSICA" ${categoria == 'MUSICA' ? 'selected' : ''}>Música</option>
                <option value="TEATRO" ${categoria == 'TEATRO' ? 'selected' : ''}>Teatro</option>



            </select>
        </div>

        <div class="mb-3">
            <label for="Tipo de retorno" class="form-label">Tipo de retorno </label>
            <select id="tipoRetorno" name="tipoRetorno" class="form-select" required>
                <option value="">Selecciona un tipo de retorno</option>
                <option value="ENTRADAGRATIS" ${tipoRetorno == 'ENTRADAGRATIS' ? 'selected' : ''}>Entrada Gratis</option>
                <option value="PORCENTAJEGANANCIAS" ${tipoRetorno == 'PORCENTAJEDEGANANCIAS' ? 'selected' : ''}>Porcentaje de Ganancias</option>
                </select>
        </div>

        <div class="mb-3">
            <label for="montoEntrada" class="form-label">Precio por Entrada (UYU) </label>
            <input id="montoEntrada" name="montoEntrada" type="number" min="1" class="form-control" value="${montoEntrada != null ? montoEntrada : ''}" required>
        </div>

        <div class="mb-3">
            <label for="montoNecesario" class="form-label">Monto Total Necesario (UYU) </label>
            <input id="montoNecesario" name="montoNecesario" type="number" min="1" class="form-control" value="${montoNecesario != null ? montoNecesario : ''}" required>
        </div>

        <div class="mb-3">
            <label for="imagen" class="form-label">Subir imagen:</label>
            <input type="file" id="imagen" name="imagen" accept="image/*">
            </div>

        <button type="submit" class="btn btn-success w-100">Crear Propuesta</button>


    </form>
    </div>
</body>
</html>