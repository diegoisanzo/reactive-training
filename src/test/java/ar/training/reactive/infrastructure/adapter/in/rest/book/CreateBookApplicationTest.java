package ar.training.reactive.infrastructure.adapter.in.rest.book;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.application.port.in.book.CreateBookInboundPort;
import ar.training.reactive.domain.exception.book.BookAlreadyExistsException;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.domain.model.Genre;
import ar.training.reactive.fixture.book.CreateBookDtoFixture;
import ar.training.reactive.infrastructure.adapter.in.rest.BaseApplicationTest;
import ar.training.reactive.infrastructure.adapter.in.rest.TestDataSetup;
import ar.training.reactive.infrastructure.adapter.out.persistence.author.AuthorDBData;
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

import static ar.training.reactive.infrastructure.adapter.in.rest.book.BookController.BOOK_BY_ID_PATH;
import static ar.training.reactive.infrastructure.adapter.in.rest.book.BookController.BOOK_PATH;
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

    private static final UUID AUTHOR_ID = AuthorDBData.ALL.getFirst().getId();

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
                    assertEquals(createBookDto.availableCopies(), newBookDto.availableCopies());
                    assertEquals(createBookDto.genre(), newBookDto.genre());
                    assertEquals(createBookDto.authorId(), newBookDto.authorId());
                });
    }

    @Test
    void shouldPersistCreatedBookWhenFetchedAgain() {
        var createBookDto = CreateBookDtoFixture.withDefaults();
        var created = authedReadWriteUserClient().post()
                .uri(BOOK_PATH)
                .bodyValue(createBookDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(created);

        authedReadUserClient().get()
                .uri(BOOK_BY_ID_PATH, created.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(created);
    }

    @Test
    void shouldReturn400WhenCreatingBookWithNullIsbn() {
        var invalidDto = new CreateBookDto(null, "Valid Title", 0, Genre.FICTION, AUTHOR_ID);
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
        var invalidDto = new CreateBookDto("", "Valid Title", 0, Genre.FICTION, AUTHOR_ID);
        authedReadWriteUserClient().post().uri(BOOK_PATH).bodyValue(invalidDto).exchange()
                .expectStatus().isBadRequest().expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenCreatingBookWithIsbnExceedingMaxLength() {
        var invalidDto = new CreateBookDto("12345678901234", "Valid Title", 0, Genre.FICTION, AUTHOR_ID);
        authedReadWriteUserClient().post().uri(BOOK_PATH).bodyValue(invalidDto).exchange()
                .expectStatus().isBadRequest().expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenCreatingBookWithNullTitle() {
        var invalidDto = new CreateBookDto("9780000000000", null, 0, Genre.FICTION, AUTHOR_ID);
        authedReadWriteUserClient().post().uri(BOOK_PATH).bodyValue(invalidDto).exchange()
                .expectStatus().isBadRequest().expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenCreatingBookWithEmptyTitle() {
        var invalidDto = new CreateBookDto("9780000000000", "", 0, Genre.FICTION, AUTHOR_ID);
        authedReadWriteUserClient().post().uri(BOOK_PATH).bodyValue(invalidDto).exchange()
                .expectStatus().isBadRequest().expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenCreatingBookWithTitleExceedingMaxLength() {
        var longTitle = "a".repeat(256);
        var invalidDto = new CreateBookDto("9780000000000", longTitle, 0, Genre.FICTION, AUTHOR_ID);
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
        doAnswer(invocation -> Mono.error(new BookAlreadyExistsException(existingBookId)))
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
