package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.application.port.in.CreateBookInboundPort;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.domain.model.Genre;
import ar.training.reactive.fixture.CreateBookDtoFixture;
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

import static ar.training.reactive.infrastructure.adapter.in.rest.BookController.BOOK_PATH;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class CreateBookApplicationTest extends BaseApplicationTest {

    @MockitoSpyBean
    private CreateBookInboundPort createBookInboundPort;

    @Autowired
    CreateBookApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup, JwtService jwtService) {
        super(webTestClient, testDataSetup, jwtService);
    }

    @Test
    void shouldCreateBook() {
        var createBookDto = CreateBookDtoFixture.withDefaults();
        authedReadWriteUserClient().post()
                .uri(BOOK_PATH)
                .bodyValue(createBookDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .value(newBookDto -> {
                    assertNotNull(newBookDto);
                    assertNotNull(newBookDto.id());
                    assertEquals(createBookDto.isbn(), newBookDto.isbn());
                    assertEquals(createBookDto.title(), newBookDto.title());
                });
    }

    @Test
    void shouldReturn400WhenCreatingBookWithNullIsbn() {
        var invalidDto = new CreateBookDto(null, "Valid Title", 0, Genre.FICTION);
        authedReadWriteUserClient().post()
                .uri(BOOK_PATH)
                .bodyValue(invalidDto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .consumeWith(result -> {
                    var detail = result.getResponseBody();
                    assertNotNull(detail);
                    assertEquals(400, detail.getStatus());
                });
    }

    @Test
    void shouldReturn400WhenCreatingBookWithEmptyIsbn() {
        var invalidDto = new CreateBookDto("", "Valid Title", 0, Genre.FICTION);
        authedReadWriteUserClient().post().uri(BOOK_PATH).bodyValue(invalidDto).exchange()
                .expectStatus().isBadRequest().expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenCreatingBookWithIsbnExceedingMaxLength() {
        var invalidDto = new CreateBookDto("12345678901234", "Valid Title", 0, Genre.FICTION);
        authedReadWriteUserClient().post().uri(BOOK_PATH).bodyValue(invalidDto).exchange()
                .expectStatus().isBadRequest().expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenCreatingBookWithNullTitle() {
        var invalidDto = new CreateBookDto("9780000000000", null, 0, Genre.FICTION);
        authedReadWriteUserClient().post().uri(BOOK_PATH).bodyValue(invalidDto).exchange()
                .expectStatus().isBadRequest().expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenCreatingBookWithEmptyTitle() {
        var invalidDto = new CreateBookDto("9780000000000", "", 0, Genre.FICTION);
        authedReadWriteUserClient().post().uri(BOOK_PATH).bodyValue(invalidDto).exchange()
                .expectStatus().isBadRequest().expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenCreatingBookWithTitleExceedingMaxLength() {
        var longTitle = "a".repeat(256);
        var invalidDto = new CreateBookDto("9780000000000", longTitle, 0, Genre.FICTION);
        authedReadWriteUserClient().post().uri(BOOK_PATH).bodyValue(invalidDto).exchange()
                .expectStatus().isBadRequest().expectBody(ProblemDetail.class);
    }

    @Test
    void shouldFailToCreateBookWhenExceedingTimeLimit() {
        doAnswer(invocation -> Mono
                .delay(Duration.of(2200, MILLIS))
                .then(Mono.just(invocation.getArgument(0)))
        )
            .when(createBookInboundPort)
            .createBook(any(Book.class));
        var createBookDto = CreateBookDtoFixture.withDefaults();
        authedReadWriteUserClient().post()
                .uri(BOOK_PATH)
                .bodyValue(createBookDto)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ProblemDetail.class)
                .value(pd -> assertInternalServerError(pd, "/v1/books"));
    }

    @Test
    void shouldReturn409WhenBookAlreadyExists() {
        var existingBookId = java.util.UUID.randomUUID();
        doAnswer(invocation -> Mono.error(new ar.training.reactive.domain.exception.BookAlreadyExistsException(existingBookId)))
                .when(createBookInboundPort)
                .createBook(any(Book.class));
        var createBookDto = CreateBookDtoFixture.withDefaults();
        authedReadWriteUserClient().post()
                .uri(BOOK_PATH)
                .bodyValue(createBookDto)
                .exchange()
                .expectStatus().isEqualTo(org.springframework.http.HttpStatus.CONFLICT)
                .expectBody(ProblemDetail.class)
                .value(problemDetail -> {
                    assertNotNull(problemDetail);
                    assertEquals("Book already exists", problemDetail.getTitle());
                    assertEquals(409, problemDetail.getStatus());
                    assertEquals("Book with id " + existingBookId + " already exists", problemDetail.getDetail());
                });
    }
}
