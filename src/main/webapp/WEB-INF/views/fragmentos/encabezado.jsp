<%@ page pageEncoding="UTF-8" %>
<nav class="navbar navbar-expand-lg navbar-dark app-navbar">
    <div class="container">
        <a class="navbar-brand fw-semibold" href="${pageContext.request.contextPath}/">Olafo <span>· Inventario</span></a>
        <div class="navbar-nav ms-auto">
            <a class="nav-link ${pageContext.request.requestURI.endsWith('/categorias') ? 'active' : ''}"
               href="${pageContext.request.contextPath}/categorias"
               ${pageContext.request.requestURI.endsWith('/categorias') ? 'aria-current="page"' : ''}>Categorías</a>
            <a class="nav-link ${pageContext.request.requestURI.endsWith('/productos') ? 'active' : ''}"
               href="${pageContext.request.contextPath}/productos"
               ${pageContext.request.requestURI.endsWith('/productos') ? 'aria-current="page"' : ''}>Productos</a>
        </div>
    </div>
</nav>
