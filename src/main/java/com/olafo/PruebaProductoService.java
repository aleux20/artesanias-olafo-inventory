package com.olafo;

import com.olafo.config.ConexionBD;
import com.olafo.model.Categoria;
import com.olafo.model.Producto;
import com.olafo.service.CategoriaService;
import com.olafo.service.ProductoService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Prueba de ProductoService: validaciones (sin MySQL) y flujo completo con
 * una categoría y un producto temporales que se eliminan al final.
 */
public class PruebaProductoService {
    private static int aprobadas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {
        ProductoService productoService = new ProductoService();
        CategoriaService categoriaService = new CategoriaService();

        Categoria categoriaFalsa = new Categoria(1, "Cualquiera", null);

        System.out.println("=== PRUEBAS DE VALIDACIÓN (PRODUCTOS) ===");

        probarValidacion("Nombre vacío", () -> productoService.registrar(
                nuevoProducto(categoriaFalsa, "", "1000", 1)));

        probarValidacion("Nombre mayor a 100", () -> productoService.registrar(
                nuevoProducto(categoriaFalsa, "A".repeat(101), "1000", 1)));

        probarValidacion("Precio nulo", () -> productoService.registrar(
                nuevoProducto(categoriaFalsa, "Producto", null, 1)));

        probarValidacion("Precio negativo", () -> productoService.registrar(
                nuevoProducto(categoriaFalsa, "Producto", "-5", 1)));

        probarValidacion("Precio superior al máximo", () -> productoService.registrar(
                nuevoProducto(categoriaFalsa, "Producto", "100000000", 1)));

        probarValidacion("Stock negativo", () -> productoService.registrar(
                nuevoProducto(categoriaFalsa, "Producto", "1000", -1)));

        probarValidacion("Sin categoría", () -> productoService.registrar(
                nuevoProducto(null, "Producto", "1000", 1)));

        probarValidacion("ID inválido", () -> productoService.buscarPorId(0));

        probarValidacion("Búsqueda vacía", () -> productoService.buscarPorNombre(" "));

        probarValidacion("Umbral negativo", () -> productoService.listarStockBajo(-1));

        System.out.println("\n=== FLUJO COMPLETO (requiere MySQL) ===");

        Categoria categoria = new Categoria(0,
                "CatProducto-" + System.currentTimeMillis(), "Categoría temporal");
        Producto producto = null;
        boolean categoriaCreada = false;

        try {
            categoriaService.registrar(categoria);
            categoriaCreada = true;

            // Registrar
            producto = nuevoProducto(categoria, "  Producto de prueba  ", "45000.50", 3);
            productoService.registrar(producto);
            verificar("Producto registrado con id " + producto.getIdProducto(),
                    producto.getIdProducto() > 0);

            // Consultar
            Producto guardado = productoService.buscarPorId(producto.getIdProducto());
            verificar("Se consulta con su categoría",
                    guardado != null
                            && guardado.getCategoria().getIdCategoria() == categoria.getIdCategoria());
            verificar("Está activo y tiene fecha de creación",
                    guardado.isActivo() && guardado.getFechaCreacion() != null);

            List<Producto> porNombre = productoService.buscarPorNombre("Producto de prueba");
            verificar("Se encuentra por nombre", !porNombre.isEmpty());

            // Categoría inexistente
            final Producto huerfano = nuevoProducto(new Categoria(999999, "X", null), "Huérfano", "1000", 1);
            probarValidacion("Categoría inexistente", () -> productoService.registrar(huerfano));

            // Actualizar
            guardado.setPrecioVenta(new BigDecimal("52000"));
            guardado.setStockActual(10);
            productoService.actualizar(guardado);
            Producto actualizado = productoService.buscarPorId(guardado.getIdProducto());
            verificar("Actualización aplicada",
                    actualizado.getPrecioVenta().compareTo(new BigDecimal("52000")) == 0
                            && actualizado.getStockActual() == 10);

            // Stock bajo
            guardado.setStockActual(2);
            productoService.actualizar(guardado);
            final int idProducto = guardado.getIdProducto();
            verificar("Aparece en la consulta de stock bajo (<= 5)",
                    productoService.listarStockBajo(5).stream()
                            .anyMatch(p -> p.getIdProducto() == idProducto));

            // Desactivar (eliminación lógica)
            productoService.desactivar(idProducto);
            verificar("Producto desactivado",
                    !productoService.buscarPorId(idProducto).isActivo());
            probarValidacion("Desactivar un producto ya desactivado", () -> productoService.desactivar(idProducto));
            verificar("Ya no aparece en stock bajo",
                    productoService.listarStockBajo(5).stream()
                            .noneMatch(p -> p.getIdProducto() == idProducto));

        } catch (SQLException e) {
            fallidas++;
            System.out.println("[ERROR JDBC] " + e.getMessage());
        } catch (IllegalArgumentException e) {
            fallidas++;
            System.out.println("[FALLO] " + e.getMessage());
        } finally {
            limpiar(categoriaService, categoria, categoriaCreada, producto);
        }

        System.out.println("\nResultado: " + aprobadas + " aprobadas, "
                + fallidas + " fallidas.");
    }

    // ------------------------- Utilidades -------------------------
    private static Producto nuevoProducto(Categoria categoria, String nombre,
            String precio, int stock) {
        Producto p = new Producto();
        p.setCategoria(categoria);
        p.setNombre(nombre);
        p.setDescripcion("Producto creado por la prueba");
        p.setPrecioVenta(precio == null ? null : new BigDecimal(precio));
        p.setStockActual(stock);
        p.setActivo(true);
        return p;
    }

    @FunctionalInterface
    interface Operacion {
        void ejecutar() throws SQLException;
    }

    // La operación DEBE ser rechazada con IllegalArgumentException.
    private static void probarValidacion(String prueba, Operacion operacion) {
        try {
            operacion.ejecutar();
            fallidas++;
            System.out.println("[FALLO] " + prueba + ": no rechazó el dato.");

        } catch (IllegalArgumentException e) {
            aprobadas++;
            System.out.println("[APROBADO] " + prueba + ": " + e.getMessage());

        } catch (SQLException e) {
            fallidas++;
            System.out.println("[ERROR JDBC] " + prueba + ": " + e.getMessage());
        }
    }

    private static void verificar(String prueba, boolean condicion) {
        if (condicion) {
            aprobadas++;
            System.out.println("[APROBADO] " + prueba);
        } else {
            fallidas++;
            System.out.println("[FALLO] " + prueba);
        }
    }

    // El DAO solo desactiva productos (eliminación lógica); para no dejar
    // basura en la base, la prueba borra su producto con SQL directo
    // y luego elimina la categoría temporal con el servicio.
    private static void limpiar(CategoriaService categoriaService, Categoria categoria,
            boolean categoriaCreada, Producto producto) {

        try {
            if (producto != null && producto.getIdProducto() > 0) {
                try (Connection con = ConexionBD.obtenerConexion();
                        PreparedStatement ps = con.prepareStatement(
                                "DELETE FROM producto_terminado WHERE id_producto = ?")) {
                    ps.setInt(1, producto.getIdProducto());
                    ps.executeUpdate();
                }
            }
            if (categoriaCreada) {
                categoriaService.eliminar(categoria.getIdCategoria());
            }
        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("No se pudieron limpiar los datos de prueba: " + e.getMessage());
        }
    }
}
