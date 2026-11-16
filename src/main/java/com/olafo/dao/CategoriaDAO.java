package com.olafo.dao;

import com.olafo.model.Categoria;

import java.sql.SQLException;
import java.util.List;

/** Contrato de acceso a datos para la tabla categoria_producto. */
public interface CategoriaDAO {
    // CREATE: Registrar categoría (asigna el id generado al objeto)
    void guardar(Categoria categoria) throws SQLException;

    // READ: Consultar todas las categorías
    List<Categoria> listar() throws SQLException;

    // READ: Buscar categoría por ID (null si no existe)
    Categoria buscarPorId(int id) throws SQLException;

    // READ: Buscar categorías cuyo nombre contenga el texto
    List<Categoria> buscarPorNombre(String nombre) throws SQLException;

    // UPDATE: Actualizar categoría
    void actualizar(Categoria categoria) throws SQLException;

    // DELETE: Eliminar categoría (la tabla no tiene columna de estado).
    // Falla si tiene productos asociados (llave foránea ON DELETE RESTRICT).
    void eliminar(int id) throws SQLException;
}
