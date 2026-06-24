package ar.training.reactive.infrastructure.adapter.in.rest.book;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.application.port.in.book.GetBookByIdInboundPort;
import ar.training.reactive.fixture.book.BookDtoFixture;
import ar.training.reactive.fixture.book.BookFixture;
import ar.training.reactive.infrastructure.adapter.in.rest.BaseApplicationTest;
import ar.training.reactive.infrastructure.adapter.in.rest.BookDto;
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
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.UUID;

import static ar.training.reactive.infrastructure.adapter.in.rest.BookController.BOOK_BY_ID_PATH;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class GetBookByIdApplicationTest extends BaseApplicationTest {

    @MockitoSpyBean
    private GetBookByIdInboundPort getBookByIdInboundPort;

    @Autowired
    GetBookByIdApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup, JwtService jwtService) {
        super(webTestClient, testDataSetup, jwtService);
    }

    @Test
    void shouldReturnBookById() {
        var expected = BookDtoFixture.withDefaults();
        authedReadUserClient().get()
                .uri(BOOK_BY_ID_PATH, expected.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(expected);
    }

    @Test
    void shouldReturn404WhenBookNotFoundById() throws URISyntaxException {
        var id = new UUID(0, 0);
        var expectedProblemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, "Book with id %s not found".formatted(id));
        expectedProblemDetail.setTitle("Book not found");
        expectedProblemDetail.setInstance(new URI(BOOK_BY_ID_PATH.replace("{id}", id.toString())));

        authedReadUserClient().get()
                .uri(BOOK_BY_ID_PATH, id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .isEqualTo(expectedProblemDetail);
    }

    @Test
    void shouldFailToGetBookByIdWhenExceedingTimeLimit() {
        var id = BookDtoFixture.withDefaults().id();
        doAnswer(_ -> Mono.delay(Duration.of(1200, MILLIS)).then(Mono.just(BookFixture.withDefaults())))
            .when(getBookByIdInboundPort)
            .getBookById(any(UUID.class));
        authedReadUserClient().get()
                .uri(BOOK_BY_ID_PATH, id)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ProblemDetail.class)
                .value(pd -> assertInternalServerError(pd, "/v1/books/" + id));
    }
}
