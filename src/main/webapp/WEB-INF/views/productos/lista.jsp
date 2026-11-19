<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Productos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css?v=2">
</head>
<body>
<%@ include file="/WEB-INF/views/fragmentos/encabezado.jsp" %>

<main class="container py-4">
    <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4">
        <div>
            <p class="section-eyebrow mb-1">Catálogo</p>
            <h1 class="h2 mb-0">Productos terminados</h1>
        </div>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/productos?accion=nuevo">Nuevo producto</a>
    </div>

    <c:if test="${param.registro == 'exitoso'}">
        <div class="alert alert-success" role="status">Producto registrado correctamente.</div>
    </c:if>
    <c:if test="${param.actualizacion == 'exitosa'}">
        <div class="alert alert-success" role="status">Producto actualizado correctamente.</div>
    </c:if>
    <c:if test="${param.desactivacion == 'exitosa'}">
        <div class="alert alert-success" role="status">Producto desactivado correctamente.</div>
    </c:if>
    <c:if test="${param.desactivacion == 'error'}">
        <div class="alert alert-danger" role="alert">No fue posible desactivar el producto.</div>
    </c:if>

    <section class="card border-0 shadow-sm mb-4">
        <div class="card-body">
            <form class="row g-3 align-items-end" method="get" action="${pageContext.request.contextPath}/productos">
                <input type="hidden" name="accion" value="stock-bajo">
                <div class="col-sm-8 col-md-5">
                    <label class="form-label" for="umbral">Consultar productos con stock menor o igual a</label>
                    <input class="form-control" id="umbral" name="umbral" type="number" min="0"
                           value="${empty umbral ? 10 : umbral}">
                </div>
                <div class="col-sm-auto">
                    <button class="btn btn-outline-primary" type="submit">Consultar stock</button>
                </div>
            </form>
        </div>
    </section>

    <section class="card border-0 shadow-sm">
        <div class="card-header bg-white py-3">
            <h2 class="h5 mb-0">Listado de productos</h2>
        </div>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                <tr>
                    <th scope="col">ID</th>
                    <th scope="col">Nombre</th>
                    <th scope="col">Categoría</th>
                    <th scope="col">Precio</th>
                    <th scope="col">Stock</th>
                    <th scope="col">Estado</th>
                    <th scope="col">Acciones</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="producto" items="${productos}">
                    <tr>
                        <td><c:out value="${producto.idProducto}"/></td>
                        <td class="fw-medium"><c:out value="${producto.nombre}"/></td>
                        <td><c:out value="${producto.categoria.nombre}"/></td>
                        <td><c:out value="${producto.precioVenta}"/></td>
                        <td><c:out value="${producto.stockActual}"/></td>
                        <td>
                            <span class="badge ${producto.activo ? 'text-bg-success' : 'text-bg-secondary'}">
                                <c:out value="${producto.activo ? 'Activo' : 'Inactivo'}"/>
                            </span>
                        </td>
                        <td>
                            <div class="d-flex flex-wrap gap-2">
                                <a class="btn btn-sm btn-outline-primary"
                                   href="${pageContext.request.contextPath}/productos?accion=editar&amp;id=${producto.idProducto}">Editar</a>
                                <c:if test="${producto.activo}">
                                    <form method="post" action="${pageContext.request.contextPath}/productos">
                                        <input type="hidden" name="accion" value="desactivar">
                                        <input type="hidden" name="idProducto" value="${producto.idProducto}">
                                        <button class="btn btn-sm btn-outline-danger" type="submit"
                                                onclick="return confirm('¿Desactivar este producto?');">Desactivar</button>
                                    </form>
                                </c:if>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty productos}">
                    <tr><td class="text-center text-secondary py-4" colspan="7">No hay productos para mostrar.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </section>
</main>
<%@ include file="/WEB-INF/views/fragmentos/pie.jsp" %>
</body>
</html>
