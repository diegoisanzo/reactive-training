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

## Resilience

Resilience patterns are applied at the controller layer via Resilience4j annotations. Each endpoint has its own named instance, configured independently in `application.yaml`.

### Time Limiter

`@TimeLimiter` enforces a per-endpoint timeout on the reactive pipeline. If the operation exceeds the configured duration, the request fails with HTTP 500 (the default Spring WebFlux error response for a timeout signal propagated as an unhandled exception).

| HTTP Verb | Endpoint         | Timeout |
|-----------|------------------|---------|
| POST      | `/v1/books`      | 2s      |
| GET       | `/v1/books`      | 3s      |
| GET       | `/v1/books/{id}` | 1s      |
| PUT       | `/v1/books`      | 2s      |
| DELETE    | `/v1/books/{id}` | 1s      |

### Rate Limiter

`@RateLimiter` limits the number of requests allowed per refresh period per endpoint. Excess requests are rejected immediately (`timeoutDuration: 0ms`) without queuing. `ExceptionsHandler` maps `RequestNotPermitted` → HTTP 429 with a `ProblemDetail` body (`title: "Rate limit exceeded"`).

All endpoints share the same defaults: **100 requests / 1s** window.

## Running

Requires PostgreSQL at `localhost:5432`, database `postgres`, user `postgres`, password `p`.

Start the database with Docker Compose:

```bash
docker compose up -d
```

Then run the application:

```bash
./gradlew bootRun
```

The table schema and seed data are created at startup by `DBConfigCommandLineRunner` (no Flyway/Liquibase).

To stop the database:

```bash
docker compose down        # keeps data
docker compose down -v     # also removes the volume
```

## Testing

```bash
# All tests
./gradlew test

# Single class
./gradlew test --tests "ar.training.reactive.infrastructure.adapter.in.rest.GetAllBooksApplicationTest"
```

Integration tests use Testcontainers (`postgres:16-alpine`). No mocks — all tests hit a real database.
