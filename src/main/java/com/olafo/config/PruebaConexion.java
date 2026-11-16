package com.olafo.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Prueba rápida de la conexión JDBC. Ejecutar antes que el menú. */
public class PruebaConexion {
    public static void main(String[] args) {
        System.out.println("Iniciando prueba de conexión...");

        try (Connection conexion = ConexionBD.obtenerConexion()) {
            if (conexion.isValid(5)) {
                DatabaseMetaData info = conexion.getMetaData();
                System.out.println("CONEXIÓN EXITOSA A MYSQL");
                System.out.println("Base de datos: " + conexion.getCatalog());
                System.out.println("Servidor     : " + info.getDatabaseProductName()
                        + " " + info.getDatabaseProductVersion());
                System.out.println("Driver JDBC  : " + info.getDriverName()
                        + " " + info.getDriverVersion());

                mostrarTotal(conexion, "categoria_producto");
                mostrarTotal(conexion, "producto_terminado");
            } else {
                System.out.println("La conexión no es válida.");
            }

        } catch (SQLException e) {
            System.err.println("ERROR DE CONEXIÓN: " + e.getMessage());
            System.err.println("Revise: MySQL encendido, variables OLAFO_DB_USER y "
                    + "OLAFO_DB_PASSWORD, y que exista la base artesanias_olafo.");
        }
    }

    private static void mostrarTotal(Connection conexion, String tabla) {
        // El nombre de la tabla es una constante interna, no un dato del usuario.
        String sql = "SELECT COUNT(*) FROM " + tabla;
        try (PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                System.out.println("Tabla " + tabla + ": " + rs.getInt(1) + " registro(s)");
            }
        } catch (SQLException e) {
            System.out.println("Tabla " + tabla + ": no accesible (" + e.getMessage() + ")");
        }
    }
}
