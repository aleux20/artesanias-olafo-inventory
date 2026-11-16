package com.olafo;

import com.olafo.dao.CategoriaDAO;
import com.olafo.dao.impl.CategoriaDAOImpl;
import com.olafo.model.Categoria;

import java.sql.SQLException;
import java.util.List;

/**
 * Prueba directa del CRUD de CategoriaDAO (sin capa de servicio).
 * Usa una categoría temporal y la elimina al final, por lo que la base
 * queda como estaba y la prueba se puede repetir.
 */
public class PruebaCategoriaDAO {
    public static void main(String[] args) {
        CategoriaDAO dao = new CategoriaDAOImpl();

        // Nombre único para no chocar con el UNIQUE de la columna nombre.
        String nombrePrueba = "Prueba-" + System.currentTimeMillis();

        try {
            // 1. REGISTRAR
            Categoria nueva = new Categoria(
                    0, nombrePrueba, "Categoría temporal para pruebas");

            dao.guardar(nueva);

            System.out.println("Categoría registrada con id "
                    + nueva.getIdCategoria());

            // 2. LISTAR
            System.out.println("\nLISTADO DE CATEGORÍAS");

            List<Categoria> categorias = dao.listar();

            for (Categoria c : categorias) {
                System.out.println(c);
            }

            // 3. BUSCAR POR ID Y POR NOMBRE
            System.out.println("\nBUSCAR CATEGORÍA");

            System.out.println("Por id    : "
                    + dao.buscarPorId(nueva.getIdCategoria()));

            List<Categoria> encontradas = dao.buscarPorNombre(nombrePrueba);

            for (Categoria c : encontradas) {
                System.out.println("Por nombre: " + c);
            }

            // 4. ACTUALIZAR
            Categoria categoria = dao.buscarPorId(nueva.getIdCategoria());
            categoria.setDescripcion("Categoría actualizada desde Java");

            dao.actualizar(categoria);

            System.out.println("\nCategoría actualizada: "
                    + dao.buscarPorId(categoria.getIdCategoria()));

            // 5. ELIMINAR
            dao.eliminar(categoria.getIdCategoria());

            System.out.println("\nCategoría eliminada. Al buscarla de nuevo: "
                    + (dao.buscarPorId(categoria.getIdCategoria()) == null
                            ? "ya no existe (correcto)"
                            : "¡sigue existiendo!"));

        } catch (SQLException e) {
            System.err.println("Error JDBC: " + e.getMessage());
        }
    }
}
