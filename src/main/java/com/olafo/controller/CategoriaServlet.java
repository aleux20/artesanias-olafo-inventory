package com.olafo.controller;

import com.olafo.model.Categoria;
import com.olafo.service.CategoriaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/categorias")
public class CategoriaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private CategoriaService categoriaService;

    @Override
    public void init() throws ServletException {
        categoriaService = new CategoriaService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("nuevo".equals(accion)) {
            mostrarFormulario(request, response);
        } else if ("editar".equals(accion)) {
            mostrarCategoriaParaEditar(request, response);
        } else {
            listarCategorias(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");

        if ("guardar".equals(accion)) {
            guardarCategoria(request, response);
        } else if ("actualizar".equals(accion)) {
            actualizarCategoria(request, response);
        } else if ("eliminar".equals(accion)) {
            eliminarCategoria(request, response);
        } else {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Operación no válida.");
        }
    }

    private void listarCategorias(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Categoria> categorias = categoriaService.listar();
            request.setAttribute("categorias", categorias);
            request.getRequestDispatcher("/WEB-INF/views/categorias/lista.jsp")
                    .forward(request, response);
        } catch (SQLException e) {
            registrarError(request, "Error al consultar categorías", e);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible consultar las categorías.");
        }
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/views/categorias/formulario.jsp")
                .forward(request, response);
    }

    private void mostrarCategoriaParaEditar(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        Integer id = leerId(request.getParameter("id"));
        if (id == null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "El ID de la categoría no es válido.");
            return;
        }

        try {
            Categoria categoria = categoriaService.buscarPorId(id);
            if (categoria == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "La categoría no existe.");
                return;
            }

            request.setAttribute("modoEdicion", true);
            request.setAttribute("idCategoria", categoria.getIdCategoria());
            request.setAttribute("nombre", categoria.getNombre());
            request.setAttribute("descripcion", categoria.getDescripcion());
            mostrarFormulario(request, response);
        } catch (SQLException e) {
            registrarError(request, "Error al consultar categoría", e);
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "No fue posible consultar la categoría.");
        }
    }

    private void guardarCategoria(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");

        try {
            Categoria categoria = new Categoria();
            categoria.setNombre(nombre);
            categoria.setDescripcion(descripcion);
            categoriaService.registrar(categoria);

            response.sendRedirect(
                    request.getContextPath() + "/categorias?registro=exitoso");
        } catch (IllegalArgumentException e) {
            conservarFormulario(request, nombre, descripcion, e.getMessage());
            mostrarFormulario(request, response);
        } catch (SQLException e) {
            registrarError(request, "Error al registrar categoría", e);
            conservarFormulario(
                    request,
                    nombre,
                    descripcion,
                    "No fue posible registrar la categoría.");
            mostrarFormulario(request, response);
        }
    }

    private void actualizarCategoria(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer id = leerId(request.getParameter("idCategoria"));
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");

        if (id == null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "El ID de la categoría no es válido.");
            return;
        }

        try {
            Categoria existente = categoriaService.buscarPorId(id);
            if (existente == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "La categoría no existe.");
                return;
            }

            existente.setNombre(nombre);
            existente.setDescripcion(descripcion);
            categoriaService.actualizar(existente);

            response.sendRedirect(
                    request.getContextPath() + "/categorias?actualizacion=exitosa");
        } catch (IllegalArgumentException e) {
            conservarFormulario(request, nombre, descripcion, e.getMessage());
            request.setAttribute("modoEdicion", true);
            request.setAttribute("idCategoria", id);
            mostrarFormulario(request, response);
        } catch (SQLException e) {
            registrarError(request, "Error al actualizar categoría", e);
            conservarFormulario(
                    request,
                    nombre,
                    descripcion,
                    "No fue posible actualizar la categoría.");
            request.setAttribute("modoEdicion", true);
            request.setAttribute("idCategoria", id);
            mostrarFormulario(request, response);
        }
    }

    private void eliminarCategoria(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer id = leerId(request.getParameter("idCategoria"));
        if (id == null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "El ID de la categoría no es válido.");
            return;
        }

        try {
            categoriaService.eliminar(id);
            response.sendRedirect(
                    request.getContextPath() + "/categorias?eliminacion=exitosa");
        } catch (IllegalArgumentException e) {
            response.sendRedirect(
                    request.getContextPath() + "/categorias?eliminacion=error");
        } catch (SQLException e) {
            registrarError(request, "Error al eliminar categoría", e);
            response.sendRedirect(
                    request.getContextPath() + "/categorias?eliminacion=error");
        }
    }

    private Integer leerId(String texto) {
        try {
            int id = Integer.parseInt(texto);
            return id > 0 ? id : null;
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    private void conservarFormulario(HttpServletRequest request,
            String nombre,
            String descripcion,
            String error) {
        request.setAttribute("nombre", nombre);
        request.setAttribute("descripcion", descripcion);
        request.setAttribute("error", error);
    }

    private void registrarError(HttpServletRequest request, String mensaje, Exception e) {
        getServletContext().log(mensaje + " [" + request.getRequestURI() + "]", e);
    }
}
