package ar.training.reactive.infrastructure.adapter.out.persistence.author;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface R2dbcAuthorRepository extends R2dbcRepository<AuthorEntity, UUID> {
}
