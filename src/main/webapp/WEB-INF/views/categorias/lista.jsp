<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Categorías</title>
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
            <p class="section-eyebrow mb-1">Organización</p>
            <h1 class="h2 mb-0">Categorías</h1>
        </div>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/categorias?accion=nuevo">Nueva categoría</a>
    </div>

    <c:if test="${param.registro == 'exitoso'}">
        <div class="alert alert-success" role="status">Categoría registrada correctamente.</div>
    </c:if>
    <c:if test="${param.actualizacion == 'exitosa'}">
        <div class="alert alert-success" role="status">Categoría actualizada correctamente.</div>
    </c:if>
    <c:if test="${param.eliminacion == 'exitosa'}">
        <div class="alert alert-success" role="status">Categoría eliminada correctamente.</div>
    </c:if>
    <c:if test="${param.eliminacion == 'error'}">
        <div class="alert alert-danger" role="alert">No fue posible eliminar la categoría. Puede tener productos asociados.</div>
    </c:if>

    <section class="card border-0 shadow-sm">
        <div class="card-header bg-white py-3">
            <h2 class="h5 mb-0">Listado de categorías</h2>
        </div>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                <tr>
                    <th scope="col">ID</th>
                    <th scope="col">Nombre</th>
                    <th scope="col">Descripción</th>
                    <th scope="col">Acciones</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="categoria" items="${categorias}">
                    <tr>
                        <td><c:out value="${categoria.idCategoria}"/></td>
                        <td class="fw-medium"><c:out value="${categoria.nombre}"/></td>
                        <td><c:out value="${empty categoria.descripcion ? '-' : categoria.descripcion}"/></td>
                        <td>
                            <div class="d-flex flex-wrap gap-2">
                                <a class="btn btn-sm btn-outline-primary"
                                   href="${pageContext.request.contextPath}/categorias?accion=editar&amp;id=${categoria.idCategoria}">Editar</a>
                                <form method="post" action="${pageContext.request.contextPath}/categorias">
                                    <input type="hidden" name="accion" value="eliminar">
                                    <input type="hidden" name="idCategoria" value="${categoria.idCategoria}">
                                    <button class="btn btn-sm btn-outline-danger" type="submit"
                                            onclick="return confirm('¿Eliminar esta categoría?');">Eliminar</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty categorias}">
                    <tr><td class="text-center text-secondary py-4" colspan="4">No hay categorías para mostrar.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </section>
</main>
<%@ include file="/WEB-INF/views/fragmentos/pie.jsp" %>
</body>
</html>
