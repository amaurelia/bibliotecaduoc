# BibliotecaDuoc - API REST con Spring Boot + JPA + MySQL

Proyecto académico de ejemplo para aprender arquitectura por capas con Spring Boot:

- `controller` (capa web / endpoints REST)
- `service` (lógica de negocio)
- `repository` (acceso a datos con **JPA / Hibernate**)
- `model` (entidades JPA / estructura de datos)

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
spring.datasource.url=jdbc:mysql://localhost:3306/bibliotecaduoc?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

- `createDatabaseIfNotExist=true` → crea la base de datos `bibliotecaduoc` si no existe.
- `ddl-auto=update` → Hibernate actualiza el esquema automáticamente según la entidad `Libro`.
- `show-sql=true` → muestra las consultas SQL generadas en la consola.

---

## 3) ¿Cómo ejecutar el proyecto?

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

## 6) Colección de Postman

Se incluye una colección lista para importar:

`postman/BibliotecaDuoc.postman_collection.json`

Incluye todos los endpoints:
- Listar libros
- Buscar libro por ID
- Crear libro
- Actualizar libro
- Eliminar libro

### Cómo importarla en Postman

1. Abrir Postman.
2. Clic en **Import**.
3. Seleccionar el archivo `postman/BibliotecaDuoc.postman_collection.json`.
4. Ejecutar requests sobre `http://localhost:8080`.

---

## 7) Dependencias principales (pom.xml)

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

---

## 8) JpaRepository: métodos usados en este proyecto

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

## 9) ResponseEntity: manejo de respuestas HTTP

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

## 10) Autor

- **Alvaro Maurelia**
- **Correo:** al.maurelia@profesor.duoc.cl
