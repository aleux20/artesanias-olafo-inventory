package com.olafo.dao.impl;

import com.olafo.config.ConexionBD;
import com.olafo.dao.CategoriaDAO;
import com.olafo.model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Implementación JDBC de CategoriaDAO para la tabla categoria_producto. */
public class CategoriaDAOImpl implements CategoriaDAO {
    private static final String SELECT_BASE = """
            SELECT id_categoria_producto, nombre, descripcion
            FROM categoria_producto
            """;

    @Override
    public void guardar(Categoria categoria) throws SQLException {

        String sql = """
                INSERT INTO categoria_producto (nombre, descripcion)
                VALUES (?, ?)
                """;

        try (Connection con = ConexionBD.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());

            ps.executeUpdate();

            // Recuperar el ID generado por MySQL.
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    categoria.setIdCategoria(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public List<Categoria> listar() throws SQLException {

        String sql = SELECT_BASE + " ORDER BY nombre";
        List<Categoria> categorias = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categorias.add(mapearCategoria(rs));
            }
        }

        return categorias;
    }

    @Override
    public Categoria buscarPorId(int id) throws SQLException {

        String sql = SELECT_BASE + " WHERE id_categoria_producto = ?";

        try (Connection con = ConexionBD.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCategoria(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Categoria> buscarPorNombre(String nombre) throws SQLException {

        String sql = SELECT_BASE + " WHERE nombre LIKE ? ORDER BY nombre";
        List<Categoria> categorias = new ArrayList<>();

        try (Connection con = ConexionBD.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categorias.add(mapearCategoria(rs));
                }
            }
        }

        return categorias;
    }

    @Override
    public void actualizar(Categoria categoria) throws SQLException {

        String sql = """
                UPDATE categoria_producto
                SET nombre = ?, descripcion = ?
                WHERE id_categoria_producto = ?
                """;

        try (Connection con = ConexionBD.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setInt(3, categoria.getIdCategoria());

            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {

        String sql = "DELETE FROM categoria_producto WHERE id_categoria_producto = ?";

        try (Connection con = ConexionBD.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Convertir una fila SQL en objeto Java
    private Categoria mapearCategoria(ResultSet rs) throws SQLException {

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(rs.getInt("id_categoria_producto"));
        categoria.setNombre(rs.getString("nombre"));
        categoria.setDescripcion(rs.getString("descripcion"));
        return categoria;
    }
}
