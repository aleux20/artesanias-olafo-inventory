package com.olafo.app;

import com.olafo.model.Categoria;
import com.olafo.model.Producto;
import com.olafo.service.CategoriaService;
import com.olafo.service.ProductoService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/** Menú de consola del sistema: usa únicamente la capa de servicio. */
public class MenuPrincipal {
    private final Scanner scanner = new Scanner(System.in);

    private final CategoriaService categoriaService = new CategoriaService();
    private final ProductoService productoService = new ProductoService();

    // =========================================
    // INICIO DE LA APLICACIÓN
    // =========================================

    public static void main(String[] args) {
        new MenuPrincipal().iniciar();
    }

    public void iniciar() {

        int opcion;

        do {
            System.out.println("\n========================================");
            System.out.println("        ARTESANÍAS OLAFO");
            System.out.println("   SISTEMA DE GESTIÓN DE INVENTARIO");
            System.out.println("========================================");
            System.out.println("1. Gestionar categorías");
            System.out.println("2. Gestionar productos terminados");
            System.out.println("0. Salir");

            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> menuCategorias();
                case 2 -> menuProductos();
                case 0 -> System.out.println("\nGracias por utilizar el sistema.");
                default -> System.out.println("Opción no válida.");
            }

        } while (opcion != 0);

        scanner.close();
    }

    // =========================================
    // MENÚ DE CATEGORÍAS
    // =========================================

    private void menuCategorias() {
        int opcion;

        do {
            System.out.println("\n====== GESTIÓN DE CATEGORÍAS ======");
            System.out.println("1. Registrar categoría");
            System.out.println("2. Listar categorías");
            System.out.println("3. Buscar categoría por ID");
            System.out.println("4. Buscar categoría por nombre");
            System.out.println("5. Actualizar categoría");
            System.out.println("6. Eliminar categoría");
            System.out.println("0. Volver al menú principal");

            opcion = leerEntero("Seleccione una opción: ");

            try {
                switch (opcion) {
                    case 1 -> registrarCategoria();
                    case 2 -> listarCategorias();
                    case 3 -> buscarCategoriaPorId();
                    case 4 -> buscarCategoriaPorNombre();
                    case 5 -> actualizarCategoria();
                    case 6 -> eliminarCategoria();
                    case 0 -> System.out.println("Regresando al menú principal...");
                    default -> System.out.println("Opción no válida.");
                }
            } catch (SQLException | IllegalArgumentException e) {
                mostrarError(e);
            }

            if (opcion != 0) {
                pausar();
            }

        } while (opcion != 0);
    }

    // =========================================
    // CRUD DE CATEGORÍAS
    // =========================================

    private void registrarCategoria() throws SQLException {
        System.out.println("\n===== REGISTRAR CATEGORÍA =====");

        Categoria categoria = new Categoria();
        categoria.setNombre(leerTexto("Nombre: "));
        categoria.setDescripcion(leerTexto("Descripción: "));

        categoriaService.registrar(categoria);

        System.out.println("Categoría registrada correctamente (id "
                + categoria.getIdCategoria() + ").");
    }

    private void listarCategorias() throws SQLException {
        System.out.println("\n===== LISTADO DE CATEGORÍAS =====");

        List<Categoria> categorias = categoriaService.listar();

        if (categorias.isEmpty()) {
            System.out.println("No existen categorías registradas.");
            return;
        }

        System.out.printf("%-5s %-25s %-45s%n", "ID", "NOMBRE", "DESCRIPCIÓN");
        System.out.println("-".repeat(78));

        for (Categoria c : categorias) {
            System.out.printf("%-5d %-25s %-45s%n",
                    c.getIdCategoria(),
                    limitar(c.getNombre(), 25),
                    limitar(c.getDescripcion(), 45));
        }

        System.out.println("\nTotal de categorías: " + categorias.size());
    }

    private void buscarCategoriaPorId() throws SQLException {
        System.out.println("\n===== BUSCAR CATEGORÍA POR ID =====");

        Categoria categoria = categoriaService.buscarPorId(
                leerEntero("Ingrese ID de categoría: "));

        if (categoria == null) {
            System.out.println("Categoría no encontrada.");
            return;
        }

        mostrarDetalleCategoria(categoria);
    }

    private void buscarCategoriaPorNombre() throws SQLException {
        System.out.println("\n===== BUSCAR CATEGORÍA POR NOMBRE =====");

        List<Categoria> categorias = categoriaService.buscarPorNombre(leerTexto("Ingrese nombre: "));

        if (categorias.isEmpty()) {
            System.out.println("No se encontraron coincidencias.");
            return;
        }

        for (Categoria c : categorias) {
            mostrarDetalleCategoria(c);
        }
    }

    private void actualizarCategoria() throws SQLException {
        System.out.println("\n===== ACTUALIZAR CATEGORÍA =====");

        Categoria categoria = categoriaService.buscarPorId(
                leerEntero("Ingrese ID de categoría: "));

        if (categoria == null) {
            System.out.println("La categoría no existe.");
            return;
        }

        mostrarDetalleCategoria(categoria);

        categoria.setNombre(leerTexto("Nuevo nombre: "));
        categoria.setDescripcion(leerTexto("Nueva descripción: "));

        categoriaService.actualizar(categoria);

        System.out.println("Categoría actualizada correctamente.");
    }

    private void eliminarCategoria() throws SQLException {
        System.out.println("\n===== ELIMINAR CATEGORÍA =====");

        int id = leerEntero("Ingrese ID de categoría: ");

        Categoria categoria = categoriaService.buscarPorId(id);

        if (categoria == null) {
            System.out.println("La categoría no existe.");
            return;
        }

        mostrarDetalleCategoria(categoria);

        if (confirmar("¿Desea eliminar esta categoría?")) {
            categoriaService.eliminar(id);
            System.out.println("Categoría eliminada correctamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    // =========================================
    // MENÚ DE PRODUCTOS
    // =========================================

    private void menuProductos() {
        int opcion;

        do {
            System.out.println("\n====== GESTIÓN DE PRODUCTOS TERMINADOS ======");
            System.out.println("1. Registrar producto");
            System.out.println("2. Listar productos");
            System.out.println("3. Buscar producto por ID");
            System.out.println("4. Buscar producto por nombre");
            System.out.println("5. Actualizar producto");
            System.out.println("6. Desactivar producto");
            System.out.println("7. Consultar stock bajo");
            System.out.println("0. Volver al menú principal");

            opcion = leerEntero("Seleccione una opción: ");

            try {
                switch (opcion) {
                    case 1 -> registrarProducto();
                    case 2 -> listarProductos();
                    case 3 -> buscarProductoPorId();
                    case 4 -> buscarProductoPorNombre();
                    case 5 -> actualizarProducto();
                    case 6 -> desactivarProducto();
                    case 7 -> consultarStockBajo();
                    case 0 -> System.out.println("Regresando al menú principal...");
                    default -> System.out.println("Opción no válida.");
                }
            } catch (SQLException | IllegalArgumentException e) {
                mostrarError(e);
            }

            if (opcion != 0) {
                pausar();
            }

        } while (opcion != 0);
    }

    // =========================================
    // CRUD DE PRODUCTOS
    // =========================================

    private void registrarProducto() throws SQLException {
        System.out.println("\n===== REGISTRAR PRODUCTO =====");

        Producto producto = new Producto();
        producto.setNombre(leerTexto("Nombre: "));
        producto.setDescripcion(leerTexto("Descripción: "));
        producto.setPrecioVenta(leerDecimal("Precio de venta: "));
        producto.setStockActual(leerEntero("Stock inicial: "));
        producto.setCategoria(seleccionarCategoria());
        producto.setActivo(true);

        productoService.registrar(producto);

        System.out.println("\nProducto registrado correctamente (id "
                + producto.getIdProducto() + ").");
    }

    private void listarProductos() throws SQLException {
        System.out.println("\n===== LISTADO DE PRODUCTOS =====");
        mostrarTablaProductos(productoService.listar());
    }

    private void buscarProductoPorId() throws SQLException {
        System.out.println("\n===== BUSCAR PRODUCTO POR ID =====");

        Producto producto = productoService.buscarPorId(
                leerEntero("Ingrese ID del producto: "));

        if (producto == null) {
            System.out.println("Producto no encontrado.");
            return;
        }

        mostrarDetalleProducto(producto);
    }

    private void buscarProductoPorNombre() throws SQLException {
        System.out.println("\n===== BUSCAR PRODUCTO POR NOMBRE =====");
        mostrarTablaProductos(
                productoService.buscarPorNombre(leerTexto("Ingrese nombre: ")));
    }

    private void actualizarProducto() throws SQLException {
        System.out.println("\n===== ACTUALIZAR PRODUCTO =====");

        Producto producto = productoService.buscarPorId(
                leerEntero("Ingrese ID del producto: "));

        if (producto == null) {
            System.out.println("El producto no existe.");
            return;
        }

        mostrarDetalleProducto(producto);

        if (!confirmar("¿Desea modificar este producto?")) {
            System.out.println("Operación cancelada.");
            return;
        }

        System.out.println("\nIngrese los nuevos datos:");

        producto.setNombre(leerTexto("Nombre: "));
        producto.setDescripcion(leerTexto("Descripción: "));
        producto.setPrecioVenta(leerDecimal("Precio de venta: "));
        producto.setStockActual(leerEntero("Stock actual: "));
        producto.setCategoria(seleccionarCategoria());

        productoService.actualizar(producto);

        System.out.println("\nProducto actualizado correctamente.");
    }

    private void desactivarProducto() throws SQLException {
        System.out.println("\n===== DESACTIVAR PRODUCTO =====");

        int id = leerEntero("Ingrese ID del producto: ");

        Producto producto = productoService.buscarPorId(id);

        if (producto == null) {
            System.out.println("El producto no existe.");
            return;
        }

        mostrarDetalleProducto(producto);

        if (confirmar("¿Desea desactivar este producto?")) {
            productoService.desactivar(id);
            System.out.println("Producto desactivado correctamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void consultarStockBajo() throws SQLException {
        System.out.println("\n===== PRODUCTOS CON STOCK BAJO =====");

        int umbral = leerEntero("Mostrar productos con stock menor o igual a: ");

        List<Producto> productos = productoService.listarStockBajo(umbral);

        if (productos.isEmpty()) {
            System.out.println("No existen productos activos con stock bajo.");
            return;
        }

        mostrarTablaProductos(productos);
    }

    // =========================================
    // SELECCIÓN DE CATEGORÍA
    // =========================================

    private Categoria seleccionarCategoria() throws SQLException {
        List<Categoria> categorias = categoriaService.listar();

        if (categorias.isEmpty()) {
            throw new IllegalArgumentException(
                    "No existen categorías. Registre una primero.");
        }

        System.out.println("\n===== CATEGORÍAS DISPONIBLES =====");

        for (Categoria c : categorias) {
            System.out.printf("%d - %s%n", c.getIdCategoria(), c.getNombre());
        }

        Categoria categoria = categoriaService.buscarPorId(
                leerEntero("Seleccione ID de categoría: "));

        if (categoria == null) {
            throw new IllegalArgumentException(
                    "La categoría seleccionada no existe.");
        }

        return categoria;
    }

    // =========================================
    // PRESENTACIÓN
    // =========================================

    private void mostrarDetalleCategoria(Categoria c) {
        System.out.println("\n----------------------------------");
        System.out.println("ID: " + c.getIdCategoria());
        System.out.println("Nombre: " + c.getNombre());
        System.out.println("Descripción: "
                + (c.getDescripcion() == null ? "(sin descripción)" : c.getDescripcion()));
        System.out.println("----------------------------------");
    }

    private void mostrarTablaProductos(List<Producto> productos) {
        if (productos.isEmpty()) {
            System.out.println("No se encontraron productos.");
            return;
        }

        System.out.printf("%-5s %-28s %-18s %-14s %-7s %-10s%n",
                "ID", "PRODUCTO", "CATEGORÍA", "PRECIO", "STOCK", "ESTADO");
        System.out.println("-".repeat(86));

        for (Producto p : productos) {
            System.out.printf("%-5d %-28s %-18s %-14s %-7d %-10s%n",
                    p.getIdProducto(),
                    limitar(p.getNombre(), 28),
                    limitar(p.getCategoria().getNombre(), 18),
                    p.getPrecioVenta().toPlainString(),
                    p.getStockActual(),
                    p.isActivo() ? "Activo" : "Inactivo");
        }

        System.out.println("\nTotal de productos: " + productos.size());
    }

    private void mostrarDetalleProducto(Producto p) {
        System.out.println("\n========== DETALLE DEL PRODUCTO ==========");
        System.out.println("ID: " + p.getIdProducto());
        System.out.println("Nombre: " + p.getNombre());
        System.out.println("Descripción: "
                + (p.getDescripcion() == null ? "(sin descripción)" : p.getDescripcion()));
        System.out.println("Categoría: " + p.getCategoria().getNombre());
        System.out.println("Precio de venta: " + p.getPrecioVenta());
        System.out.println("Stock actual: " + p.getStockActual());
        System.out.println("Estado: " + (p.isActivo() ? "Activo" : "Inactivo"));
        System.out.println("Fecha de creación: " + p.getFechaCreacion());
        System.out.println("==========================================");
    }

    // =========================================
    // MÉTODOS AUXILIARES
    // =========================================

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();

            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero válido.");
            }
        }
    }

    private BigDecimal leerDecimal(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();

            try {
                return new BigDecimal(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un valor decimal válido. Ejemplo: 45000.00");
            }
        }
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private boolean confirmar(String mensaje) {
        while (true) {
            String respuesta = leerTexto(mensaje + " (S/N): ").toUpperCase();

            if (respuesta.equals("S")) {
                return true;
            }
            if (respuesta.equals("N")) {
                return false;
            }
            System.out.println("Responda S para Sí o N para No.");
        }
    }

    private void pausar() {
        System.out.println("\nPresione ENTER para continuar...");
        scanner.nextLine();
    }

    private void mostrarError(Exception e) {
        System.out.println("\nERROR: " + e.getMessage());
    }

    private String limitar(String texto, int longitud) {
        if (texto == null) {
            return "";
        }
        if (texto.length() <= longitud) {
            return texto;
        }
        return texto.substring(0, longitud - 3) + "...";
    }
}
