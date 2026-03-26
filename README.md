# BibliotecaDuoc - API REST básica con Spring Boot

Proyecto académico de ejemplo para aprender arquitectura por capas con Spring Boot:

- `controller` (capa web / endpoints REST)
- `service` (lógica de negocio)
- `repository` (acceso a datos, en este caso en memoria)
- `model` (estructura de datos)

---

## 1) Requisitos

- Java 17
- Maven (opcional si usas `mvnw`)
- IDE recomendado: VS Code / IntelliJ / Eclipse
- Postman (opcional para probar la API)

> Este proyecto usa **Spring Boot** y **Lombok**.

---

## 2) ¿Cómo ejecutar el proyecto?

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

## 3) URL base de la API

Por defecto Spring Boot levanta en puerto `8080`:

```text
http://localhost:8080
```

Base path del controlador:

```text
/api/v1/libros
```

---

## 4) Endpoints disponibles

### 4.1 Listar libros
- **Método:** `GET`
- **URL:** `/api/v1/libros`
- **Descripción:** retorna todos los libros almacenados en memoria.

### 4.2 Buscar libro por ID
- **Método:** `GET`
- **URL:** `/api/v1/libros/{id}`
- **Descripción:** retorna un libro por su id.

### 4.3 Crear libro
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

### 4.4 Actualizar libro
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

### 4.5 Eliminar libro
- **Método:** `DELETE`
- **URL:** `/api/v1/libros/{id}`
- **Descripción:** elimina un libro por id.

---

## 5) Estructura del proyecto y explicación por capas

```text
src/main/java/com/example/bibliotecaduoc/
├── controller/
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

Responsabilidades:
- Simular persistencia de datos (usa `List<Libro>` en memoria).
- Implementar operaciones CRUD básicas: obtener, buscar, guardar, actualizar y eliminar.

Anotación clave:

- `@Repository`
	- Indica que esta clase pertenece a la capa de acceso a datos.
	- Spring la registra como bean del contenedor.

> Importante: aquí **no hay base de datos real**. Los datos se pierden al reiniciar la aplicación.

### 5.4 `model` (entidades / estructura de datos)

En esta carpeta está `Libro`, que representa los datos de un libro.

Campos actuales:
- `id`
- `isbn`
- `titulo`
- `editorial`
- `fechaPublicacion`
- `autor`

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

- `lombok`
	- Para reducir código repetitivo en modelos.

- `spring-boot-starter-webmvc-test`
	- Soporte de testing para capa web.

---

## 8) Autor

- **Alvaro Maurelia**
- **Correo:** al.maurelia@profesor.duoc.cl
