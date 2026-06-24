package ar.training.reactive.application.usecase.book;

import ar.training.reactive.application.port.in.book.DeleteBookByIdInboundPort;
import ar.training.reactive.application.port.out.book.BookRepositoryOutboundPort;
import ar.training.reactive.domain.exception.book.BookNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class DeleteBookByIdUseCase implements DeleteBookByIdInboundPort {

    private final BookRepositoryOutboundPort bookRepository;

    public DeleteBookByIdUseCase(BookRepositoryOutboundPort bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Mono<Void> deleteBookById(UUID id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new BookNotFoundException(id)))
                .flatMap(bookRepository::delete);
    }
}
