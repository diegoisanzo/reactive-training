package ar.training.reactive.infrastructure.config;

import ar.training.reactive.application.port.out.book.BookRepositoryOutboundPort;
import ar.training.reactive.application.usecase.book.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookUseCaseConfig {

    // ==========================================
    // BOOKS
    // ==========================================

    @Bean
    public CreateBookUseCase createBookUseCase(BookRepositoryOutboundPort bookRepositoryPort) {
        return new CreateBookUseCase(bookRepositoryPort);
    }

    @Bean
    public DeleteBookByIdUseCase deleteBookByIdUseCase(BookRepositoryOutboundPort bookRepositoryPort) {
        return new DeleteBookByIdUseCase(bookRepositoryPort);
    }

    @Bean
    public GetAllBooksUseCase getAllBooksUseCase(BookRepositoryOutboundPort bookRepositoryPort) {
        return new GetAllBooksUseCase(bookRepositoryPort);
    }

    @Bean
    public GetBookByIdUseCase getBookByIdUseCase(BookRepositoryOutboundPort bookRepositoryPort) {
        return new GetBookByIdUseCase(bookRepositoryPort);
    }

    @Bean
    public UpdateBookUseCase updateBookUseCase(BookRepositoryOutboundPort bookRepositoryPort) {
        return new UpdateBookUseCase(bookRepositoryPort);
    }
}