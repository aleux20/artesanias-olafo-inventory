package com.olafo.controller;

import com.olafo.model.Categoria;
import com.olafo.model.Producto;
import com.olafo.service.CategoriaService;
import com.olafo.service.ProductoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/productos")
public class ProductoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ProductoService productoService;
    private CategoriaService categoriaService;

    @Override
    public void init() throws ServletException {
        productoService = new ProductoService();
        categoriaService = new CategoriaService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("nuevo".equals(accion)) {
            mostrarFormulario(request, response);
        } else if ("editar".equals(accion)) {
            mostrarFormularioEditar(request, response);
        } else if ("stock-bajo".equals(accion)) {
            listarStockBajo(request, response);
        } else {
            listarProductos(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");

        if ("guardar".equals(accion)) {
            guardarProducto(request, response);
        } else if ("actualizar".equals(accion)) {
            actualizarProducto(request, response);
        } else if ("desactivar".equals(accion)) {
            desactivarProducto(request, response);
        } else {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Acción no válida.");
        }
    }

    private void listarProductos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            request.setAttribute("productos", productoService.listar());
            request.getRequestDispatcher("/WEB-INF/views/productos/lista.jsp")
                    .forward(request, response);
        } catch (SQLException e) {
            manejarErrorBD(request, "Error al listar productos", e);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible consultar los productos.");
        }
    }

    private void listarStockBajo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer umbral = leerEntero(request.getParameter("umbral"));
        if (umbral == null || umbral < 0) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "El umbral de stock no es válido.");
            return;
        }

        try {
            request.setAttribute("productos", productoService.listarStockBajo(umbral));
            request.setAttribute("umbral", umbral);
            request.getRequestDispatcher("/WEB-INF/views/productos/lista.jsp")
                    .forward(request, response);
        } catch (SQLException e) {
            manejarErrorBD(request, "Error al consultar stock bajo", e);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible consultar el stock bajo.");
        }
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            cargarCategorias(request);
            request.getRequestDispatcher("/WEB-INF/views/productos/formulario.jsp")
                    .forward(request, response);
        } catch (SQLException e) {
            manejarErrorBD(request, "Error al cargar categorías", e);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible cargar el formulario.");
        }
    }

    private void mostrarFormularioEditar(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Integer id = leerEntero(request.getParameter("id"));
        if (id == null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "El identificador del producto no es válido.");
            return;
        }

        try {
            Producto producto = productoService.buscarPorId(id);
            if (producto == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "El producto solicitado no existe.");
                return;
            }

            request.setAttribute("producto", producto);
            request.setAttribute("modoEdicion", true);
            request.setAttribute("idProducto", producto.getIdProducto());
            cargarCategorias(request);
            request.getRequestDispatcher("/WEB-INF/views/productos/formulario.jsp")
                    .forward(request, response);
        } catch (SQLException e) {
            manejarErrorBD(request, "Error al consultar producto", e);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible consultar el producto.");
        }
    }

    private void cargarCategorias(HttpServletRequest request) throws SQLException {
        request.setAttribute("categorias", categoriaService.listar());
    }

    private void guardarProducto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Producto producto = construirProducto(request);
            producto.setActivo(true);
            productoService.registrar(producto);

            response.sendRedirect(
                    request.getContextPath() + "/productos?registro=exitoso");
        } catch (IllegalArgumentException e) {
            conservarDatosFormulario(request, e.getMessage());
            mostrarFormulario(request, response);
        } catch (SQLException e) {
            manejarErrorBD(request, "Error al registrar producto", e);
            conservarDatosFormulario(
                    request,
                    "No fue posible registrar el producto.");
            mostrarFormulario(request, response);
        }
    }

    private void actualizarProducto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer id = leerEntero(request.getParameter("idProducto"));
        if (id == null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "El identificador del producto no es válido.");
            return;
        }

        try {
            Producto existente = productoService.buscarPorId(id);
            if (existente == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "El producto no existe.");
                return;
            }

            Producto producto = construirProducto(request);
            producto.setIdProducto(id);
            producto.setActivo(existente.isActivo());
            producto.setFechaCreacion(existente.getFechaCreacion());
            productoService.actualizar(producto);

            response.sendRedirect(
                    request.getContextPath() + "/productos?actualizacion=exitosa");
        } catch (IllegalArgumentException e) {
            conservarDatosFormulario(request, e.getMessage());
            request.setAttribute("modoEdicion", true);
            request.setAttribute("idProducto", id);
            mostrarFormulario(request, response);
        } catch (SQLException e) {
            manejarErrorBD(request, "Error al actualizar producto", e);
            conservarDatosFormulario(
                    request,
                    "No fue posible actualizar el producto.");
            request.setAttribute("modoEdicion", true);
            request.setAttribute("idProducto", id);
            mostrarFormulario(request, response);
        }
    }

    private void desactivarProducto(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer id = leerEntero(request.getParameter("idProducto"));
        if (id == null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "El identificador del producto no es válido.");
            return;
        }

        try {
            productoService.desactivar(id);
            response.sendRedirect(
                    request.getContextPath() + "/productos?desactivacion=exitosa");
        } catch (IllegalArgumentException e) {
            response.sendRedirect(
                    request.getContextPath() + "/productos?desactivacion=error");
        } catch (SQLException e) {
            manejarErrorBD(request, "Error al desactivar producto", e);
            response.sendRedirect(
                    request.getContextPath() + "/productos?desactivacion=error");
        }
    }

    private Producto construirProducto(HttpServletRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.getParameter("nombre"));
        producto.setDescripcion(request.getParameter("descripcion"));

        producto.setPrecioVenta(leerDecimal(request.getParameter("precioVenta")));
        producto.setStockActual(leerEnteroObligatorio(request.getParameter("stockActual")));

        Integer idCategoria;
        try {
            idCategoria = Integer.parseInt(
                    request.getParameter("idCategoria"));
            if (idCategoria <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una categoría válida.");
        }

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(idCategoria);
        producto.setCategoria(categoria);

        return producto;
    }

    private void conservarDatosFormulario(HttpServletRequest request, String error) {
        request.setAttribute("nombre", request.getParameter("nombre"));
        request.setAttribute("descripcion", request.getParameter("descripcion"));
        request.setAttribute("precioVenta", request.getParameter("precioVenta"));
        request.setAttribute("stockActual", request.getParameter("stockActual"));
        request.setAttribute("idCategoria", request.getParameter("idCategoria"));
        request.setAttribute("error", error);
    }

    private Integer leerEntero(String texto) {
        try {
            int valor = Integer.parseInt(texto);
            return valor > 0 ? valor : null;
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    private int leerEnteroObligatorio(String texto) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("El stock actual no es válido.");
        }
    }

    private BigDecimal leerDecimal(String texto) {
        try {
            return new BigDecimal(texto);
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("El precio de venta no es válido.");
        }
    }

    private void manejarErrorBD(HttpServletRequest request, String mensaje, SQLException e) {
        getServletContext().log(
                mensaje + " [" + request.getRequestURI() + "]",
                e);
    }
}
