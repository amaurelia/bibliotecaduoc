# BibliotecaDuoc - API REST con Spring Boot + JPA + MySQL

Proyecto académico de ejemplo para aprender arquitectura por capas con Spring Boot:

- `controller` (capa web / endpoints REST)
- `service` (lógica de negocio)
- `repository` (acceso a datos con **JPA / Hibernate**)
- `model` (entidades JPA / estructura de datos)

---

## Índice

- [1) Requisitos](#1-requisitos)
- [2) Configuración de base de datos](#2-configuración-de-base-de-datos)
- [3) ¿Cómo ejecutar el proyecto?](#3-cómo-ejecutar-el-proyecto)
- [4) URL base de la API](#4-url-base-de-la-api)
- [5) Endpoints disponibles](#5-endpoints-disponibles)
- [6) Estructura del proyecto y explicación por capas](#6-estructura-del-proyecto-y-explicación-por-capas)
- [7) Colección de Postman](#7-colección-de-postman)
- [8) Dependencias principales (pom.xml)](#8-dependencias-principales-pomxml)
- [9) JpaRepository: métodos usados en este proyecto](#9-jparepository-métodos-usados-en-este-proyecto)
- [10) Manejador global de errores (@ControllerAdvice)](#10-manejador-global-de-errores-controlleradvice)
- [11) ResponseEntity: manejo de respuestas HTTP](#11-responseentity-manejo-de-respuestas-http)
- [12) WebClient y consumo de APIs externas](#12-webclient-y-consumo-de-apis-externas)
- [13) Spring Security y autenticación JWT](#13-spring-security-y-autenticación-jwt)
- [14) Swagger / OpenAPI](#14-swagger--openapi)
- [15) Pruebas unitarias: guía paso a paso](#15-pruebas-unitarias-guía-paso-a-paso)
- [16) Docker: guía para principiantes](#16-docker-guía-para-principiantes)
- [17) Despliegue en Railway (GitHub + MySQL)](#17-despliegue-en-railway-github--mysql)
- [18) Autor](#18-autor)

---

## 1) Requisitos

- Java 17
- Maven (opcional si usas `mvnw`)
- **MySQL** corriendo en `localhost:3306` (usuario `root`, sin contraseña)
- IDE recomendado: VS Code / IntelliJ / Eclipse
- Postman (opcional para probar la API)

> Hibernate crea automáticamente la tabla `libros` al iniciar la aplicación (`ddl-auto=update`). No es necesario crearla manualmente.

---

## 2) Configuración de base de datos

El archivo `src/main/resources/application.properties` contiene la conexión:

```properties
server.port=${PORT:8080}
spring.datasource.url=${DB_URL:${MYSQL_URL}}
spring.datasource.username=${DB_USERNAME:${MYSQLUSER}}
spring.datasource.password=${DB_PASSWORD:${MYSQLPASSWORD}}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

- `createDatabaseIfNotExist=true` → crea la base de datos `bibliotecaduoc` si no existe.
- `ddl-auto=update` → Hibernate actualiza el esquema automáticamente según la entidad `Libro`.
- `show-sql=true` → muestra las consultas SQL generadas en la consola.

### Recomendación de seguridad (credenciales)

No subas credenciales reales al repositorio. Este proyecto ya está preparado para leer secretos desde variables de entorno:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`

Para desarrollo local puedes exportarlas en terminal (PowerShell):

```powershell
$env:DB_URL="jdbc:mysql://localhost:3307/bibliotecaduoc?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="tu_password"
$env:JWT_SECRET="una-clave-larga-y-segura-de-al-menos-32-caracteres"
.\mvnw.cmd spring-boot:run
```

Para Docker Compose, usa archivo `.env` (no versionado). Hay una plantilla en `.env.example`.

> Nota: en esta configuración **no hay valores por defecto** para credenciales/secretos. Si falta una variable de entorno, la app/compose fallará al iniciar (comportamiento intencional).

## 3) ¿Cómo ejecutar el proyecto?

Esta sección es para ejecución local (sin contenedores). La guía completa de Docker está en la sección **15) Docker: guía para principiantes**.

### Opción A: usando Maven Wrapper (recomendado)

Desde la carpeta raíz del proyecto (`bibliotecaduoc`):

#### En Windows (PowerShell / CMD)

```bash
.\mvnw.cmd spring-boot:run
```

#### En Linux / macOS

```bash
./mvnw spring-boot:run
```

### Opción B: compilar y ejecutar el JAR

```bash
./mvnw clean package
java -jar target/bibliotecaduoc-0.0.1-SNAPSHOT.jar
```

> En Windows, reemplaza `./mvnw` por `.\mvnw.cmd`.

---

## 4) URL base de la API

Por defecto Spring Boot levanta en puerto `8080`:

```text
http://localhost:8080
```

Base path del controlador:

```text
/api/v1/libros
```

---

## 5) Endpoints disponibles

### 5.1 Listar libros
- **Método:** `GET`
- **URL:** `/api/v1/libros`
- **Descripción:** retorna todos los libros almacenados en la base de datos.

### 5.2 Buscar libro por ID
- **Método:** `GET`
- **URL:** `/api/v1/libros/{id}`
- **Descripción:** retorna un libro por su id.

### 5.3 Crear libro
- **Método:** `POST`
- **URL:** `/api/v1/libros`
- **Body JSON ejemplo:**

```json
{
	"id": 1,
	"isbn": "9789561234567",
	"titulo": "Clean Code",
	"editorial": "Prentice Hall",
	"fechaPublicacion": 2008,
	"autor": "Robert C. Martin"
}
```

### 5.4 Actualizar libro
- **Método:** `PUT`
- **URL:** `/api/v1/libros/{id}`
- **Body JSON ejemplo:**

```json
{
	"id": 1,
	"isbn": "9789561234567",
	"titulo": "Clean Code (Edición actualizada)",
	"editorial": "Prentice Hall",
	"fechaPublicacion": 2009,
	"autor": "Robert C. Martin"
}
```

### 5.5 Eliminar libro
- **Método:** `DELETE`
- **URL:** `/api/v1/libros/{id}`
- **Descripción:** elimina un libro por id.

### 5.6 Listar libros con nacionalidad del autor
- **Método:** `GET`
- **URL:** `/api/v1/libros/con-nacionalidad`
- **Descripción:** retorna todos los libros junto con la nacionalidad de su autor, usando un DTO proyectado (sin exponer la entidad completa).
- **Respuesta JSON ejemplo:**

```json
[
  { "titulo": "Cien años de soledad", "nacionalidadAutor": "Colombiana" },
  { "titulo": "La casa de los espíritus", "nacionalidadAutor": "Chilena" }
]
```

#### ¿Cómo funciona el DTO internamente?

La clase `LibroNacionalidadDTO` es un objeto simple (POJO) con solo los campos que queremos exponer.
En el servicio se usa un **stream** para transformar la lista de entidades `Libro` en una lista de DTOs:

```java
libroRepository.findAll().stream()
    .filter(l -> l.getAutor() != null)  // evita NullPointerException si el libro no tiene autor
    .map(l -> new LibroNacionalidadDTO(
            l.getTitulo(),
            l.getAutor().getNacionalidad()
    ))
    .toList();  // equivalente a .collect(Collectors.toList()) en Java 16+
```

| Paso | ¿Es obligatorio? | Descripción |
|---|---|---|
| `.stream()` | Sí, en este enfoque | Convierte la lista en un flujo procesable |
| `.filter(...)` | Sí | Descarta libros sin autor para evitar `NullPointerException` |
| `.map(...)` | Sí | Transforma cada `Libro` en un `LibroNacionalidadDTO` |
| `.toList()` | Sí | Materializa el stream en una `List` |

> **Alternativa sin streams:** se puede definir la query directamente en el repositorio con JPQL
> y un constructor, evitando cargar las entidades completas en memoria:
> ```java
> @Query("SELECT new com.example.bibliotecaduoc.dto.LibroNacionalidadDTO(l.titulo, l.autor.nacionalidad) FROM Libro l WHERE l.autor IS NOT NULL")
> List<LibroNacionalidadDTO> findLibrosConNacionalidad();
> ```

### 5.7 Libro con HATEOAS
- **Método:** `GET`
- **URL:** `/api/v1/libros/hateoas/{id}`
- **Descripción:** retorna un libro más una colección de links relacionados para navegar la API.
- **¿Qué links aparecen?**
  - `self`: enlace al mismo recurso HATEOAS.
  - `detalle-sin-hateoas`: enlace al endpoint tradicional del libro.
  - `coleccion`: enlace a la colección HATEOAS de libros.
  - `autor`: enlace al autor asociado, si existe.

**Ejemplo de respuesta:**

```json
{
  "id": 1,
  "isbn": "9780132350884",
  "titulo": "Clean Code",
  "editorial": "Prentice Hall",
  "fechaPublicacion": 2008,
  "autor": {
    "id": 1,
    "nombre": "Robert C. Martin",
    "edad": 73,
    "nacionalidad": "Estadounidense"
  },
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/v1/libros/hateoas/1"
    },
    "detalle-sin-hateoas": {
      "href": "http://localhost:8080/api/v1/libros/1"
    },
    "coleccion": {
      "href": "http://localhost:8080/api/v1/libros/hateoas"
    },
    "autor": {
      "href": "http://localhost:8080/api/v1/autores/1"
    }
  }
}
```

### 5.8 Colección de libros con HATEOAS
- **Método:** `GET`
- **URL:** `/api/v1/libros/hateoas`
- **Descripción:** retorna la lista de libros en formato HATEOAS.

**¿Para qué sirve esto?**

HATEOAS agrega links automáticos en la respuesta para que el cliente descubra rutas relacionadas sin hardcodearlas manualmente. En este proyecto se usa como ejemplo didáctico para comparar una respuesta REST tradicional vs una respuesta hipermedia.

---

## 6) Estructura del proyecto y explicación por capas

```text
src/main/java/com/example/bibliotecaduoc/
├── controller/
├── dto/
├── service/
├── repository/
└── model/
```

### 5.1 `controller` (presentación / API REST)

En esta carpeta está `LibroController`, que recibe las peticiones HTTP.

Anotaciones importantes:

- `@RestController`
	- Le dice a Spring que esta clase es un controlador REST.
	- Los métodos retornan datos (JSON) directamente.

- `@RequestMapping("/api/v1/libros")`
	- Define la ruta base para todos los endpoints de este controlador.

- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
	- Asocian cada método Java con un verbo HTTP.

- `@PathVariable`
	- Obtiene valores de la URL, por ejemplo `{id}`.

- `@RequestBody`
	- Convierte automáticamente JSON del request a un objeto Java (`Libro`).

- `@Autowired`
	- Inyección de dependencias automática.
	- Spring inyecta una instancia de `LibroService`.

### 5.1.1 ResponseEntity: qué funciones puedes usar

`ResponseEntity<T>` representa una respuesta HTTP completa. Incluye:

- Código de estado (por ejemplo: 200, 201, 404)
- Cuerpo de la respuesta (el objeto `T`)
- Encabezados HTTP

En este proyecto se usa en controladores para devolver respuestas claras y también en pruebas unitarias para validar el comportamiento.

Métodos de instancia más útiles:

| Método | ¿Qué devuelve? | ¿Para qué sirve en pruebas? |
|---|---|---|
| `getStatusCode()` | `HttpStatusCode` | Verificar el estado HTTP esperado (por ejemplo `CREATED`). |
| `getBody()` | `T` (puede ser `null`) | Obtener el objeto de respuesta para validar sus campos. |
| `getHeaders()` | `HttpHeaders` | Validar headers como `Location` o `Content-Type`. |
| `hasBody()` | `boolean` | Confirmar si la respuesta trae cuerpo o no. |
| `toString()` | `String` | Útil para depuración rápida en consola. |
| `equals()` / `hashCode()` | comparación/clave | Útil en comparaciones o colecciones (menos común en tests básicos). |

Ejemplo típico de validación en un test:

```java
var respuesta = libroController.agregarLibro(libro);

assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
assertTrue(respuesta.hasBody());
assertNotNull(respuesta.getBody());
assertEquals("Cien años de soledad", respuesta.getBody().getTitulo());
```

Métodos estáticos útiles para construir respuestas en controladores:

- `ResponseEntity.ok(...)`
- `ResponseEntity.status(...)`
- `ResponseEntity.created(...)`
- `ResponseEntity.accepted()`
- `ResponseEntity.noContent()`
- `ResponseEntity.badRequest()`
- `ResponseEntity.notFound()`
- `ResponseEntity.internalServerError()`
- `ResponseEntity.of(...)` y `ResponseEntity.ofNullable(...)`

Ejemplos de uso en este proyecto:

- [src/main/java/com/example/bibliotecaduoc/controller/LibroController.java](src/main/java/com/example/bibliotecaduoc/controller/LibroController.java)
- [src/test/java/com/example/bibliotecaduoc/controller/LibroControllerTest.java](src/test/java/com/example/bibliotecaduoc/controller/LibroControllerTest.java)

### 5.2 `service` (lógica de negocio)

En esta carpeta está `LibroService`.

Responsabilidades:
- Centralizar reglas y flujo de negocio.
- Evitar que el controlador tenga lógica compleja.
- Coordinar acceso al repositorio.

Anotación clave:

- `@Service`
	- Marca la clase como componente de la capa de servicio.
	- Spring la detecta y la gestiona como bean.

También usa `@Autowired` para inyectar `LibroRepository`.

### 5.3 `repository` (acceso a datos)

En esta carpeta está `LibroRepository`.

Ahora es una **interfaz** que extiende `JpaRepository<Libro, Integer>`:

```java
@Repository
public interface LibroRepository extends JpaRepository<Libro, Integer> { }
```

Al extender `JpaRepository`, Spring Data JPA genera automáticamente la implementación con todos los métodos CRUD:

| Método JPA | Descripción |
|---|---|
| `findAll()` | Obtiene todos los registros |
| `findById(id)` | Busca por id, retorna `Optional<Libro>` |
| `save(libro)` | Inserta o actualiza |
| `existsById(id)` | Verifica si existe |
| `deleteById(id)` | Elimina por id |

> Ya no existe la lista en memoria. Los datos se persisten en MySQL y **sobreviven al reinicio** de la aplicación.

Anotación clave:

- `@Repository`
	- Indica que esta interfaz pertenece a la capa de acceso a datos.

### 5.4 `model` (entidades / estructura de datos)

En esta carpeta está `Libro`, que representa los datos de un libro.

Campos actuales:
- `id`
- `isbn`
- `titulo`
- `editorial`
- `fechaPublicacion`
- `autor`

Anotaciones de validación y persistencia usadas:

- `@Id`
	- Marca el identificador de la entidad.
	- En este proyecto sirve para señalar que `id` es la clave del libro.
	- Nota: como el repositorio actual es en memoria (`List<Libro>`), no hay persistencia real en base de datos todavía.

- `@NotNull`
	- Exige que el valor no sea `null`.
	- Se usa en `id` y `fechaPublicacion`.

- `@NotBlank`
	- Exige que el texto no sea `null`, no esté vacío (`""`) y no tenga solo espacios.
	- Se usa en `isbn`, `titulo`, `editorial` y `autor`.

Anotaciones de Lombok usadas:

- `@Data`
	- Genera automáticamente:
		- getters y setters
		- `toString()`
		- `equals()` y `hashCode()`

- `@AllArgsConstructor`
	- Genera un constructor con **todos** los atributos.

- `@NoArgsConstructor`
	- Genera un constructor **vacío** (sin parámetros).

Esto evita escribir mucho código repetitivo (boilerplate).

### 5.5 Clase principal de la aplicación

Archivo: `BibliotecaduocApplication`.

- `@SpringBootApplication`
	- Es una anotación compuesta que incluye configuración automática de Spring Boot y escaneo de componentes.
	- Indica el punto de inicio de la aplicación.

### 5.6 Pruebas

Archivo: `BibliotecaduocApplicationTests`.

- `@SpringBootTest`
	- Levanta el contexto completo de Spring para pruebas de integración.

- `@Test`
	- Marca un método como caso de prueba en JUnit 5.

### 5.7 Resumen completo de anotaciones del proyecto

Estas son **todas** las anotaciones usadas actualmente en el código fuente:

- Spring Web/API: `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@RequestBody`, `@PathVariable`
- Inyección y estereotipos Spring: `@Autowired`, `@Service`, `@Repository`
- Spring Boot: `@SpringBootApplication`, `@SpringBootTest`
- Validación/Persistencia Jakarta: `@Id`, `@NotBlank`, `@NotNull`
- Lombok: `@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`
- Testing JUnit: `@Test`

---

## 7) Colección de Postman

Se incluye una colección lista para importar:

`postman/BibliotecaDuoc.postman_collection.json`

Incluye todos los endpoints:
- Listar libros
- Buscar libro por ID
- Crear libro
- Actualizar libro
- Eliminar libro
- Libros con HATEOAS
- Endpoints de autenticación JWT
- Endpoints de autores y clima

### Cómo importarla en Postman

1. Abrir Postman.
2. Clic en **Import**.
3. Seleccionar el archivo `postman/BibliotecaDuoc.postman_collection.json`.
4. Crear un Environment con estas variables:

```text
baseUrl = http://localhost:8080
token =
```

5. Seleccionar ese Environment en la esquina superior derecha de Postman.
6. Ejecutar `Login`; el script guarda automáticamente el JWT en la variable de environment `token`.
7. Los demás requests usan `Authorization: Bearer {{token}}` automáticamente.

> Importante: en esta colección el token debe vivir en el **Environment** de Postman, no como variable manual pegada request por request.

---

## 8) Dependencias principales (pom.xml)

- `spring-boot-starter-webmvc`
	- Para construir API REST.

- `spring-boot-starter-validation`
	- Soporte de validaciones con anotaciones como `@NotBlank` y `@NotNull`.

- `jakarta.persistence-api`
	- API de persistencia Jakarta (incluye anotaciones como `@Id`).

- `lombok`
	- Para reducir código repetitivo en modelos.

- `spring-boot-starter-webmvc-test`
	- Soporte de testing para capa web.

- `springdoc-openapi-starter-webmvc-ui`
  - Genera documentación OpenAPI y la interfaz Swagger UI.

- `spring-boot-starter-hateoas`
  - Permite construir respuestas con links hipermedia (`_links`) usando `EntityModel` y `CollectionModel`.

---

## 9) JpaRepository: métodos usados en este proyecto

Tanto `LibroRepository` como `AutorRepository` extienden `JpaRepository`:

```java
public interface LibroRepository extends JpaRepository<Libro, Integer> { }
public interface AutorRepository extends JpaRepository<Autor, Integer> { }
```

Al extender `JpaRepository<T, ID>`, Spring Data JPA **genera automáticamente** la implementación de los métodos de acceso a datos. No hace falta escribir ninguna consulta SQL manualmente.

Los parámetros de tipo son:
- `T` → la entidad (`Libro` o `Autor`)
- `ID` → el tipo del identificador (`Integer`)

---

### Métodos usados en los servicios de este proyecto

| Método | Dónde se usa | Qué hace |
|--------|-------------|----------|
| `findAll()` | `getLibros()`, `getAutores()` | Ejecuta `SELECT * FROM libros` (o `autores`) y retorna una `List<T>` con todos los registros. |
| `save(entity)` | `saveLibro()`, `saveAutor()`, `updateLibro()`, `updateAutor()` | Si la entidad **no tiene id** (o el id no existe en BD) hace un `INSERT`. Si el id ya existe, hace un `UPDATE`. Retorna la entidad guardada (con el id asignado por la BD en caso de inserción). |
| `findById(id)` | `getLibroId()`, `getAutorId()` | Ejecuta `SELECT ... WHERE id = ?` y retorna un `Optional<T>`. Se usa `.orElse(null)` para obtener el objeto o `null` si no existe. |
| `existsById(id)` | `updateLibro()`, `updateAutor()` | Ejecuta un `SELECT COUNT(*)` para verificar si existe un registro con ese id. Retorna `boolean`. Se usa para evitar hacer un `save` sobre un id inexistente (lo que crearía un registro nuevo en lugar de actualizar). |
| `deleteById(id)` | `deleteLibro()`, `deleteAutor()` | Ejecuta `DELETE FROM ... WHERE id = ?`. Si el id no existe, lanza `EmptyResultDataAccessException`. |

---

### Ejemplo: flujo completo de `updateLibro`

```java
public Libro updateLibro(Libro libro) {
    if (!libroRepository.existsById(libro.getId())) {  // ← existsById
        return null;                                    //   si no existe, retorna null (404)
    }
    return libroRepository.save(libro);                // ← save hace UPDATE porque el id ya existe
}
```

### ¿Por qué no hay SQL en el repositorio?

Spring Data JPA utiliza **Hibernate** como proveedor de JPA. Hibernate traduce las llamadas a los métodos del repositorio a consultas SQL reales, que se pueden ver en la consola gracias al parámetro `show-sql=true` del `application.properties`.

---

## 10) Manejador global de errores (`@ControllerAdvice`)

### ¿Qué es?

`@ControllerAdvice` es una anotación de Spring que permite crear una **clase centralizada** que intercepta las excepciones lanzadas por cualquier controlador de la aplicación.

En lugar de poner `try-catch` en cada endpoint (código repetitivo y difícil de mantener), defines el manejo de errores **una sola vez** y Spring lo aplica automáticamente a todos los controladores.

---

### Paso a paso: cómo funciona

#### Paso 1 — Crear la clase de error (`ApiError`)

Primero se define un modelo simple que representa la estructura del JSON de error que recibirá el cliente:

```java
// exception/ApiError.java
public class ApiError {
    private int codigo;      // código HTTP numérico (400, 500...)
    private String mensaje;  // descripción corta del error
    private String detalle;  // información técnica / campo con problema
}
```

Cuando algo falla, la API devuelve este JSON en lugar de un mensaje de texto plano:
```json
{
  "codigo": 400,
  "mensaje": "Error de validación",
  "detalle": "nombre: no debe estar vacío, edad: no debe ser nulo"
}
```

#### Paso 2 — Crear el manejador global (`GlobalExceptionHandler`)

```java
// exception/GlobalExceptionHandler.java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Se activa cuando @Valid falla → 400 Bad Request
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex) {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
                .body(new ApiError(400, "Error de validación", detalle));
    }

    // Se activa ante cualquier excepción no esperada → 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError(500, "Error interno del servidor", ex.getMessage()));
    }
}
```

#### Paso 3 — Limpiar los controladores

Con el manejador global activo, los controladores ya **no necesitan `try-catch`**. Spring intercepta las excepciones automáticamente antes de que lleguen al cliente:

```java
// ANTES (con try-catch en cada método)
public ResponseEntity<?> agregarAutor(@Valid @RequestBody Autor autor) {
    try {
        return ResponseEntity.status(HttpStatus.CREATED).body(autorService.saveAutor(autor));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
    }
}

// DESPUÉS (limpio, sin try-catch)
public ResponseEntity<Autor> agregarAutor(@Valid @RequestBody Autor autor) {
    return ResponseEntity.status(HttpStatus.CREATED).body(autorService.saveAutor(autor));
}
```

---

### Flujo completo

```
Cliente envía request
       ↓
Controlador ejecuta el método
       ↓
   ¿Ocurre una excepción?
      ↙            ↘
    NO              SÍ
     ↓               ↓
Responde normal   Spring la intercepta
(200, 201, etc.)  y la envía al GlobalExceptionHandler
                       ↓
              ¿Qué tipo de excepción es?
              ↙                        ↘
  MethodArgumentNotValidException    Exception (cualquier otra)
  (fallo de @Valid)                  (error de BD, null, etc.)
       ↓                                    ↓
  400 Bad Request + ApiError          500 Internal Server Error + ApiError
```

---

### Estructura de archivos agregada

```text
src/main/java/com/example/bibliotecaduoc/
└── exception/
    ├── ApiError.java              ← modelo de respuesta de error
    └── GlobalExceptionHandler.java ← manejador central con @RestControllerAdvice
```

---

### Anotaciones clave

| Anotación | Descripción |
|---|---|
| `@RestControllerAdvice` | Marca la clase como manejadora global de excepciones para controladores REST |
| `@ExceptionHandler(X.class)` | Indica qué método se ejecuta cuando ocurre una excepción de tipo `X` |
| `MethodArgumentNotValidException` | Excepción que lanza Spring cuando `@Valid` detecta un campo inválido |

---

## 11) ResponseEntity: manejo de respuestas HTTP

### ¿Qué es `ResponseEntity`?

`ResponseEntity` es una clase de Spring que representa **una respuesta HTTP completa**: código de estado, cabeceras y cuerpo.

Al usarla en los controladores, el servidor le comunica al cliente no solo los datos, sino también el resultado de la operación a través del código HTTP. Esto es fundamental en una API REST bien diseñada.

```java
// Sin ResponseEntity → Spring asume siempre HTTP 200, sin control
public List<Autor> listarAutores() { ... }

// Con ResponseEntity → control total sobre la respuesta
public ResponseEntity<?> listarAutores() { ... }
```

El tipo genérico `<?>` (wildcard) se usa cuando el método puede devolver distintos tipos según el resultado: por ejemplo, un objeto `Autor` en el caso exitoso y un `String` con el mensaje de error en el caso fallido.

---

### Variantes usadas en este proyecto

#### `ResponseEntity.ok(cuerpo)` → HTTP 200 OK

```java
return ResponseEntity.ok(actualizado);
```

- **Cuándo se usa:** cuando la operación fue exitosa y se devuelve un dato al cliente.
- **Casos típicos:** listar todos los registros, buscar por id (cuando existe), actualizar (cuando existe).
- **Respuesta:** HTTP 200 + el objeto en formato JSON.

---

#### `ResponseEntity.status(HttpStatus.CREATED).body(cuerpo)` → HTTP 201 Created

```java
return ResponseEntity.status(HttpStatus.CREATED).body(autorService.saveAutor(autor));
```

- **Cuándo se usa:** cuando se crea un nuevo recurso exitosamente.
- **Casos típicos:** endpoint `POST` que inserta un nuevo registro en la base de datos.
- **Respuesta:** HTTP 201 + el objeto recién creado en formato JSON.
- **¿Por qué no usar 200?** El estándar HTTP reserva el 201 específicamente para creaciones, lo que hace la API más expresiva y correcta semánticamente.

---

#### `ResponseEntity.notFound().build()` → HTTP 404 Not Found

```java
if (autor == null) {
    return ResponseEntity.notFound().build();
}
```

- **Cuándo se usa:** cuando el recurso solicitado no existe en la base de datos.
- **Casos típicos:** buscar o actualizar por un id que no existe.
- **Respuesta:** HTTP 404, sin cuerpo (`.build()` indica que no hay body).
- **¿Qué significa `.build()`?** Construye la respuesta sin agregar ningún cuerpo. Se usa cuando no hay datos que devolver.

---

#### `ResponseEntity.noContent().build()` → HTTP 204 No Content

```java
autorService.deleteAutor(id);
return ResponseEntity.noContent().build();
```

- **Cuándo se usa:** cuando la operación fue exitosa pero no hay nada que devolver.
- **Casos típicos:** endpoint `DELETE` que elimina un registro correctamente.
- **Respuesta:** HTTP 204, sin cuerpo.
- **¿Por qué no usar 200?** Al eliminar, no hay objeto que retornar. El 204 indica éxito sin contenido.

---

#### `ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mensaje)` → HTTP 500 Internal Server Error

```java
} catch (Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al agregar autor: " + e.getMessage());
}
```

- **Cuándo se usa:** cuando ocurre un error inesperado en el servidor al procesar la solicitud.
- **Casos típicos:** falla de conexión a la base de datos, error de constraint, excepción no controlada.
- **Respuesta:** HTTP 500 + mensaje descriptivo del error como texto.
- **¿Por qué capturar `Exception`?** Captura cualquier excepción que pueda lanzar la capa de servicio o repositorio, evitando que el servidor devuelva un error genérico sin información.

---

### Tabla resumen

| Método | Código HTTP | Cuándo usarlo |
|--------|-------------|---------------|
| `ResponseEntity.ok(body)` | **200 OK** | Operación exitosa con datos que devolver |
| `ResponseEntity.status(CREATED).body(body)` | **201 Created** | Recurso creado exitosamente (POST) |
| `ResponseEntity.notFound().build()` | **404 Not Found** | El recurso solicitado no existe |
| `ResponseEntity.noContent().build()` | **204 No Content** | Operación exitosa sin datos que devolver (DELETE) |
| `ResponseEntity.status(INTERNAL_SERVER_ERROR).body(msg)` | **500 Internal Server Error** | Error inesperado en el servidor |

---

## 12) WebClient y consumo de APIs externas

### ¿Qué es WebClient?

`WebClient` es el cliente HTTP reactivo de Spring (parte de **Spring WebFlux**). A diferencia del antiguo `RestTemplate`, está diseñado para llamadas HTTP modernas y soporta tanto el modo reactivo (asíncrono) como el bloqueante (síncrono). En este proyecto se usa en modo **bloqueante** con `.block()` para conservar la arquitectura simple del resto del código.

### ¿Por qué WebClient y no RestTemplate?

| | `RestTemplate` | `WebClient` |
|---|---|---|
| Estado | Obsoleto (deprecated desde Spring 5) | Recomendado actualmente |
| Estilo | Solo síncrono/bloqueante | Reactivo y bloqueante |
| Configuración | Por instancia directa | Por `@Bean` reutilizable |

---

### ¿Cómo se configura? — `WebClientConfig`

La clase `WebClientConfig` vive en el paquete `config` y usa la anotación `@Configuration`, lo que le indica a Spring que contiene definiciones de **beans** (objetos gestionados por el contenedor de Spring).

```java
@Configuration
public class WebClientConfig {

    @Value("${openmeteo.base-url}")
    private String openMeteoBaseUrl;

    @Bean
    public WebClient weatherWebClient() {
        return WebClient.builder()
                .baseUrl(openMeteoBaseUrl)
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
```

| Elemento | Descripción |
|---|---|
| `@Configuration` | Indica que la clase declara beans de Spring |
| `@Value("${openmeteo.base-url}")` | Lee la URL base desde `application.properties`, evitando escribirla directamente en el código |
| `@Bean` | Le dice a Spring que el método produce un bean gestionado. Puede inyectarse con `@Autowired` en cualquier servicio |
| `.baseUrl(...)` | URL raíz que se antepone automáticamente a cada llamada |
| `.defaultHeader(...)` | Cabecera incluida en todas las peticiones del cliente |

La URL base se define en `application.properties`:

```properties
# Open-Meteo API (clima, sin API key)
openmeteo.base-url=https://api.open-meteo.com
```

---

### ¿Cómo se usa? — `WeatherService`

El servicio inyecta el bean mediante `@Autowired` + `@Qualifier` (necesario porque podría haber varios `WebClient` beans registrados):

```java
@Service
public class WeatherService {

    @Autowired
    @Qualifier("weatherWebClient")
    private WebClient weatherWebClient;

    public WeatherDTO obtenerClima(double latitude, double longitude) {
        return weatherWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/forecast")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("current_weather", true)
                        .build())
                .retrieve()
                .bodyToMono(WeatherDTO.class)
                .block();
    }
}
```

| Paso | Descripción |
|---|---|
| `.get()` | Define el método HTTP (GET) |
| `.uri(...)` | Construye la URL final, usando un `UriBuilder` para agregar query params de forma segura |
| `.retrieve()` | Ejecuta la petición y prepara la lectura de la respuesta |
| `.bodyToMono(WeatherDTO.class)` | Deserializa el JSON de respuesta al DTO indicado |
| `.block()` | Bloquea el hilo hasta recibir la respuesta (modo síncrono) |

La URL real que WebClient construye y llama internamente para Santiago de Chile es:

```
https://api.open-meteo.com/v1/forecast?latitude=-33.45&longitude=-70.65&current_weather=true
```

> Puedes pegar esa URL directamente en el navegador para ver la respuesta JSON cruda de Open-Meteo, **sin pasar por tu aplicación**. Lo que hace WebClient es exactamente eso, pero desde dentro del servidor Java y mapeando el resultado al `WeatherDTO`.

---

### Endpoint del clima — `WeatherController`

```
GET /api/v1/clima
```

- **Parámetros opcionales:**

| Parámetro | Tipo | Default | Descripción |
|---|---|---|---|
| `lat` | `double` | `-33.45` | Latitud |
| `lon` | `double` | `-70.65` | Longitud |

- **Ejemplos:**

```
GET /api/v1/clima                          → clima actual de Santiago de Chile
GET /api/v1/clima?lat=-41.47&lon=-72.94   → clima actual de Puerto Montt
```

- **Respuesta exitosa (200):**

```json
{
  "latitude": -33.45,
  "longitude": -70.65,
  "current_weather": {
    "temperature": 14.5,
    "windspeed": 22.3,
    "winddirection": 180,
    "weathercode": 3,
    "is_day": 1,
    "time": "2024-01-15T12:00"
  }
}
```

| Campo | Descripción |
|---|---|
| `temperature` | Temperatura actual en °C |
| `windspeed` | Velocidad del viento en km/h |
| `winddirection` | Dirección del viento en grados (0–360) |
| `weathercode` | Código WMO del estado del tiempo (0 = despejado, 3 = nublado, etc.) |
| `is_day` | `1` si es de día, `0` si es de noche |
| `time` | Hora de la medición |

---

### Flujo completo de una llamada al clima

```
Cliente
  │
  │  GET /api/v1/clima?lat=-33.45&lon=-70.65
  ▼
WeatherController
  │  weatherService.obtenerClima(-33.45, -70.65)
  ▼
WeatherService
  │  weatherWebClient.get().uri(...).retrieve().bodyToMono(WeatherDTO.class).block()
  ▼
Open-Meteo API (https://api.open-meteo.com)
  │  200 OK + JSON con datos del clima
  ▼
WeatherService
  │  deserializa JSON → WeatherDTO
  ▼
WeatherController
  │  ResponseEntity.ok(weatherDTO)
  ▼
Cliente
     200 OK + JSON
```

---

### API externa utilizada: Open-Meteo

- **URL base:** `https://api.open-meteo.com`
- **Gratuita:** sí, sin necesidad de API Key
- **Documentación:** [open-meteo.com](https://open-meteo.com)

---

## 13) Spring Security y autenticación JWT

### ¿Qué es Spring Security?

**Spring Security** es el framework de seguridad estándar del ecosistema Spring. Se integra como una cadena de **filtros HTTP** que intercepta cada request antes de que llegue a los controladores, de manera completamente transparente para el código de negocio existente.

Sus dos responsabilidades principales son:

| Concepto | Definición | Ejemplo en este proyecto |
|---|---|---|
| **Autenticación** | Verificar *quién eres* | El usuario hace `POST /api/v1/auth/login` con su usuario y contraseña |
| **Autorización** | Verificar *qué puedes hacer* | Un `USER` solo puede leer libros; un `ADMIN` puede crear, editar y eliminar |

---

### Estrategia elegida: JWT (JSON Web Token)

Una API REST es **stateless** (sin estado), lo que significa que el servidor no guarda sesiones entre requests. Por eso se usa JWT en lugar de cookies de sesión:

1. El usuario hace login → el servidor genera y firma un **token JWT**.
2. El cliente guarda ese token y lo envía en el **header `Authorization`** de cada request posterior.
3. El servidor verifica la firma del token en cada request, sin necesidad de consultar la base de datos.

#### ¿Qué es un JWT?

Un token JWT tiene tres partes separadas por puntos:

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOi...  .xYz_firma
   ^ Header                  ^ Payload (claims)                ^ Signature
```

| Parte | Contenido |
|---|---|
| **Header** | Algoritmo de firma (`HS256`) |
| **Payload** | `sub` (username), `role`, `iat` (emitido), `exp` (vencimiento) |
| **Signature** | HMAC-SHA256 del header+payload firmado con la clave secreta del servidor |

El token **no está encriptado** (Base64URL), pero sí está **firmado**: cualquier modificación invalida la firma.

---

### Roles implementados

| Rol | Permisos |
|---|---|
| `ROLE_USER` | `GET` en cualquier endpoint de `/api/v1/**` |
| `ROLE_ADMIN` | `GET`, `POST`, `PUT`, `DELETE` en cualquier endpoint de `/api/v1/**` |

> Los usuarios se registran siempre con `ROLE_USER`. Para promover a `ROLE_ADMIN` hay que modificar el campo `role` directamente en la base de datos.

---

### Nuevos endpoints de autenticación

#### POST `/api/v1/auth/register` — Registrar usuario

Crea un nuevo usuario con rol `ROLE_USER`. La contraseña se almacena encriptada con **BCrypt**.

```json
// Body (JSON)
{
  "username": "juan",
  "password": "miPassword123"
}
```

Respuestas:
- `201 Created` → `"Usuario registrado exitosamente"`
- `409 Conflict` → `"El usuario ya existe"`

---

#### POST `/api/v1/auth/login` — Iniciar sesión

Autentica las credenciales y devuelve un JWT válido por **24 horas**.

```json
// Body (JSON)
{
  "username": "juan",
  "password": "miPassword123"
}
```

```json
// Respuesta 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuIiwicm9sZSI6..."
}
```

Respuestas:
- `200 OK` → `{ "token": "..." }`
- `401 Unauthorized` → credenciales incorrectas

---

### ¿Cómo usar el token en Postman?

1. Hacer `POST /api/v1/auth/login` y copiar el valor del campo `token`.
2. En cada request protegido, agregar el header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOi...
```

En Postman: pestaña **Authorization** → tipo **Bearer Token** → pegar el token.

---

### Arquitectura de la implementación

#### Nuevos archivos creados

```text
src/main/java/com/example/bibliotecaduoc/
├── model/
│   └── Usuario.java                  ← entidad JPA con username, password, role
├── repository/
│   └── UsuarioRepository.java        ← findByUsername()
├── dto/
│   ├── AuthRequest.java              ← { username, password }
│   └── AuthResponse.java             ← { token }
├── security/
│   ├── JwtUtil.java                  ← genera, parsea y valida tokens JWT
│   ├── JwtFilter.java                ← filtro que valida el JWT en cada request
│   ├── UserDetailsServiceImpl.java   ← carga el usuario desde la BD
│   └── SecurityConfig.java           ← reglas de acceso y configuración general
└── controller/
    └── AuthController.java           ← endpoints /register y /login
```

---

#### Flujo de autenticación (login)

```
Cliente
  │  POST /api/v1/auth/login  { username, password }
  ▼
AuthController.login()
  │  authenticationManager.authenticate(...)
  ▼
UserDetailsServiceImpl.loadUserByUsername()
  │  SELECT * FROM usuarios WHERE username = ?
  ▼
Spring compara password recibido con el hash BCrypt guardado en BD
  │  ¿Coinciden?
  ├─ NO → BadCredentialsException → 401 Unauthorized
  └─ SÍ → JwtUtil.generateToken(username, role)
              │  firma el token con HMAC-SHA256
              ▼
           { "token": "eyJ..." }  → 200 OK
```

---

#### Flujo de autorización (request protegido)

```
Cliente
  │  GET /api/v1/libros
  │  Authorization: Bearer eyJ...
  ▼
JwtFilter (intercepta antes que el controlador)
  │  ¿El header Authorization existe y empieza por "Bearer "?
  ├─ NO  → sigue sin autenticación → Spring rechaza con 403
  └─ SÍ  → JwtUtil.validateToken(token)
              │  ¿La firma es válida y no venció?
              ├─ NO  → 403 Forbidden
              └─ SÍ  → extrae username y role → registro en SecurityContext
                            │
                            ▼
                    SecurityConfig evalúa reglas
                    ¿GET /api/v1/**? → hasAnyRole("USER","ADMIN") → ✓
                            │
                            ▼
                    LibroController.listarLibros()
                            │
                            ▼
                    200 OK + [ { ... }, { ... } ]
```

---

### Descripción de los nuevos componentes

#### `JwtUtil`
Componente `@Component` que encapsula toda la lógica de JWT usando la librería **JJWT 0.12.6**:
- `generateToken(username, role)` → construye y firma el JWT.
- `extractUsername(token)` → lee el `subject` del payload.
- `extractRole(token)` → lee el claim `role` del payload.
- `validateToken(token)` → verifica firma y vigencia; retorna `boolean`.

#### `JwtFilter`
Extiende `OncePerRequestFilter`, lo que garantiza que se ejecuta exactamente **una vez por request**. Lee el header `Authorization`, delega la validación a `JwtUtil` y registra la autenticación en el `SecurityContextHolder`.

#### `UserDetailsServiceImpl`
Implementa la interfaz `UserDetailsService` de Spring Security. Spring la detecta automáticamente y la usa durante `authenticationManager.authenticate()` para cargar el usuario desde la base de datos y comparar la contraseña con BCrypt.

#### `SecurityConfig`
Clase `@Configuration` que define el bean `SecurityFilterChain`:
- **CSRF deshabilitado**: no es necesario en APIs REST stateless.
- **Sesión stateless**: `SessionCreationPolicy.STATELESS`, sin cookies de sesión.
- **Reglas de acceso**: define qué rol puede usar cada método HTTP.
- **JwtFilter agregado**: se registra antes del `UsernamePasswordAuthenticationFilter` de Spring.
- **`PasswordEncoder`**: bean `BCryptPasswordEncoder` para encriptar contraseñas.
- **`AuthenticationManager`**: expuesto como bean para usarlo en `AuthController`.

---

### Nuevas anotaciones del proyecto

| Anotación | Descripción |
|---|---|
| `@EnableWebSecurity` | Activa la configuración de seguridad web de Spring |
| `@Configuration` | Indica que la clase declara beans de Spring |
| `@Bean` | Declara un método que produce un objeto gestionado por Spring |
| `@Component` | Marca `JwtUtil` y `JwtFilter` como beans gestionados |

---

### Nuevas dependencias (pom.xml)

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT (JJWT 0.12.6) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

---

### Propiedad agregada en `application.properties`

```properties
# Clave secreta para firmar los tokens JWT (mínimo 32 caracteres)
# IMPORTANTE: cambiar por un valor seguro y aleatorio en producción
jwt.secret=bibliotecaduoc-clave-secreta-jwt-2026-cambiar-en-produccion
```

---

## 14) Swagger / OpenAPI

Swagger UI permite visualizar y probar la API desde el navegador.

### Dependencia necesaria en `pom.xml`

```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.8.9</version>
</dependency>
```

### Qué hay que permitir en Security

Para que Swagger se pueda abrir sin JWT, hay que dejar públicas estas rutas en `SecurityConfig`:

```java
.requestMatchers(
  "/swagger-ui/**",
  "/swagger-ui.html",
  "/v3/api-docs/**"
).permitAll()
```

Eso permite entrar a la documentación sin autenticarse primero.

### URL de acceso

```text
http://localhost:8080/swagger-ui/index.html
```

### Qué aporta Swagger en este proyecto

1. Lista automáticamente los endpoints disponibles.
2. Permite probar requests desde el navegador.
3. Muestra los modelos y códigos HTTP.
4. Si el endpoint devuelve HATEOAS, también se ve el JSON con sus `_links` generados automáticamente.

### Nota sobre HATEOAS y Swagger en este proyecto

Se agregó además esta propiedad en `application.properties`:

```properties
springdoc.enable-hateoas=false
```

Se usa para evitar un conflicto de compatibilidad entre Springdoc y HATEOAS en la combinación actual de dependencias, manteniendo:

1. Swagger funcionando.
2. Los endpoints HATEOAS funcionando.

## 15) Pruebas unitarias: guía paso a paso

En este proyecto se dejó un test didáctico principal para explicar la base de JUnit + Mockito:

- `src/test/java/com/example/bibliotecaduoc/controller/LibroControllerTest.java`

### Objetivo de este test

Comprobar que el método del controlador que crea libros:

- responde con HTTP `201 CREATED`
- devuelve un cuerpo no nulo
- devuelve el libro esperado en el body

### Estructura que debes enseñar (Arrange - Act - Assert)

#### 1. Arrange (preparación)

En esta fase se prepara todo lo necesario:

- datos de entrada (por ejemplo `Autor` y `Libro`)
- comportamiento simulado del servicio usando Mockito

Ejemplo conceptual:

```java
Autor autor = new Autor(...);
Libro libro = new Libro(...);
when(libroService.saveLibro(libro)).thenReturn(libro);
```

Qué significa ese `when(...).thenReturn(...)`:

- `libroService` es un mock, no el servicio real
- no se llama base de datos
- cuando el controlador invoque `saveLibro`, Mockito devolverá el libro simulado

#### 2. Act (acción)

Se ejecuta exactamente el método bajo prueba:

```java
var respuesta = libroController.agregarLibro(libro);
```

Aquí `respuesta` es un `ResponseEntity<Libro>`.

#### 3. Assert (verificación)

Se valida el resultado observable:

- `assertNotNull(respuesta)`
- `assertEquals(HttpStatus.CREATED, respuesta.getStatusCode())`
- `assertNotNull(respuesta.getBody())`
- `assertEquals("Cien años de soledad", respuesta.getBody().getTitulo())`

### Ejecución de pruebas

Ejecutar solo el test principal (recomendado para clase):

```bash
.\mvnw.cmd -Dtest=LibroControllerTest test
```

Ejecutar todas las pruebas existentes en el proyecto:

```bash
.\mvnw.cmd test
```

### Importante para alumnos: qué valida y qué no valida este test

Como usa mock del servicio, este test valida la lógica del controlador, no la persistencia real en MySQL.

Para validar integración real (controller + service + DB), debes usar tests de integración (`@SpringBootTest`, Testcontainers, etc.).

---

## 16) Docker: guía para principiantes

### ¿Qué es Docker?

Docker es una herramienta para ejecutar programas en contenedores.
Un contenedor es un entorno aislado que trae todo lo necesario para correr la app.

En palabras simples: en vez de instalar Java, MySQL y configuraciones manuales en cada PC, Docker lo levanta igual para todos.

### ¿Qué se agregó en este proyecto?

- `Dockerfile`: define cómo construir y ejecutar la API Spring Boot.
- `docker-compose.yml`: define y levanta dos servicios juntos:
  - `mysql` (base de datos)
  - `app` (tu API)
- `.dockerignore`: evita copiar archivos innecesarios al build.

### Requisitos previos (una sola vez)

1. Instalar Docker Desktop.
2. Abrir Docker Desktop y esperar que diga "Engine running".
3. Verificar instalación:

```bash
docker --version
docker compose version
```

### Explicación de `docker-compose.yml`

Servicio `mysql`:

- imagen: `mysql:8.4`
- crea base `bibliotecaduoc`
- contraseña root configurable por `.env` (`MYSQL_ROOT_PASSWORD`)
- mapea `${MYSQL_HOST_PORT}` (host) -> `${MYSQL_CONTAINER_PORT}` (contenedor)
- guarda datos en volumen `mysql_data`
- healthcheck para que la app espere a que MySQL esté listo

Servicio `app`:

- se construye desde el `Dockerfile`
- depende de `mysql` saludable
- expone `${APP_HOST_PORT}:8080`
- recibe variables de entorno para Spring (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, etc.)

### Variables de `.env` para puertos

```text
MYSQL_HOST_PORT=3307
MYSQL_CONTAINER_PORT=3306
APP_HOST_PORT=8080
```

Con esto puedes cambiar puertos sin editar el `docker-compose.yml`.

### Flujo recomendado para alumnos

#### Paso 1: levantar todo

```bash
docker compose up --build
```

Qué hace este comando:

- construye imagen de la app
- descarga imagen de MySQL si no existe
- crea y ejecuta ambos contenedores
- muestra logs en pantalla

#### Paso 2: probar que funciona

- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui/index.html`
- MySQL desde host: puerto `${MYSQL_HOST_PORT}` (por defecto `3307`)

#### Paso 3: detener

```bash
docker compose down
```

#### Paso 4 (opcional): borrar también los datos de la base

```bash
docker compose down -v
```

Usa esto solo si quieres resetear completamente la BD.

### Comandos útiles de diagnóstico

Ver contenedores activos:

```bash
docker compose ps
```

Ver logs de la app:

```bash
docker compose logs app
```

Ver logs de MySQL:

```bash
docker compose logs mysql
```

### Errores comunes en clase y solución rápida

1. "docker no se reconoce"
   Instalar Docker Desktop y reiniciar terminal.

2. Puerto 8080 ocupado
  Cambiar `APP_HOST_PORT` en `.env` (ej. `8081`).

3. Puerto 3307 ocupado
  Cambiar `MYSQL_HOST_PORT` en `.env` (ej. `3308`).

4. La app arranca antes que MySQL
   Ya está mitigado con `healthcheck` + `depends_on`.

---

## 17) Despliegue en Railway (GitHub + MySQL)

Guía rápida para publicar esta API en https://railway.com:

1. En Railway, crea un proyecto nuevo.
2. Elige la opción de desplegar desde GitHub.
3. Conecta tu cuenta GitHub y selecciona el repositorio Spring Boot.
4. Railway detectará el proyecto y hará el primer deploy.
5. Agrega un servicio de base de datos MySQL dentro del mismo proyecto.
6. Desde MySQL, obtén las credenciales (host, puerto, database, username, password).
7. Vuelve al servicio de la app y define estas variables de entorno:
   - `DB_URL=jdbc:mysql://HOST:PUERTO/BASE?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
   - `DB_USERNAME=TU_USUARIO_MYSQL`
   - `DB_PASSWORD=TU_PASSWORD_MYSQL`
   - `JWT_SECRET=CLAVE_LARGA_Y_SEGURA_MIN_32_CARACTERES`
   - `OPENMETEO_BASE_URL=https://api.open-meteo.com`
8. Haz redeploy de la app para que tome los cambios de variables.
9. En `Settings > Domains`, genera un dominio público para la app.

Si todo sale bien:

- Railway mostrará el despliegue en estado exitoso.
- En GitHub, el commit asociado al deploy aparecerá con tick verde.
- La base URL de tus endpoints quedará así:
  - `https://TU-DOMINIO-RAILWAY`

---

## 18) Autor

- **Alvaro Maurelia**
- **Correo:** al.maurelia@profesor.duoc.cl
