<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Listado de Configuraciones</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
</head>
<body>
<%
    // Recuperamos el mensaje de éxito/error del Servlet
    String mensaje = (String) request.getAttribute("mensaje");
%>

<div class="container mt-5">

    <h1 class="mb-4">Configuraciones de Aplicación</h1>

    <c:if test="${mensaje != null}">
        <div class="alert alert-info alert-dismissible fade show" role="alert">
            <%= mensaje %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <form action="ConfiguracionController" method="GET">
        <input type="hidden" name="accion" value="agregar">

        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-dark">
                <tr>
                    <th scope="col">ID</th>
                    <th scope="col">Entorno</th>
                    <th scope="col">Módulo</th>
                    <th scope="col">Parámetros</th>
                    <th scope="col">Versión</th>
                    <th scope="col">Estado</th>
                    <th scope="col" class="text-center">Opciones</th>
                </tr>
                </thead>
                <tbody class="table-group-divider">
                <c:forEach var="config" items="${lista_configs}">
                    <tr>
                        <th scope="row" class="text-muted small">${config.id}</th>
                        <td>
                                        <span class="badge ${config.entorno == 'prod' ? 'bg-danger' : 'bg-info'}">
                                                ${config.entorno}
                                        </span>
                        </td>
                        <td><strong>${config.modulo}</strong></td>
                        <td class="small text-truncate" style="max-width: 200px;">
                                ${config.parametros}
                        </td>
                        <td>v${config.version_config}</td>
                        <td>
                            <c:choose>
                                <c:when test="${config.activo}">
                                    <span class="text-success"><i class="bi bi-check-circle-fill"></i> Activo</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-secondary"><i class="bi bi-x-circle"></i> Inactivo</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-center">
                            <div class="btn-group" role="group">
                                <a href="${pageContext.request.contextPath}/configuraciones/ConfiguracionController?accion=editar&codigo=${config.id}"
                                   class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-pencil"></i> Editar
                                </a>

                                <a href="${pageContext.request.contextPath}/configuraciones/ConfiguracionController?accion=eliminar&codigo=${config.id}"
                                   class="btn btn-sm btn-outline-danger"
                                   onclick="return confirm('¿Seguro que deseas eliminar esta configuración?')">
                                    <i class="bi bi-trash"></i> Eliminar
                                </a>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="mt-4">
            <button type="submit" class="btn btn-success btn-lg">
                <i class="bi bi-plus-lg"></i> Crear Nueva Configuración
            </button>
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-outline-secondary btn-lg">
                <i class="bi bi-house"></i> Inicio
            </a>
        </div>

    </form>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>