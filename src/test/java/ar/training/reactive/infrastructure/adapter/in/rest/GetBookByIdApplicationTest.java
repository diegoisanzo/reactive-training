package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.fixture.BookDtoFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static ar.training.reactive.infrastructure.adapter.in.rest.BookController.BOOK_BY_ID_PATH;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class GetBookByIdApplicationTest {

    private final WebTestClient webTestClient;
    private final TestDataSetup testDataSetup;

    @Autowired
    GetBookByIdApplicationTest(
            WebTestClient webTestClient,
            TestDataSetup testDataSetup) {
        this.webTestClient = webTestClient;
        this.testDataSetup = testDataSetup;
    }

    @BeforeEach
    void beforeEach() {
        testDataSetup.refresh();
    }

    @Test
    void shouldReturnBookById() {
        var expected = BookDtoFixture.withDefaults();
        webTestClient.get()
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
        var bookById = new URI(BOOK_BY_ID_PATH
            .replace("{id}", id.toString()));
        expectedProblemDetail.setInstance(bookById);

        webTestClient.get()
                .uri(BOOK_BY_ID_PATH, id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .isEqualTo(expectedProblemDetail);
    }
}
