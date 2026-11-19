<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${modoEdicion ? 'Editar categoría' : 'Nueva categoría'}"/></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css?v=2">
</head>
<body>
<%@ include file="/WEB-INF/views/fragmentos/encabezado.jsp" %>

<main class="container py-4">
    <div class="mb-4">
        <p class="section-eyebrow mb-1">Organización</p>
        <h1 class="h2 mb-0"><c:out value="${modoEdicion ? 'Editar categoría' : 'Nueva categoría'}"/></h1>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
    </c:if>

    <section class="card border-0 shadow-sm form-card">
        <div class="card-body p-4">
            <form method="post" action="${pageContext.request.contextPath}/categorias">
                <c:choose>
                    <c:when test="${modoEdicion}">
                        <input type="hidden" name="accion" value="actualizar">
                        <input type="hidden" name="idCategoria" value="${idCategoria}">
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="accion" value="guardar">
                    </c:otherwise>
                </c:choose>

                <div class="mb-3">
                    <label class="form-label" for="nombre">Nombre</label>
                    <input class="form-control" id="nombre" name="nombre" maxlength="50" required
                           value="<c:out value='${nombre}'/>">
                </div>

                <div class="mb-4">
                    <label class="form-label" for="descripcion">Descripción</label>
                    <textarea class="form-control" id="descripcion" name="descripcion" maxlength="255" rows="3"><c:out value="${descripcion}"/></textarea>
                </div>

                <div class="d-flex flex-wrap gap-2">
                    <button class="btn btn-primary" type="submit">Guardar categoría</button>
                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/categorias">Cancelar</a>
                </div>
            </form>
        </div>
    </section>
</main>
<%@ include file="/WEB-INF/views/fragmentos/pie.jsp" %>
</body>
</html>
