<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="robots" content="noindex">
    <title>Error | Inventario Olafo</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css?v=2">
</head>
<body>
<%@ include file="/WEB-INF/views/fragmentos/encabezado.jsp" %>

<main class="container py-5">
    <section class="card border-0 shadow-sm error-card mx-auto">
        <div class="card-body text-center p-4 p-md-5">
            <p class="section-eyebrow mb-2">
                Error
                <c:if test="${not empty requestScope['jakarta.servlet.error.status_code']}">
                    · <c:out value="${requestScope['jakarta.servlet.error.status_code']}"/>
                </c:if>
            </p>
            <h1 class="h2 mb-3">
                <c:choose>
                    <c:when test="${requestScope['jakarta.servlet.error.status_code'] == 404}">
                        Página no encontrada
                    </c:when>
                    <c:when test="${requestScope['jakarta.servlet.error.status_code'] == 500}">
                        Error del servidor
                    </c:when>
                    <c:otherwise>
                        No pudimos completar la solicitud
                    </c:otherwise>
                </c:choose>
            </h1>
            <p class="text-secondary mb-4">
                Puede que la página no exista o que haya ocurrido un problema. Intenta volver al inicio.
            </p>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/">Volver al inicio</a>
        </div>
    </section>
</main>

<%@ include file="/WEB-INF/views/fragmentos/pie.jsp" %>
</body>
</html>
