package ar.training.reactive.infrastructure.adapter.in.rest.book;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.application.port.in.book.UpdateBookInboundPort;
import ar.training.reactive.domain.model.Book;
import ar.training.reactive.domain.model.Genre;
import ar.training.reactive.fixture.book.BookDtoFixture;
import ar.training.reactive.infrastructure.adapter.in.rest.BaseApplicationTest;
import ar.training.reactive.infrastructure.adapter.in.rest.TestDataSetup;
import ar.training.reactive.infrastructure.adapter.out.persistence.author.AuthorDBData;
import ar.training.reactive.infrastructure.security.jwt.JwtService;
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

import static ar.training.reactive.infrastructure.adapter.in.rest.book.BookController.BOOK_PATH;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class UpdateBookApplicationTest extends BaseApplicationTest {

    private static final UUID AUTHOR_ID = AuthorDBData.ALL.getFirst().getId();

    @MockitoSpyBean
    private UpdateBookInboundPort updateBookInboundPort;

    @Autowired
    UpdateBookApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup, JwtService jwtService) {
        super(webTestClient, testDataSetup, jwtService);
    }

    @Test
    void shouldUpdateBookViaHttp() {
        var updated = BookDtoFixture.withUpdatesToDefault();
        authedPut(BOOK_PATH)
                .bodyValue(updated)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(updated);
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentBook() {
        var nonExistent = new BookDto(UUID.randomUUID(), "9780000000000", "Unknown", 0, Genre.FICTION, AUTHOR_ID);
        authedPut(BOOK_PATH).bodyValue(nonExistent).exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithNullId() {
        var invalidDto = new BookDto(null, "9780000000000", "Valid Title", 0, Genre.FICTION, AUTHOR_ID);
        assertBadRequestOnPut(BOOK_PATH, invalidDto, "id", "id must not be null");
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithNullIsbn() {
        var invalidDto = new BookDto(UUID.randomUUID(), null, "Valid Title", 0, Genre.FICTION, AUTHOR_ID);
        assertBadRequestOnPut(BOOK_PATH, invalidDto, "isbn", "isbn must not be null");
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithEmptyIsbn() {
        var invalidDto = new BookDto(UUID.randomUUID(), "", "Valid Title", 0, Genre.FICTION, AUTHOR_ID);
        assertBadRequestOnPut(BOOK_PATH, invalidDto, "isbn", "isbn must not be empty");
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithIsbnExceedingMaxLength() {
        var invalidDto = new BookDto(UUID.randomUUID(), "12345678901234", "Valid Title", 0, Genre.FICTION, AUTHOR_ID);
        assertBadRequestOnPut(BOOK_PATH, invalidDto, "isbn", "isbn must not exceed 13 characters");
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithNullTitle() {
        var invalidDto = new BookDto(UUID.randomUUID(), "9780000000000", null, 0, Genre.FICTION, AUTHOR_ID);
        assertBadRequestOnPut(BOOK_PATH, invalidDto, "title", "title must not be null");
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithEmptyTitle() {
        var invalidDto = new BookDto(UUID.randomUUID(), "9780000000000", "", 0, Genre.FICTION, AUTHOR_ID);
        assertBadRequestOnPut(BOOK_PATH, invalidDto, "title", "title must not be empty");
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithTitleExceedingMaxLength() {
        var longTitle = "a".repeat(Book.TITLE_MAX_LENGTH + 1);
        var invalidDto = new BookDto(UUID.randomUUID(), "9780000000000", longTitle, 0, Genre.FICTION, AUTHOR_ID);
        assertBadRequestOnPut(BOOK_PATH, invalidDto, "title", "title must not exceed " + Book.TITLE_MAX_LENGTH + " characters");
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithNegativeAvailableCopies() {
        var invalidDto = new BookDto(UUID.randomUUID(), "9780000000000", "Valid Title", -1, Genre.FICTION, AUTHOR_ID);
        assertBadRequestOnPut(BOOK_PATH, invalidDto, "availableCopies", "availableCopies must be zero or positive");
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithNullGenre() {
        var invalidDto = new BookDto(UUID.randomUUID(), "9780000000000", "Valid Title", 0, null, AUTHOR_ID);
        assertBadRequestOnPut(BOOK_PATH, invalidDto, "genre", "genre must not be null");
    }

    @Test
    void shouldReturn400WhenUpdatingBookWithNullAuthorId() {
        var invalidDto = new BookDto(UUID.randomUUID(), "9780000000000", "Valid Title", 0, Genre.FICTION, null);
        assertBadRequestOnPut(BOOK_PATH, invalidDto, "authorId", "authorId must not be null");
    }

    @Test
    void shouldFailToUpdateBookWhenExceedingTimeLimit() {
        doAnswer(invocation -> Mono
                .delay(Duration.of(2200, MILLIS))
                .then(Mono.just(invocation.getArgument(0)))
        )
            .when(updateBookInboundPort)
            .updateBook(any(Book.class));
        authedPut(BOOK_PATH)
                .bodyValue(BookDtoFixture.withUpdatesToDefault())
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ProblemDetail.class)
                .value(pd -> assertInternalServerError(pd, "/v1/books"));
    }
}
