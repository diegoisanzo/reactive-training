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
import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorController.AUTHOR_PATH;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class UpdateAuthorApplicationTest extends BaseApplicationTest {

    @Autowired
    UpdateAuthorApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup, JwtService jwtService) {
        super(webTestClient, testDataSetup, jwtService);
    }

    @Test
    void shouldUpdateAuthorViaHttp() {
        var created = createAuthor(authedReadWriteUserClient(), CreateAuthorDtoFixture.withDefaults());
        var updated = new AuthorDto(created.id(), "Updated Name");

        authedReadWriteUserClient().put()
                .uri(AUTHOR_PATH)
                .bodyValue(updated)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .isEqualTo(updated);
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentAuthor() {
        var nonExistent = new AuthorDto(UUID.randomUUID(), "Unknown");
        authedReadWriteUserClient().put().uri(AUTHOR_PATH).bodyValue(nonExistent).exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturn400WhenUpdatingAuthorWithNullId() {
        var invalidDto = new AuthorDto(null, "Valid Name");
        authedReadWriteUserClient().put().uri(AUTHOR_PATH).bodyValue(invalidDto).exchange()
                .expectStatus().isBadRequest().expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenUpdatingAuthorWithNullName() {
        var created = createAuthor(authedReadWriteUserClient(), CreateAuthorDtoFixture.withDefaults());
        var invalidDto = new AuthorDto(created.id(), null);
        authedReadWriteUserClient().put().uri(AUTHOR_PATH).bodyValue(invalidDto).exchange()
                .expectStatus().isBadRequest().expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenUpdatingAuthorWithEmptyName() {
        var created = createAuthor(authedReadWriteUserClient(), CreateAuthorDtoFixture.withDefaults());
        var invalidDto = new AuthorDto(created.id(), "");
        authedReadWriteUserClient().put().uri(AUTHOR_PATH).bodyValue(invalidDto).exchange()
                .expectStatus().isBadRequest().expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenUpdatingAuthorWithBlankName() {
        var created = createAuthor(authedReadWriteUserClient(), CreateAuthorDtoFixture.withDefaults());
        var invalidDto = new AuthorDto(created.id(), "   ");
        authedReadWriteUserClient().put().uri(AUTHOR_PATH).bodyValue(invalidDto).exchange()
                .expectStatus().isBadRequest().expectBody(ProblemDetail.class);
    }
}
