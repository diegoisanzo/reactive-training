package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.application.usecase.book.CreateBookUseCase;
import ar.training.reactive.application.usecase.book.DeleteBookByIdUseCase;
import ar.training.reactive.application.usecase.book.GetAllBooksUseCase;
import ar.training.reactive.application.usecase.book.GetBookByIdUseCase;
import ar.training.reactive.application.usecase.book.UpdateBookUseCase;
import ar.training.reactive.infrastructure.adapter.in.rest.book.BookController;
import ar.training.reactive.infrastructure.adapter.out.persistence.book.BookRepositoryOutboundAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
class ApplicationContextTest {

    private final ApplicationContext applicationContext;

    @Autowired
    ApplicationContextTest(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Test
    void contextLoads() {
        assertContainsBeanOfType(BookController.class);

        assertContainsBeanOfType(CreateBookUseCase.class);
        assertContainsBeanOfType(GetAllBooksUseCase.class);
        assertContainsBeanOfType(GetBookByIdUseCase.class);
        assertContainsBeanOfType(UpdateBookUseCase.class);
        assertContainsBeanOfType(DeleteBookByIdUseCase.class);

        assertContainsBeanOfType(BookRepositoryOutboundAdapter.class);
    }

    private void assertContainsBeanOfType(final Class<?> requiredType) {
        var bean = applicationContext.getBean(requiredType);
        assertThat(bean)
                .isNotNull()
                .isInstanceOf(requiredType);
    }
}
