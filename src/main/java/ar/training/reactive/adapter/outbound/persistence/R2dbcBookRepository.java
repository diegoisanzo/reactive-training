package ar.training.reactive.adapter.outbound.persistence;

import ar.training.reactive.domain.model.Book;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface R2dbcBookRepository extends R2dbcRepository<Book, UUID> {
}
