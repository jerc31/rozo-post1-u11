

## Objetivo

Este proyecto refactoriza una API REST de catalogo de productos aplicando buenas
practicas de arquitectura en capas. La solucion separa responsabilidades con
SOLID, usa Repository como DAO, DTOs para entrada y salida, un Factory para
convertir objetos y un `@RestControllerAdvice` para centralizar errores.

## Tecnologias

- Java 17
- Spring Boot 3.2.5
- Maven
- Spring Web
- Spring Data JPA
- H2 Database
- Bean Validation

## Arquitectura implementada

```text
src/main/java/com/empresa/catalogo/
|-- controller/
|   `-- ProductoController.java
|-- service/
|   |-- ProductoService.java
|   `-- ProductoServiceImpl.java
|-- repository/
|   `-- ProductoRepository.java
|-- dto/
|   |-- ProductoRequestDTO.java
|   `-- ProductoResponseDTO.java
|-- entity/
|   `-- Producto.java
|-- factory/
|   `-- ProductoFactory.java
`-- exception/
    |-- ApiError.java
    |-- GlobalExceptionHandler.java
    `-- RecursoNoEncontradoException.java
```
# Diagrama de arquitectura en capas

```text
                    CLIENTE / POSTMAN / CURL
                               │
                               ▼
                    ProductoController
                               │
                               ▼
                 ProductoService (Interfaz)
                               │
                               ▼
                    ProductoServiceImpl
                      │                │
                      │                │
                      ▼                ▼
            ProductoRepository     ProductoFactory
                  (DAO)                  │
                      │                  │
                      ▼                  ▼
                 Base de Datos      DTOs / Entity
                                        │
                                        ▼
                         ProductoRequestDTO
                         ProductoResponseDTO

GlobalExceptionHandler captura excepciones y retorna ApiError.
```

---

# Principios y patrones implementados

## SRP — Single Responsibility Principle

Cada clase tiene una única responsabilidad:

- `ProductoController`: manejo de endpoints REST.
- `ProductoServiceImpl`: lógica de negocio.
- `ProductoRepository`: acceso a datos.
- `ProductoFactory`: conversión Entity ↔ DTO.
- `GlobalExceptionHandler`: manejo global de errores.

---

## DIP — Dependency Inversion Principle

El controlador depende de la abstracción `ProductoService`
y no de la implementación concreta.

```java
private final ProductoService service;
```

---

## DAO — Data Access Object

Se implementa mediante `ProductoRepository`
extendiendo `JpaRepository`.

```java
public interface ProductoRepository extends JpaRepository<Producto, Long>
```

---

## DTO — Data Transfer Object

### DTO de entrada

`ProductoRequestDTO`

- Valida datos enviados por el cliente.
- Usa:
  - `@NotBlank`
  - `@Positive`

### DTO de salida

`ProductoResponseDTO`

- Expone únicamente información necesaria.
- Evita retornar datos internos innecesarios.

---

## Factory Pattern

`ProductoFactory` centraliza las conversiones:

- DTO → Entity
- Entity → DTO

Esto evita duplicar lógica de transformación.

---

## Manejo global de excepciones

`GlobalExceptionHandler` utiliza:

```java
@RestControllerAdvice
```

Capturando:

| Excepción | Status |
| --- | --- |
| RecursoNoEncontradoException | 404 |
| MethodArgumentNotValidException | 400 |
| Exception | 500 |

---

# Entidad Producto

```java
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @Positive
    private Double precio;

    private String categoria;

    private boolean activo = true;
}
```

---

# Ejecución del proyecto

## Clonar repositorio

```powershell
git clone https://github.com/kevinjavierramirez55-tech/ramirez-post1-u11.git
```

---

## Entrar al proyecto

```powershell
cd ramirez-post1-u11
```

---

## Compilar proyecto

```powershell
.\mvnw.cmd compile
```

Resultado esperado:

```text
BUILD SUCCESS
```

---

## Ejecutar aplicación

```powershell
.\mvnw.cmd spring-boot:run
```

La API quedará disponible en:

```text
http://localhost:8080/api/productos
```

---

# Endpoints disponibles

| Método | Endpoint | Descripción |
| --- | --- | --- |
| GET | `/api/productos` | Listar productos activos |
| GET | `/api/productos/{id}` | Buscar producto por id |
| POST | `/api/productos` | Crear producto |
| DELETE | `/api/productos/{id}` | Eliminar producto |

---

# Pruebas con curl

## GET — listar productos

```powershell
curl.exe -i http://localhost:8080/api/productos
```

Respuesta esperada inicial:

```json
[]
```

---

## POST — crear producto

```powershell
curl.exe -i -X POST http://localhost:8080/api/productos ^
-H "Content-Type: application/json" ^
-d "{\"nombre\":\"Laptop\",\"precio\":3500000,\"categoria\":\"ELECTRONICA\"}"
```

Respuesta esperada:

```json
{
  "id": 1,
  "nombre": "Laptop",
  "precio": 3500000.0,
  "categoria": "ELECTRONICA"
}
```

---

## GET — producto inexistente

```powershell
curl.exe -i http://localhost:8080/api/productos/999
```

Respuesta esperada:

```json
{
  "status": 404,
  "error": "Not Found",
  "mensaje": "Producto con id 999 no encontrado.",
  "timestamp": "...",
  "path": "/api/productos/999"
}
```

---

## POST — validación de datos

```powershell
curl.exe -i -X POST http://localhost:8080/api/productos ^
-H "Content-Type: application/json" ^
-d "{}"
```

Respuesta esperada:

```json
{
  "status": 400,
  "error": "Bad Request",
  "mensaje": "nombre: El nombre es obligatorio; precio: El precio debe ser mayor a cero",
  "timestamp": "...",
  "path": "/api/productos"
}
```

---

# Checkpoints y evidencias

## Checkpoint 1 — Entidad, DTOs y Factory

### Requisito

- Proyecto compila correctamente.
- DTOs creados.
- Factory implementado.
- Validaciones:
  - `@NotBlank`
  - `@Positive`

---

## Checkpoint 2 — Service con DIP y Repository

### Requisito

- Aplicación inicia correctamente.
- `GET /api/productos` retorna lista vacía.
- `POST /api/productos` retorna DTO con id generado.

## Checkpoint 3 — GlobalExceptionHandler

### Requisito

- Manejo correcto de errores 404 y 400.
- Respuestas JSON estandarizadas mediante `ApiError`.

---

## Capturas del Proyecto

Las capturas se encuentran en la carpeta `evidencias/`.

### App compila sin errores

![app_corriendo](evidencias/app_corriendo.png)

### Post retorna 201

![post_201](evidencias/post_201.png)

### Get retorna error 404

![error_404](evidencias/error_404.png)


### Post retorna error 400

![error_400](evidencias/error_400.png)