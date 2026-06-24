package ar.training.reactive.application.usecase.book;

import ar.training.reactive.application.port.in.book.GetAllBooksInboundPort;
import ar.training.reactive.application.port.out.book.BookRepositoryOutboundPort;
import ar.training.reactive.domain.model.Book;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

public class GetAllBooksUseCase implements GetAllBooksInboundPort {
    private final BookRepositoryOutboundPort bookRepository;

    public GetAllBooksUseCase(BookRepositoryOutboundPort bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Flux<Book> getAllBooks() {
        return bookRepository.findAll();
    }
}
