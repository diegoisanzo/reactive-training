package ar.training.reactive.infrastructure.adapter.out.persistence;

import ar.training.reactive.application.port.out.BookRepositoryOutboundPort;
import ar.training.reactive.domain.model.Book;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class BookRepositoryOutboundAdapter implements BookRepositoryOutboundPort {

    private final R2dbcBookRepository repository;

    public BookRepositoryOutboundAdapter(R2dbcBookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Book> findById(UUID id) {
        return repository.findById(id)
                .map(BookMapper::toDomain);
    }

    @Override
    public Flux<Book> findAll() {
        return repository.findAll()
                .map(BookMapper::toDomain);
    }

    @Override
    public Mono<Book> save(Book book) {
        return repository.save(BookMapper.toEntity(book))
                .map(BookMapper::toDomain);
    }

    @Override
    public Mono<Void> delete(Book book) {
        return repository.delete(BookMapper.toEntity(book));
    }
}
