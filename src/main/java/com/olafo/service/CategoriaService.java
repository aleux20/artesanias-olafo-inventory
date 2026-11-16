package com.olafo.service;

import com.olafo.dao.CategoriaDAO;
import com.olafo.dao.impl.CategoriaDAOImpl;
import com.olafo.model.Categoria;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Objects;

/**
 * Capa de servicio del módulo de categorías.
 * Aplica las reglas de negocio y validaciones antes de llamar al DAO.
 * Los datos inválidos lanzan IllegalArgumentException; los fallos de
 * base de datos se propagan como SQLException.
 */
public class CategoriaService {
    // Límites definidos en la tabla categoria_producto.
    private static final int MAX_NOMBRE = 50;
    private static final int MAX_DESCRIPCION = 255;

    private final CategoriaDAO categoriaDAO;

    // Constructor por defecto
    public CategoriaService() {
        this(new CategoriaDAOImpl());
    }

    // Constructor con inyección de dependencias
    public CategoriaService(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = Objects.requireNonNull(
                categoriaDAO, "CategoriaDAO no puede ser nulo.");
    }

    // Registrar una categoría
    public void registrar(Categoria categoria) throws SQLException {

        validarCategoria(categoria);

        Categoria duplicada = buscarNombreExacto(categoria.getNombre());
        if (duplicada != null) {
            throw new IllegalArgumentException(
                    "Ya existe una categoría con ese nombre.");
        }

        categoriaDAO.guardar(categoria);
    }

    // Consultar todas las categorías
    public List<Categoria> listar() throws SQLException {
        return categoriaDAO.listar();
    }

    // Buscar categoría por ID
    public Categoria buscarPorId(int id) throws SQLException {
        validarId(id);
        return categoriaDAO.buscarPorId(id);
    }

    // Buscar categorías por nombre
    public List<Categoria> buscarPorNombre(String nombre) throws SQLException {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe ingresar un nombre para buscar.");
        }

        return categoriaDAO.buscarPorNombre(nombre.trim());
    }

    // Actualizar categoría
    public void actualizar(Categoria categoria) throws SQLException {

        validarCategoria(categoria);
        validarId(categoria.getIdCategoria());

        if (categoriaDAO.buscarPorId(categoria.getIdCategoria()) == null) {
            throw new IllegalArgumentException("La categoría no existe.");
        }

        // El nuevo nombre no puede pertenecer a otra categoría.
        Categoria conMismoNombre = buscarNombreExacto(categoria.getNombre());
        if (conMismoNombre != null
                && conMismoNombre.getIdCategoria() != categoria.getIdCategoria()) {
            throw new IllegalArgumentException(
                    "Ya existe otra categoría con ese nombre.");
        }

        categoriaDAO.actualizar(categoria);
    }

    // Eliminar categoría (no se permite si tiene productos asociados)
    public void eliminar(int id) throws SQLException {

        validarId(id);

        if (categoriaDAO.buscarPorId(id) == null) {
            throw new IllegalArgumentException("La categoría no existe.");
        }

        try {
            categoriaDAO.eliminar(id);
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException(
                    "No se puede eliminar la categoría porque tiene productos asociados.");
        }
    }

    // =========================================
    // VALIDACIONES DEL NEGOCIO
    // =========================================
    private void validarId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor que cero.");
        }
    }

    private void validarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría no puede ser nula.");
        }

        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre de la categoría es obligatorio.");
        }

        if (categoria.getNombre().trim().length() > MAX_NOMBRE) {
            throw new IllegalArgumentException(
                    "El nombre no puede superar " + MAX_NOMBRE + " caracteres.");
        }

        if (categoria.getDescripcion() != null
                && categoria.getDescripcion().trim().length() > MAX_DESCRIPCION) {
            throw new IllegalArgumentException(
                    "La descripción no puede superar " + MAX_DESCRIPCION + " caracteres.");
        }

        // Normalizar: sin espacios sobrantes; descripción vacía = NULL.
        categoria.setNombre(categoria.getNombre().trim());
        if (categoria.getDescripcion() != null) {
            String descripcion = categoria.getDescripcion().trim();
            categoria.setDescripcion(descripcion.isEmpty() ? null : descripcion);
        }
    }

    // El LIKE del DAO devuelve coincidencias parciales: se filtra la exacta.
    private Categoria buscarNombreExacto(String nombre) throws SQLException {
        for (Categoria c : categoriaDAO.buscarPorNombre(nombre)) {
            if (c.getNombre().equalsIgnoreCase(nombre)) {
                return c;
            }
        }
        return null;
    }
}
