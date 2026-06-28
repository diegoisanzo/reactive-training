package ar.training.reactive.infrastructure.config;

import ar.training.reactive.application.port.out.author.AuthorRepositoryOutboundPort;
import ar.training.reactive.application.usecase.author.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthorUseCaseConfig {

    // ==========================================
    // AUTHORS
    // ==========================================

    @Bean
    public CreateAuthorUseCase createAuthorUseCase(AuthorRepositoryOutboundPort authorRepositoryPort) {
        return new CreateAuthorUseCase(authorRepositoryPort);
    }

    @Bean
    public DeleteAuthorByIdUseCase deleteAuthorByIdUseCase(AuthorRepositoryOutboundPort authorRepositoryPort) {
        return new DeleteAuthorByIdUseCase(authorRepositoryPort);
    }

    @Bean
    public GetAllAuthorsUseCase getAllAuthorsUseCase(AuthorRepositoryOutboundPort authorRepositoryPort) {
        return new GetAllAuthorsUseCase(authorRepositoryPort);
    }

    @Bean
    public GetAuthorByIdUseCase getAuthorByIdUseCase(AuthorRepositoryOutboundPort authorRepositoryPort) {
        return new GetAuthorByIdUseCase(authorRepositoryPort);
    }

    @Bean
    public UpdateAuthorUseCase updateAuthorUseCase(AuthorRepositoryOutboundPort authorRepositoryPort) {
        return new UpdateAuthorUseCase(authorRepositoryPort);
    }

}
