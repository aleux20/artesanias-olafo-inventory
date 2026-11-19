<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${modoEdicion ? 'Editar producto' : 'Nuevo producto'}"/></title>
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
        <p class="section-eyebrow mb-1">Catálogo</p>
        <h1 class="h2 mb-0"><c:out value="${modoEdicion ? 'Editar producto' : 'Nuevo producto'}"/></h1>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-danger" role="alert"><c:out value="${error}"/></div>
    </c:if>

    <section class="card border-0 shadow-sm form-card">
        <div class="card-body p-4">
            <form method="post" action="${pageContext.request.contextPath}/productos">
                <c:choose>
                    <c:when test="${modoEdicion}">
                        <input type="hidden" name="accion" value="actualizar">
                        <input type="hidden" name="idProducto" value="${idProducto}">
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="accion" value="guardar">
                    </c:otherwise>
                </c:choose>

                <div class="mb-3">
                    <label class="form-label" for="nombre">Nombre</label>
                    <input class="form-control" id="nombre" name="nombre" maxlength="100" required
                           value="<c:out value='${modoEdicion ? producto.nombre : nombre}'/>">
                </div>

                <div class="mb-3">
                    <label class="form-label" for="descripcion">Descripción</label>
                    <textarea class="form-control" id="descripcion" name="descripcion" rows="3"><c:out value="${modoEdicion ? producto.descripcion : descripcion}"/></textarea>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label" for="precioVenta">Precio de venta</label>
                        <input class="form-control" id="precioVenta" name="precioVenta" type="number" step="0.01" min="0" required
                               value="<c:out value='${modoEdicion ? producto.precioVenta : precioVenta}'/>">
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label" for="stockActual">Stock actual</label>
                        <input class="form-control" id="stockActual" name="stockActual" type="number" min="0" required
                               value="<c:out value='${modoEdicion ? producto.stockActual : stockActual}'/>">
                    </div>
                </div>

                <div class="mb-4">
                    <label class="form-label" for="idCategoria">Categoría</label>
                    <select class="form-select" id="idCategoria" name="idCategoria" required>
                        <option value="">Seleccione una categoría</option>
                        <c:forEach var="categoria" items="${categorias}">
                            <c:set var="seleccionada"
                                   value="${modoEdicion ? producto.categoria.idCategoria : idCategoria}"/>
                            <option value="${categoria.idCategoria}"
                                    <c:if test="${seleccionada == categoria.idCategoria}">selected</c:if>>
                                <c:out value="${categoria.nombre}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="d-flex flex-wrap gap-2">
                    <button class="btn btn-primary" type="submit">Guardar producto</button>
                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/productos">Cancelar</a>
                </div>
            </form>
        </div>
    </section>
</main>
<%@ include file="/WEB-INF/views/fragmentos/pie.jsp" %>
</body>
</html>
