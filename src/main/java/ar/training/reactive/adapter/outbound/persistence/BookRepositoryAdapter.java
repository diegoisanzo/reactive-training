package ar.training.reactive.adapter.outbound.persistence;

import ar.training.reactive.domain.model.Book;
import ar.training.reactive.domain.port.BookRepositoryPort;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class BookRepositoryAdapter implements BookRepositoryPort {

    private final R2dbcBookRepository repository;

    public BookRepositoryAdapter(R2dbcBookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Book> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Flux<Book> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Book> save(Book book) {
        return repository.save(book);
    }

    @Override
    public Mono<Void> delete(Book book) {
        return repository.delete(book);
    }
}
