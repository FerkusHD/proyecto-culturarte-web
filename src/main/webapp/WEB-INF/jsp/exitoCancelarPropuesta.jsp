<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Propuesta Cancelada - Culturarte</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
</head>
<body class="bg-light">
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card shadow">
                    <div class="card-body text-center p-5">
                        <i class="bi bi-check-circle-fill text-success display-1"></i>
                        <h2 class="mt-3 text-success">¡Propuesta Cancelada!</h2>
                        <p class="lead">${mensaje}</p>
                        <a href="${pageContext.request.contextPath}/" class="btn btn-primary mt-3">Ir al inicio</a>

                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
