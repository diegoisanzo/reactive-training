package ar.training.reactive.infrastructure.adapter.in.rest.author;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.fixture.author.CreateAuthorDtoFixture;
import ar.training.reactive.infrastructure.adapter.in.rest.BaseApplicationTest;
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
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class GetAuthorByIdApplicationTest extends BaseApplicationTest {

    @Autowired
    GetAuthorByIdApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup, JwtService jwtService) {
        super(webTestClient, testDataSetup, jwtService);
    }

    @Test
    void shouldReturnAuthorById() {
        var created = createAuthor(authedReadWriteUserClient(), CreateAuthorDtoFixture.withDefaults());

        authedReadUserClient().get()
                .uri(AUTHOR_BY_ID_PATH, created.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .isEqualTo(created);
    }

    @Test
    void shouldReturn404WhenAuthorNotFoundById() {
        var id = UUID.randomUUID();

        authedReadUserClient().get()
                .uri(AUTHOR_BY_ID_PATH, id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .value(problemDetail -> {
                    assertEquals("Author not found", problemDetail.getTitle());
                    assertEquals("Author with id " + id + " not found", problemDetail.getDetail());
                });
    }
}
