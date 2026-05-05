<%@ page import="org.example.base2.modelo.ConfiguracionRequest" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Formulario de Configuración</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%
  ConfiguracionRequest configReq = (ConfiguracionRequest) request.getAttribute("config");
  if (configReq == null) {
    configReq = new ConfiguracionRequest();
  }
  String errores = (String) request.getAttribute("errores");
%>
<div class="container mt-5">

  <h1 class="mb-4">Datos de la Configuración</h1>

  <c:if test="${errores != null}">
    <div class="alert alert-danger" role="alert">
      <%= errores %>
    </div>
  </c:if>

  <form class="row g-3" action="ConfiguracionController" method="GET">

    <input type="hidden" name="accion" value="guardar">

    <div class="col-md-6">
      <label for="codigo" class="form-label">ID de Configuración</label>
      <input type="text" class="form-control" id="codigo" name="codigo"
             readonly
             value="<%= configReq.getId() %>">
    </div>

    <div class="col-md-6">
      <label for="modulo" class="form-label">Módulo</label>
      <input type="text" class="form-control" id="modulo" name="modulo"
             value="<%= configReq.getModulo() %>" placeholder="Ej: Ventas, Seguridad...">
    </div>

    <div class="col-md-6">
      <label for="entorno" class="form-label">Entorno</label>
      <select class="form-select" id="entorno" name="entorno">
        <option value="dev" <%= "dev".equals(configReq.getEntorno()) ? "selected" : "" %>>Desarrollo (dev)</option>
        <option value="test" <%= "test".equals(configReq.getEntorno()) ? "selected" : "" %>>Pruebas (test)</option>
        <option value="prod" <%= "prod".equals(configReq.getEntorno()) ? "selected" : "" %>>Producción (prod)</option>
      </select>
    </div>

    <div class="col-md-6">
      <label for="version_config" class="form-label">Versión Config</label>
      <input type="text" class="form-control" id="version_config" name="version_config"
             value="<%= configReq.getVersion_config() %>" placeholder="Ej: 1.0.2">
    </div>

    <div class="col-12">
      <label for="parametros" class="form-label">Parámetros (Formato: clave=valor, clave2=valor2)</label>
      <textarea class="form-control" id="parametros" name="parametros" rows="3"
                placeholder="timeout=30, reintentos=5"><%= configReq.getParametros() %></textarea>
      <div class="form-text">Separa los parámetros por comas y usa el signo igual (=).</div>
    </div>

    <div class="col-12">
      <div class="form-check">
        <input class="form-check-input" type="checkbox" id="activo" name="activo" value="true"
          <%= "true".equals(configReq.getActivo()) ? "checked" : "" %>>
        <label class="form-check-label" for="activo">
          ¿Configuración Activa?
        </label>
      </div>
    </div>

    <div class="col-12 mt-4">
      <button type="submit" class="btn btn-primary">Guardar Configuración</button>
      <a href="${pageContext.request.contextPath}/configuraciones/ConfiguracionController" class="btn btn-secondary btn-lg">
        <i class="bi bi-arrow-left"></i> Regresar al Listado
      </a>
    </div>

  </form>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>