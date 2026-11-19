package com.olafo.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Crea la conexión JDBC entre Java y MySQL.
 * La configuración se lee de las variables del sistema o del archivo .env.
 *
 * Variables:
 * OLAFO_DB_USER (obligatoria)
 * OLAFO_DB_PASSWORD (obligatoria)
 * OLAFO_DB_URL (opcional; por defecto localhost:3306/artesanias_olafo)
 */
public class ConexionBD {
    private static final Map<String, String> DOTENV = cargarDotEnv();

    private static final String URL_POR_DEFECTO = "jdbc:mysql://localhost:3306/artesanias_olafo"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota";

    private static final String URL = obtenerUrl();
    private static final String USUARIO = obtenerVariable("OLAFO_DB_USER");
    private static final String PASSWORD = obtenerVariable("OLAFO_DB_PASSWORD");

    // Constructor privado: clase utilitaria.
    private ConexionBD() {
    }

    /**
     * Abre una conexión nueva; quien la pide debe cerrarla (try-with-resources).
     */
    public static Connection obtenerConexion() throws SQLException {
        if (USUARIO == null || USUARIO.isBlank()
                || PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException(
                    "Faltan OLAFO_DB_USER y OLAFO_DB_PASSWORD en el archivo .env "
                            + "o en las variables de entorno.");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "No se encontró el controlador JDBC de MySQL. "
                            + "Verifique que mysql-connector-j esté incluido en el despliegue.",
                    e);
        }

        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }

    private static String obtenerUrl() {
        String url = obtenerVariable("OLAFO_DB_URL");
        return (url == null || url.isBlank()) ? URL_POR_DEFECTO : url;
    }

    private static String obtenerVariable(String nombre) {
        String valorSistema = System.getenv(nombre);
        return valorSistema != null ? valorSistema : DOTENV.get(nombre);
    }

    private static Map<String, String> cargarDotEnv() {
        Map<String, String> variables = new HashMap<>();

        for (Path directorio : buscarDirectoriosDotEnv()) {
            if (directorio == null) {
                continue;
            }

            Path archivo = directorio.resolve(".env");
            cargarArchivoDotEnv(archivo, variables);
            if (!variables.isEmpty()) {
                return variables;
            }
        }

        try (InputStream entrada = ConexionBD.class
                .getResourceAsStream("/.env")) {
            if (entrada != null) {
                cargarContenidoDotEnv(
                        new BufferedReader(new InputStreamReader(
                                entrada, StandardCharsets.UTF_8)),
                        variables);
            }
        } catch (IOException e) {
            // Si no existe el recurso, se usaran las variables del entorno.
        }

        return variables;
    }

    private static Iterable<Path> buscarDirectoriosDotEnv() {
        java.util.List<Path> directorios = new java.util.ArrayList<>();

        agregarDirectorioYPadres(directorios, System.getProperty("user.dir"));
        agregarDirectorioYPadres(
                directorios,
                System.getProperty("catalina.base"));
        agregarDirectorioYPadres(
                directorios,
                System.getProperty("catalina.home"));

        return directorios;
    }

    private static void agregarDirectorioYPadres(
            java.util.List<Path> directorios,
            String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return;
        }

        Path directorio = Path.of(ruta).toAbsolutePath().normalize();
        while (directorio != null) {
            directorios.add(directorio);
            directorio = directorio.getParent();
        }
    }

    private static void cargarArchivoDotEnv(
            Path archivo,
            Map<String, String> variables) {
        if (!Files.isRegularFile(archivo)) {
            return;
        }

        try (BufferedReader lector = Files.newBufferedReader(
                archivo, StandardCharsets.UTF_8)) {
            cargarContenidoDotEnv(lector, variables);
        } catch (IOException e) {
            // Si no se puede leer, se conservaran las variables del entorno.
        }
    }

    private static void cargarContenidoDotEnv(
            BufferedReader lector,
            Map<String, String> variables) throws IOException {
        String linea;
        while ((linea = lector.readLine()) != null) {
            String entrada = linea.trim();
            if (entrada.isEmpty() || entrada.startsWith("#")) {
                continue;
            }

            int separador = entrada.indexOf('=');
            if (separador > 0) {
                String nombre = entrada.substring(0, separador).trim();
                String valor = entrada.substring(separador + 1).trim();

                if ((valor.startsWith("\"") && valor.endsWith("\""))
                        || (valor.startsWith("'") && valor.endsWith("'"))) {
                    valor = valor.substring(1, valor.length() - 1);
                }

                variables.put(nombre, valor);
            }
        }
    }
}
