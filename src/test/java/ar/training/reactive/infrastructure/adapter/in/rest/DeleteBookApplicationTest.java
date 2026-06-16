package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.application.port.in.DeleteBookByIdInboundPort;
import ar.training.reactive.fixture.BookDtoFixture;
import ar.training.reactive.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

import static ar.training.reactive.infrastructure.adapter.in.rest.BookController.BOOK_BY_ID_PATH;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class DeleteBookApplicationTest extends BaseApplicationTest {

    @MockitoSpyBean
    private DeleteBookByIdInboundPort deleteBookByIdInboundPort;

    @Autowired
    DeleteBookApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup, JwtService jwtService) {
        super(webTestClient, testDataSetup, jwtService);
    }

    @Test
    void shouldDeleteBook() {
        var id = BookDtoFixture.withDefaults().id();
        authedAdminUserClient().delete().uri(BOOK_BY_ID_PATH, id).exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentBook() {
        authedAdminUserClient().delete().uri(BOOK_BY_ID_PATH, UUID.randomUUID()).exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldFailToDeleteBookWhenExceedingTimeLimit() {
        var id = BookDtoFixture.withDefaults().id();
        doAnswer(_ -> Mono.delay(Duration.of(1200, MILLIS)).then(Mono.empty()))
            .when(deleteBookByIdInboundPort)
            .deleteBookById(any(UUID.class));
        authedAdminUserClient().delete()
                .uri(BOOK_BY_ID_PATH, id)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ProblemDetail.class)
                .value(pd -> assertInternalServerError(pd, "/v1/books/" + id));
    }
}
