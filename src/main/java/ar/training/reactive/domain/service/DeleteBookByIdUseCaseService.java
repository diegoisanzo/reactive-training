package ar.training.reactive.domain.service;

import ar.training.reactive.domain.exception.BookNotFoundException;
import ar.training.reactive.domain.port.out.BookRepositoryOutputPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class DeleteBookByIdUseCaseService {

    private final BookRepositoryOutputPort bookRepository;

    public DeleteBookByIdUseCaseService(BookRepositoryOutputPort bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Mono<Void> deleteById(UUID id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new BookNotFoundException(id)))
                .flatMap(bookRepository::delete);
    }
}
