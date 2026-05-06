package ar.training.reactive.application.usecase;

import ar.training.reactive.application.port.in.DeleteBookByIdInboundPort;
import ar.training.reactive.application.port.out.BookRepositoryOutboundPort;
import ar.training.reactive.domain.exception.BookNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class DeleteBookByIdUseCase implements DeleteBookByIdInboundPort {

    private final BookRepositoryOutboundPort bookRepository;

    public DeleteBookByIdUseCase(BookRepositoryOutboundPort bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Mono<Void> deleteById(UUID id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new BookNotFoundException(id)))
                .flatMap(bookRepository::delete);
    }
}
