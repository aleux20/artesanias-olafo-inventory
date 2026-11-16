package com.olafo.service;

import com.olafo.dao.CategoriaDAO;
import com.olafo.dao.ProductoDAO;
import com.olafo.dao.impl.CategoriaDAOImpl;
import com.olafo.dao.impl.ProductoDAOImpl;
import com.olafo.model.Categoria;
import com.olafo.model.Producto;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Capa de servicio del módulo de productos terminados.
 * Valida los datos y comprueba que la categoría exista antes de usar el DAO.
 */
public class ProductoService {
    // =========================================
    // DEPENDENCIAS Y LÍMITES
    // =========================================
    private final ProductoDAO productoDAO;
    private final CategoriaDAO categoriaDAO;

    private static final int MAX_NOMBRE = 100;

    // Máximo de DECIMAL(10,2)
    private static final BigDecimal PRECIO_MAXIMO = new BigDecimal("99999999.99");

    // Constructor por defecto
    public ProductoService() {
        this(new ProductoDAOImpl(), new CategoriaDAOImpl());
    }

    // Constructor con inyección de dependencias
    public ProductoService(ProductoDAO productoDAO, CategoriaDAO categoriaDAO) {
        this.productoDAO = Objects.requireNonNull(
                productoDAO, "ProductoDAO no puede ser nulo.");
        this.categoriaDAO = Objects.requireNonNull(
                categoriaDAO, "CategoriaDAO no puede ser nulo.");
    }

    // =========================================
    // CREATE - REGISTRAR PRODUCTO
    // =========================================
    public void registrar(Producto producto) throws SQLException {

        validarProducto(producto);

        if (producto.getFechaCreacion() == null) {
            producto.setFechaCreacion(LocalDate.now());
        }

        productoDAO.guardar(producto);
    }

    // =========================================
    // READ
    // =========================================
    public List<Producto> listar() throws SQLException {
        return productoDAO.listar();
    }

    public Producto buscarPorId(int id) throws SQLException {
        validarId(id);
        return productoDAO.buscarPorId(id);
    }

    public List<Producto> buscarPorNombre(String nombre) throws SQLException {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe ingresar un nombre para buscar.");
        }

        return productoDAO.buscarPorNombre(nombre.trim());
    }

    public List<Producto> listarStockBajo(int umbral) throws SQLException {

        if (umbral < 0) {
            throw new IllegalArgumentException(
                    "El umbral de stock no puede ser negativo.");
        }

        return productoDAO.listarStockBajo(umbral);
    }

    // =========================================
    // UPDATE - ACTUALIZAR PRODUCTO
    // =========================================
    public void actualizar(Producto producto) throws SQLException {

        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        validarId(producto.getIdProducto());
        validarProducto(producto);

        if (productoDAO.buscarPorId(producto.getIdProducto()) == null) {
            throw new IllegalArgumentException("El producto no existe.");
        }

        productoDAO.actualizar(producto);
    }

    // =========================================
    // DELETE LÓGICO - DESACTIVAR PRODUCTO
    // =========================================
    public void desactivar(int id) throws SQLException {

        validarId(id);

        Producto producto = productoDAO.buscarPorId(id);

        if (producto == null) {
            throw new IllegalArgumentException("El producto no existe.");
        }

        if (!producto.isActivo()) {
            throw new IllegalArgumentException("El producto ya está desactivado.");
        }

        productoDAO.desactivar(id);
    }

    // =========================================
    // VALIDACIONES DEL NEGOCIO
    // =========================================
    private void validarId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor que cero.");
        }
    }

    // Las validaciones que no necesitan la base de datos van primero;
    // la comprobación de la categoría (consulta SQL) va al final.
    private void validarProducto(Producto producto) throws SQLException {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        if (producto.getNombre() == null || producto.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del producto es obligatorio.");
        }

        if (producto.getNombre().trim().length() > MAX_NOMBRE) {
            throw new IllegalArgumentException(
                    "El nombre no puede superar " + MAX_NOMBRE + " caracteres.");
        }

        if (producto.getPrecioVenta() == null) {
            throw new IllegalArgumentException("El precio de venta es obligatorio.");
        }

        if (producto.getPrecioVenta().signum() < 0) {
            throw new IllegalArgumentException(
                    "El precio de venta no puede ser negativo.");
        }

        if (producto.getPrecioVenta().compareTo(PRECIO_MAXIMO) > 0) {
            throw new IllegalArgumentException(
                    "El precio de venta supera el máximo permitido.");
        }

        if (producto.getStockActual() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }

        if (producto.getCategoria() == null) {
            throw new IllegalArgumentException(
                    "El producto debe pertenecer a una categoría.");
        }

        // Normalizar texto
        producto.setNombre(producto.getNombre().trim());
        if (producto.getDescripcion() != null) {
            String descripcion = producto.getDescripcion().trim();
            producto.setDescripcion(descripcion.isEmpty() ? null : descripcion);
        }

        // La categoría debe existir (consulta a la base de datos).
        Categoria categoria = categoriaDAO.buscarPorId(
                producto.getCategoria().getIdCategoria());

        if (categoria == null) {
            throw new IllegalArgumentException(
                    "La categoría seleccionada no existe.");
        }

        producto.setCategoria(categoria);
    }
}
