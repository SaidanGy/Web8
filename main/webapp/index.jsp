<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>JSP - BIENVENIDO</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container mt-5 text-center">

<div class="p-5 mb-4 bg-light rounded-3 shadow-sm">
    <h1 class="display-5 fw-bold">BASE DE DATOS - CONFIGURACIONES - APLICACIONES</h1>
    <p class="col-md-8 fs-4 mx-auto">Sistema de gestión de parámetros y entornos de desarrollo.</p>
    <hr class="my-4">

    <a href="${pageContext.request.contextPath}/configuraciones/ConfiguracionController" class="btn btn-primary btn-lg px-4">
        <i class="bi bi-table"></i> VER COLECCIÓN
    </a>
</div>

</body>
</html>