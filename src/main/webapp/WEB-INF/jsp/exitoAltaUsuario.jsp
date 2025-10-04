<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
    <title>Registro exitoso</title>
</head>
<body>
    <h2>${mensaje}</h2>

    <p>Tu cuenta fue creada correctamente.</p>

    <a href="${pageContext.request.contextPath}/login">Ir al login</a>
</body>
</html>