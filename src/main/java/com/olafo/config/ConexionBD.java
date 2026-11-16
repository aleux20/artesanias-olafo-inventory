package com.olafo.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Crea la conexión JDBC entre Java y MySQL.
 * El usuario y la contraseña se leen de variables de entorno para no
 * dejar credenciales escritas en el código ni subirlas al repositorio.
 *
 * Variables:
 * OLAFO_DB_USER (obligatoria)
 * OLAFO_DB_PASSWORD (obligatoria)
 * OLAFO_DB_URL (opcional; por defecto localhost:3306/artesanias_olafo)
 */
public class ConexionBD {

    private static final String URL_POR_DEFECTO = "jdbc:mysql://localhost:3306/artesanias_olafo"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota";

    private static final String URL = obtenerUrl();
    private static final String USUARIO = System.getenv("OLAFO_DB_USER");
    private static final String PASSWORD = System.getenv("OLAFO_DB_PASSWORD");

    // Constructor privado: clase utilitaria.
    private ConexionBD() {
    }

    /**
     * Abre una conexión nueva; quien la pide debe cerrarla (try-with-resources).
     */
    public static Connection obtenerConexion() throws SQLException {
        if (USUARIO == null || USUARIO.isBlank() || PASSWORD == null) {
            throw new SQLException(
                    "Faltan las variables de entorno OLAFO_DB_USER y OLAFO_DB_PASSWORD.");
        }

        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }

    private static String obtenerUrl() {
        String url = System.getenv("OLAFO_DB_URL");
        return (url == null || url.isBlank()) ? URL_POR_DEFECTO : url;
    }
}
