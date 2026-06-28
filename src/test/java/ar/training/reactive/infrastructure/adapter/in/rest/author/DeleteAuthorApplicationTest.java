package ar.training.reactive.infrastructure.adapter.in.rest.author;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.domain.model.Genre;
import ar.training.reactive.fixture.author.CreateAuthorDtoFixture;
import ar.training.reactive.infrastructure.adapter.in.rest.BaseApplicationTest;
import ar.training.reactive.infrastructure.adapter.in.rest.book.BookController;
import ar.training.reactive.infrastructure.adapter.in.rest.book.CreateBookDto;
import ar.training.reactive.infrastructure.adapter.in.rest.TestDataSetup;
import ar.training.reactive.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorApplicationTestHelper.createAuthor;
import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorController.AUTHOR_BY_ID_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class DeleteAuthorApplicationTest extends BaseApplicationTest {

    @Autowired
    DeleteAuthorApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup, JwtService jwtService) {
        super(webTestClient, testDataSetup, jwtService);
    }

    @Test
    void shouldDeleteAuthor() {
        var created = createAuthor(authedReadWriteUserClient(), CreateAuthorDtoFixture.withDefaults());

        authedAdminUserClient().delete().uri(AUTHOR_BY_ID_PATH, created.id()).exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentAuthor() {
        authedAdminUserClient().delete().uri(AUTHOR_BY_ID_PATH, UUID.randomUUID()).exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturn409WhenDeletingAuthorWithAssociatedBooks() {
        var author = createAuthor(authedReadWriteUserClient(), CreateAuthorDtoFixture.withDefaults());
        authedReadWriteUserClient().post()
                .uri(BookController.BOOK_PATH)
                .bodyValue(new CreateBookDto("1780132350999", "Some Book", 1, Genre.FICTION, author.id()))
                .exchange()
                .expectStatus().isOk();

        authedAdminUserClient().delete().uri(AUTHOR_BY_ID_PATH, author.id()).exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ProblemDetail.class)
                .value(pd -> {
                    assertNotNull(pd);
                    assertEquals("Author has books", pd.getTitle());
                    assertEquals(409, pd.getStatus());
                    assertEquals("Author with id " + author.id() + " has associated books", pd.getDetail());
                });
    }
}
