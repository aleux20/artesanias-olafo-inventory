package com.olafo;

import com.olafo.model.Categoria;
import com.olafo.service.CategoriaService;

import java.sql.SQLException;

/**
 * Prueba de CategoriaService: primero las validaciones (no necesitan MySQL)
 * y luego el flujo completo con una categoría temporal que se elimina al final.
 */
public class PruebaCategoriaService {
    private static int aprobadas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {
        CategoriaService service = new CategoriaService();

        System.out.println("=== PRUEBAS DE VALIDACIÓN (CATEGORÍAS) ===");

        probarValidacion("Nombre vacío", () -> service.registrar(new Categoria(0, "", "Categoría de prueba")));

        probarValidacion("Nombre nulo", () -> service.registrar(new Categoria(0, null, "Categoría de prueba")));

        probarValidacion("Nombre mayor a 50",
                () -> service.registrar(new Categoria(0, "A".repeat(51), "Categoría de prueba")));

        probarValidacion("Descripción mayor a 255",
                () -> service.registrar(new Categoria(0, "Valida", "D".repeat(256))));

        probarValidacion("ID inválido", () -> service.buscarPorId(-1));

        probarValidacion("Búsqueda vacía", () -> service.buscarPorNombre(" "));

        System.out.println("\n=== FLUJO COMPLETO (requiere MySQL) ===");

        String nombre = "ServicioPrueba-" + System.currentTimeMillis();
        Categoria categoria = new Categoria(0, "  " + nombre + "  ", "Creada por la prueba");
        boolean registrada = false;

        try {
            // Registrar
            service.registrar(categoria);
            registrada = true;
            verificar("Registrada con id " + categoria.getIdCategoria(),
                    categoria.getIdCategoria() > 0);
            verificar("El nombre se guardó sin espacios sobrantes",
                    nombre.equals(service.buscarPorId(categoria.getIdCategoria()).getNombre()));

            // Duplicado
            probarValidacion("Nombre duplicado", () -> service.registrar(new Categoria(0, nombre, "otra")));

            // Actualizar
            categoria.setNombre(nombre + "-editada");
            categoria.setDescripcion("Descripción nueva");
            service.actualizar(categoria);
            verificar("Actualización aplicada",
                    (nombre + "-editada").equals(
                            service.buscarPorId(categoria.getIdCategoria()).getNombre()));

            probarValidacion("Actualizar categoría inexistente",
                    () -> service.actualizar(new Categoria(999999, "Nada", "Nada")));

            // Eliminar
            service.eliminar(categoria.getIdCategoria());
            registrada = false;
            verificar("Eliminada: ya no se encuentra",
                    service.buscarPorId(categoria.getIdCategoria()) == null);

        } catch (SQLException e) {
            fallidas++;
            System.out.println("[ERROR JDBC] " + e.getMessage());
        } catch (IllegalArgumentException e) {
            fallidas++;
            System.out.println("[FALLO] " + e.getMessage());
        } finally {
            if (registrada) {
                try {
                    service.eliminar(categoria.getIdCategoria());
                } catch (SQLException | IllegalArgumentException e) {
                    System.out.println("No se pudo limpiar la categoría de prueba: "
                            + e.getMessage());
                }
            }
        }

        System.out.println("\nResultado: " + aprobadas + " aprobadas, "
                + fallidas + " fallidas.");
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
}
