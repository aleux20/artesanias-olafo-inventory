package com.olafo.dao;

import com.olafo.model.Producto;

import java.sql.SQLException;
import java.util.List;

/** Contrato de acceso a datos para la tabla producto_terminado. */
public interface ProductoDAO {
    // CREATE: Registrar un producto (asigna el id generado al objeto)
    void guardar(Producto producto) throws SQLException;

    // READ: Listar todos los productos
    List<Producto> listar() throws SQLException;

    // READ: Buscar producto por ID (null si no existe)
    Producto buscarPorId(int id) throws SQLException;

    // READ: Buscar productos cuyo nombre contenga el texto
    List<Producto> buscarPorNombre(String nombre) throws SQLException;

    // UPDATE: Actualizar producto
    void actualizar(Producto producto) throws SQLException;

    // DELETE lógico: Desactivar producto (activo = 0)
    void desactivar(int id) throws SQLException;

    // Consulta: Productos activos con stock menor o igual al umbral
    List<Producto> listarStockBajo(int umbral) throws SQLException;
}
