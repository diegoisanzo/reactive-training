package ar.training.reactive.application;

import ar.training.reactive.adapter.inbound.rest.BookController;
import ar.training.reactive.adapter.outbound.persistence.BookRepositoryOutputAdapter;
import ar.training.reactive.domain.service.CreateBookUseCaseService;
import ar.training.reactive.domain.service.DeleteBookByIdUseCaseService;
import ar.training.reactive.domain.service.GetAllBooksUseCaseService;
import ar.training.reactive.domain.service.GetBookByIdUseCaseService;
import ar.training.reactive.domain.service.UpdateBookUseCaseService;
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

        assertContainsBeanOfType(CreateBookUseCaseService.class);
        assertContainsBeanOfType(GetAllBooksUseCaseService.class);
        assertContainsBeanOfType(GetBookByIdUseCaseService.class);
        assertContainsBeanOfType(UpdateBookUseCaseService.class);
        assertContainsBeanOfType(DeleteBookByIdUseCaseService.class);

        assertContainsBeanOfType(BookRepositoryOutputAdapter.class);
    }

    private void assertContainsBeanOfType(final Class<?> requiredType) {
        var bean = applicationContext.getBean(requiredType);
        assertThat(bean)
                .isNotNull()
                .isInstanceOf(requiredType);
    }
}
