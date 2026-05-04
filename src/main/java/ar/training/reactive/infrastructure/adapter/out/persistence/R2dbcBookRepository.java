package ar.training.reactive.infrastructure.adapter.out.persistence;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface R2dbcBookRepository extends R2dbcRepository<BookEntity, UUID> {
}
