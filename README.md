# Inventario Artesanías Olafo – Módulos AA2 EV01

Evidencia **GA7-220501096-AA2-EV01 – Codificación de módulos del software** (SENA – ADSO).

Módulos desarrollados con Java + JDBC + MySQL:

1. **Gestión de categorías** (tabla `categoria_producto`): registrar, consultar, actualizar y eliminar categorías.
2. **Gestión de productos terminados** (tabla `producto_terminado`): registrar, consultar, actualizar y desactivar productos asociados a una categoría existente.

Relación: un producto terminado debe pertenecer a una categoría (llave foránea `id_categoria_producto`, relación 1:N).

## Estructura

```
src/main/java/com/olafo/
  config/
    ConexionBD.java                  Conexión JDBC a MySQL
    PruebaConexion.java              Prueba de la conexión
  model/
    Categoria.java, Producto.java    Entidades (Producto contiene su Categoria)
  dao/
    CategoriaDAO.java, ProductoDAO.java            Interfaces (contrato CRUD)
  dao/impl/
    CategoriaDAOImpl.java, ProductoDAOImpl.java    Implementación JDBC
  service/
    CategoriaService.java, ProductoService.java    Validaciones y reglas de negocio
  app/
    MenuPrincipal.java               Menú de consola (usa solo los servicios)
  PruebaCategoriaDAO.java            Prueba del DAO de categorías
  PruebaCategoriaService.java        Prueba del servicio de categorías
  PruebaProductoService.java         Prueba del servicio de productos
```

Capas: `MenuPrincipal` → `Service` → `DAO` (interfaz) → `DAOImpl` (JDBC) → MySQL.

## Requisitos

- JDK 21
- MySQL 8.0.16 o superior
- Maven (descarga el driver `mysql-connector-j`)

## Cómo ejecutar

1. Definir las variables de entorno (las credenciales no se guardan en el código):

   | Variable | Obligatoria | Ejemplo |
   |---|---|---|
   | `OLAFO_DB_USER` | Sí | `root` |
   | `OLAFO_DB_PASSWORD` | Sí | *(su contraseña de MySQL)* |
   | `OLAFO_DB_URL` | No | `jdbc:mysql://localhost:3306/artesanias_olafo?useSSL=false&serverTimezone=America/Bogota` |

   En NetBeans/IntelliJ/Eclipse se definen en la configuración de ejecución de cada clase (*Environment variables*).
3. Ejecutar, en este orden:
   1. `com.olafo.config.PruebaConexion`
   2. `com.olafo.PruebaCategoriaDAO`, `PruebaCategoriaService`, `PruebaProductoService`
   3. `com.olafo.app.MenuPrincipal`

Las clases de prueba crean datos temporales y los eliminan al terminar.

## Estándares de codificación aplicados

- Paquetes por responsabilidad: `config`, `model`, `dao`, `dao.impl`, `service`, `app`.
- Clases en PascalCase, métodos y variables en camelCase con nombres descriptivos.
- `PreparedStatement` en todas las consultas (previene inyección SQL).
- `try-with-resources` para cerrar siempre conexión, sentencia y resultados.
- Validaciones en la capa de servicio; el DAO solo accede a datos.
- Productos: eliminación lógica (`activo = 0`). Categorías: eliminación física, porque la tabla no tiene columna de estado y la llave foránea impide borrar una categoría con productos.
