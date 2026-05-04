package ar.training.reactive.application.usecase;

import ar.training.reactive.application.port.in.GetAllBooksInboundPort;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.application.port.out.BookRepositoryOutboundPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class GetAllBooksUseCase implements GetAllBooksInboundPort {
    private final BookRepositoryOutboundPort bookRepository;

    public GetAllBooksUseCase(BookRepositoryOutboundPort bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Flux<Book> getAllBooks() {
        return bookRepository.findAll();
    }
}
