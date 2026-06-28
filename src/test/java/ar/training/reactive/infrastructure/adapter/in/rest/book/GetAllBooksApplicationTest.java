package ar.training.reactive.infrastructure.adapter.in.rest.book;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.application.port.in.book.GetAllBooksInboundPort;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.fixture.book.BookDtoFixture;
import ar.training.reactive.infrastructure.adapter.in.rest.BaseApplicationTest;
import ar.training.reactive.infrastructure.adapter.in.rest.TestDataSetup;
import ar.training.reactive.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static ar.training.reactive.infrastructure.adapter.in.rest.book.BookController.BOOK_PATH;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class GetAllBooksApplicationTest extends BaseApplicationTest {

    @MockitoSpyBean
    private GetAllBooksInboundPort getAllBooksInboundPort;

    @Autowired
    GetAllBooksApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup, JwtService jwtService) {
        super(webTestClient, testDataSetup, jwtService);
    }

    @Test
    void shouldReturnAllBooks() {
        authedReadUserClient().get()
                .uri(BOOK_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookDto.class)
                .hasSize(3)
                .contains(BookDtoFixture.withDefaults());
    }

    @Test
    void shouldFailToGetAllBooksWhenExceedingTimeLimit() {
        doAnswer(_ -> Mono.delay(Duration.of(3200, MILLIS)).thenMany(Flux.<Book>empty()))
            .when(getAllBooksInboundPort)
            .getAllBooks();
        authedReadUserClient().get()
                .uri(BOOK_PATH)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ProblemDetail.class)
                .value(pd -> assertInternalServerError(pd, "/v1/books"));
    }
}
