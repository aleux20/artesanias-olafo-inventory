<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Inventario Olafo</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css?v=2">
</head>
<body>
<%@ include file="/WEB-INF/views/fragmentos/encabezado.jsp" %>

<main class="container py-5">
    <section class="hero-panel p-4 p-md-5 mb-4">
        <p class="text-uppercase small fw-semibold mb-2">Artesanías Olafo</p>
        <h1 class="display-5 fw-bold mb-3">Inventario bajo control</h1>
        <p class="lead mb-0">Administra tus productos terminados y organiza tu catálogo de categorías desde un solo lugar.</p>
    </section>

    <div class="row g-4">
        <div class="col-md-6">
            <section class="card h-100 dashboard-card">
                <div class="card-body p-4">
                    <h2 class="h4 card-title">Productos</h2>
                    <p class="card-text text-secondary">Consulta existencias, revisa el stock y administra tus productos.</p>
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/productos">Ir a productos</a>
                </div>
            </section>
        </div>
        <div class="col-md-6">
            <section class="card h-100 dashboard-card">
                <div class="card-body p-4">
                    <h2 class="h4 card-title">Categorías</h2>
                    <p class="card-text text-secondary">Organiza el catálogo para encontrar tus productos fácilmente.</p>
                    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/categorias">Gestionar categorías</a>
                </div>
            </section>
        </div>
    </div>
</main>
<%@ include file="/WEB-INF/views/fragmentos/pie.jsp" %>
</body>
</html>
