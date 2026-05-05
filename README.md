# reactive-training

Spring Boot 4 + WebFlux reactive REST API backed by PostgreSQL via R2DBC. Java 25.

## Stack

- Spring Boot 4 / WebFlux (non-blocking, reactive)
- R2DBC + PostgreSQL
- Testcontainers (integration tests)

## Architecture

The project follows **hexagonal architecture** (ports and adapters). The dependency rule is enforced strictly: outer layers depend on inner layers, never the reverse.

```
domain          — pure Java, no framework imports
application     — use cases and port interfaces
infrastructure  — Spring adapters (REST, persistence)
```

### Layers

**`domain/`**
The core. Contains the `Book` model and domain exceptions. No Spring, no R2DBC, no framework annotations.

**`application/`**
Use cases and port interfaces, organized vertically by operation:

```
application/
  port/
    in/   — inbound port interfaces (one per use case)
    out/  — outbound port interfaces (e.g. BookRepositoryOutboundPort)
  usecase/
    CreateBookUseCase
    GetAllBooksUseCase
    GetBookByIdUseCase
    UpdateBookUseCase
    DeleteBookByIdUseCase
```

Each inbound port (`CreateBookInboundPort`, `GetAllBooksInboundPort`, etc.) defines the contract for one use case. Each use case class implements exactly one inbound port. This follows the Interface Segregation Principle — callers depend only on what they actually use.

The outbound port (`BookRepositoryOutboundPort`) decouples use cases from the persistence mechanism.

> **Note on reactivity:** port interfaces and use cases return `Mono<T>` and `Flux<T>` (Reactor) directly rather than a framework-agnostic type. In a stricter hexagonal setup the application layer would be isolated from any specific reactive library, but that trade-off was accepted here in favour of simplicity — Reactor is pervasive across the whole stack and abstracting over it would add complexity with little practical benefit.

**`infrastructure/`**
Adapters that connect the application to the outside world:

```
infrastructure/
  adapter/
    in/rest/        — BookController, BookDto, CreateBookDto, ExceptionsHandler
    out/persistence/ — BookRepositoryOutboundAdapter, BookEntity, BookMapper,
                       R2dbcBookRepository, DBConfigCommandLineRunner
```

### Request flow

```
HTTP request
  → BookController          (inbound adapter)
  → *InboundPort            (inbound port interface)
  → *UseCase                (application logic)
  → BookRepositoryOutboundPort (outbound port interface)
  → BookRepositoryOutboundAdapter (outbound adapter)
  → R2DBC → PostgreSQL
```

The controller depends on inbound port interfaces, not concrete use case classes. Swapping an implementation requires no change to the controller.

## Running

Requires PostgreSQL at `localhost:5432`, database `postgres`, user `postgres`, password `p`.

```bash
./gradlew bootRun
```

The table schema and seed data are created at startup by `DBConfigCommandLineRunner` (no Flyway/Liquibase).

## Testing

```bash
# All tests
./gradlew test

# Single class
./gradlew test --tests "ar.training.reactive.infrastructure.adapter.in.rest.BookControllerApplicationTest"
```

Integration tests use Testcontainers (`postgres:16-alpine`). No mocks — all tests hit a real database.
