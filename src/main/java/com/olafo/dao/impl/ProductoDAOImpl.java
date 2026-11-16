package com.olafo.dao.impl;

import com.olafo.config.ConexionBD;
import com.olafo.dao.ProductoDAO;
import com.olafo.model.Categoria;
import com.olafo.model.Producto;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Implementación JDBC de ProductoDAO para la tabla producto_terminado. */
public class ProductoDAOImpl implements ProductoDAO {
    // Consulta base: recupera cada producto junto con su categoría.
    private static final String SELECT_BASE = """
            SELECT
                p.id_producto,
                p.nombre,
                p.descripcion,
                p.precio_venta,
                p.stock_actual,
                p.activo,
                p.fecha_creacion,
                p.id_categoria_producto,
                c.nombre AS categoria_nombre,
                c.descripcion AS categoria_descripcion
            FROM producto_terminado p
            INNER JOIN categoria_producto c
                ON p.id_categoria_producto = c.id_categoria_producto
            """;

    // =========================================
    // CREATE - REGISTRAR PRODUCTO
    // =========================================
    @Override
    public void guardar(Producto producto) throws SQLException {

        String sql = """
                INSERT INTO producto_terminado (
                    id_categoria_producto, nombre, descripcion,
                    precio_venta, stock_actual, activo, fecha_creacion
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        // fecha_creacion es NOT NULL y sin valor por defecto en la tabla.
        LocalDate fecha = producto.getFechaCreacion() != null
                ? producto.getFechaCreacion()
                : LocalDate.now();

        try (Connection con = ConexionBD.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, producto.getCategoria().getIdCategoria());
            ps.setString(2, producto.getNombre());
            ps.setString(3, producto.getDescripcion());
            ps.setBigDecimal(4, producto.getPrecioVenta());
            ps.setInt(5, producto.getStockActual());
            ps.setBoolean(6, producto.isActivo());
            ps.setDate(7, Date.valueOf(fecha));

            int filas = ps.executeUpdate();

            if (filas != 1) {
                throw new SQLException("No se pudo registrar el producto.");
            }

            // Recuperar el ID generado por MySQL.
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    producto.setIdProducto(rs.getInt(1));
                }
            }
            producto.setFechaCreacion(fecha);
        }
    }

    // =========================================
    // READ - LISTAR TODOS LOS PRODUCTOS
    // =========================================
    @Override
    public List<Producto> listar() throws SQLException {
        return consultarLista(SELECT_BASE + " ORDER BY p.nombre", null);
    }

    // =========================================
    // READ - BUSCAR PRODUCTO POR ID
    // =========================================
    @Override
    public Producto buscarPorId(int id) throws SQLException {

        String sql = SELECT_BASE + " WHERE p.id_producto = ?";

        try (Connection con = ConexionBD.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        }

        return null;
    }

    // =========================================
    // READ - BUSCAR PRODUCTOS POR NOMBRE
    // =========================================
    @Override
    public List<Producto> buscarPorNombre(String nombre) throws SQLException {

        String sql = SELECT_BASE + " WHERE p.nombre LIKE ? ORDER BY p.nombre";
        return consultarLista(sql, "%" + nombre + "%");
    }

    // =========================================
    // UPDATE - ACTUALIZAR PRODUCTO
    // =========================================
    @Override
    public void actualizar(Producto producto) throws SQLException {

        String sql = """
                UPDATE producto_terminado
                SET id_categoria_producto = ?,
                    nombre = ?,
                    descripcion = ?,
                    precio_venta = ?,
                    stock_actual = ?,
                    activo = ?
                WHERE id_producto = ?
                """;

        try (Connection con = ConexionBD.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, producto.getCategoria().getIdCategoria());
            ps.setString(2, producto.getNombre());
            ps.setString(3, producto.getDescripcion());
            ps.setBigDecimal(4, producto.getPrecioVenta());
            ps.setInt(5, producto.getStockActual());
            ps.setBoolean(6, producto.isActivo());
            ps.setInt(7, producto.getIdProducto());

            ps.executeUpdate();
        }
    }

    // =========================================
    // DELETE LÓGICO - DESACTIVAR PRODUCTO
    // =========================================
    @Override
    public void desactivar(int id) throws SQLException {

        String sql = "UPDATE producto_terminado SET activo = 0 WHERE id_producto = ?";

        try (Connection con = ConexionBD.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // =========================================
    // CONSULTA - STOCK BAJO
    // =========================================
    @Override
    public List<Producto> listarStockBajo(int umbral) throws SQLException {

        String sql = SELECT_BASE
                + " WHERE p.activo = 1 AND p.stock_actual <= ?"
                + " ORDER BY p.stock_actual, p.nombre";

        List<Producto> productos = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, umbral);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        }

        return productos;
    }

    // =========================================
    // MÉTODOS AUXILIARES
    // =========================================

    // Ejecuta una consulta con un único parámetro de texto opcional.
    private List<Producto> consultarLista(String sql, String parametro)
            throws SQLException {

        List<Producto> productos = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            if (parametro != null) {
                ps.setString(1, parametro);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        }

        return productos;
    }

    // Convertir una fila SQL (con JOIN) en objeto Java
    private Producto mapearProducto(ResultSet rs) throws SQLException {

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(rs.getInt("id_categoria_producto"));
        categoria.setNombre(rs.getString("categoria_nombre"));
        categoria.setDescripcion(rs.getString("categoria_descripcion"));

        Producto producto = new Producto();
        producto.setIdProducto(rs.getInt("id_producto"));
        producto.setCategoria(categoria);
        producto.setNombre(rs.getString("nombre"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setPrecioVenta(rs.getBigDecimal("precio_venta"));
        producto.setStockActual(rs.getInt("stock_actual"));
        producto.setActivo(rs.getBoolean("activo"));
        producto.setFechaCreacion(rs.getDate("fecha_creacion").toLocalDate());
        return producto;
    }
}
