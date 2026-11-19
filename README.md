# Inventario Artesanías Olafo

## Evidencia GA7-220501096-AA2-EV02

**Módulos de software codificados y probados** — SENA, ADSO.

Aplicación web para administrar categorías y productos terminados de Artesanías Olafo. La interfaz se implementa con JSP, JSTL y Bootstrap; los Servlets reciben las solicitudes y las capas de servicio y DAO aplican las reglas de negocio y persisten la información en MySQL.

## Tecnologías

- Java 21
- Jakarta Servlets 6.0
- JSP y JSTL 3
- JDBC y MySQL Connector/J
- MySQL 8
- Maven (empaquetado WAR)
- Apache Tomcat 10.1
- Bootstrap 5.3.3

## Módulos implementados

### Categorías

- Listar, registrar, consultar para edición, actualizar y eliminar categorías.
- Validar los datos desde la capa de servicio.
- Impedir que la base de datos elimine una categoría que todavía tenga productos asociados.

### Productos terminados

- Listar, registrar, consultar para edición y actualizar productos.
- Asociar cada producto con una categoría existente.
- Consultar productos cuyo stock sea menor o igual al umbral indicado.
- Desactivar productos mediante eliminación lógica (`activo = 0`).
- Validar los datos desde la capa de servicio.

La relación entre las entidades es de uno a muchos: una categoría puede tener varios productos y cada producto pertenece a una categoría.

## Estructura principal

```text
src/main/java/com/olafo/
  config/       Conexión JDBC y prueba de conexión
  controller/   Servlets de categorías y productos
  dao/          Contratos de persistencia
  dao/impl/     Implementaciones JDBC
  model/        Entidades Categoria y Producto
  service/      Validaciones y reglas de negocio

src/main/webapp/
  index.jsp
  css/app.css
  WEB-INF/web.xml
  WEB-INF/views/
    fragmentos/ Fragmentos compartidos de navegación y pie de página
    categorias/ Vistas de listado y formulario
    productos/  Vistas de listado y formulario
    error.jsp   Página de error para respuestas 400, 404, 500 y excepciones
```

Flujo de una solicitud: **JSP → Servlet → Service → DAO → MySQL**. Las vistas se mantienen bajo `WEB-INF` para que se acceda a ellas mediante los Servlets.

## Requisitos

- JDK 21
- Maven
- MySQL 8, con la base `artesanias_olafo` y las tablas `categoria_producto` y `producto_terminado` creadas
- Apache Tomcat 10.1

La aplicación no crea automáticamente la base de datos ni sus tablas. La tabla `producto_terminado` debe tener una llave foránea hacia `categoria_producto`.

## Configuración de la base de datos

Define estas variables en el entorno con el que se inicia Tomcat. También se puede usar un archivo local `.env`; no incluyas credenciales reales en el repositorio ni en la evidencia que compartas.

| Variable            | Obligatoria | Valor de ejemplo                                                                                                       |
| ------------------- | ----------- | ---------------------------------------------------------------------------------------------------------------------- |
| `OLAFO_DB_USER`     | Sí          | `root`                                                                                                                 |
| `OLAFO_DB_PASSWORD` | Sí          | Contraseña local de MySQL                                                                                              |
| `OLAFO_DB_URL`      | No          | `jdbc:mysql://localhost:3306/artesanias_olafo?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota` |

Si no se define `OLAFO_DB_URL`, se utiliza una URL local para la base `artesanias_olafo`.

## Construcción y despliegue

1. Verifica que MySQL esté iniciado, que exista el esquema requerido y que las variables de conexión estén configuradas.
2. Desde la raíz del proyecto, genera el WAR:

   ```bash
   mvn clean package
   ```

3. Copia `target/inventario.war` al directorio `webapps` de Tomcat 10.1 e inicia el servidor.
4. Abre `http://localhost:8080/inventario/`.

Bootstrap se descarga desde jsDelivr; la interfaz necesita conexión a internet para cargar sus estilos.

## Pruebas y verificación

Ejecuta `mvn test` para verificar la compilación y el ciclo de pruebas Maven. Las clases `PruebaConexion`, `PruebaCategoriaDAO`, `PruebaCategoriaService` y `PruebaProductoService` son programas de prueba ejecutables desde el IDE; algunas operaciones requieren que MySQL y el esquema estén disponibles. Estas clases no se ejecutan automáticamente como pruebas JUnit dentro de `mvn test`.

Para comprobar los módulos desde la aplicación desplegada:

1. Registrar una categoría y verificar que aparezca en el listado.
2. Editar y eliminar una categoría; comprobar el manejo de una categoría que tenga productos asociados.
3. Registrar un producto con una categoría existente y verificar sus datos en el listado.
4. Editar el precio o el stock y comprobar que el cambio se conserve.
5. Consultar productos por stock bajo y desactivar un producto; verificar que ya no aparezca como activo.

Las clases de prueba de servicio crean datos temporales y tratan de eliminarlos al finalizar. Ejecuta las pruebas con una base de datos de desarrollo.
