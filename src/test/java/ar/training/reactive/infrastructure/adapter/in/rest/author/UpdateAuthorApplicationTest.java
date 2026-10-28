package ar.training.reactive.infrastructure.adapter.in.rest.author;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.application.port.in.author.UpdateAuthorInboundPort;
import ar.training.reactive.domain.model.Author;
import ar.training.reactive.fixture.author.AuthorDtoFixture;
import ar.training.reactive.fixture.author.AuthorFixture;
import ar.training.reactive.fixture.author.CreateAuthorDtoFixture;
import ar.training.reactive.infrastructure.adapter.in.rest.BaseApplicationTest;
import ar.training.reactive.infrastructure.adapter.in.rest.TestDataSetup;
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

import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorApplicationTestHelper.createAuthor;
import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorController.AUTHOR_PATH;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class UpdateAuthorApplicationTest extends BaseApplicationTest {

    @MockitoSpyBean
    private UpdateAuthorInboundPort updateAuthorInboundPort;

    @Autowired
    UpdateAuthorApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup, JwtService jwtService) {
        super(webTestClient, testDataSetup, jwtService);
    }

    @Test
    void shouldUpdateAuthorViaHttp() {
        var created = createAuthor(authedReadWriteUserClient(), CreateAuthorDtoFixture.withDefaults());
        var updated = new AuthorDto(created.id(), "Updated Name");

        authedPut(AUTHOR_PATH)
                .bodyValue(updated)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .isEqualTo(updated);
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentAuthor() {
        var nonExistent = new AuthorDto(UUID.randomUUID(), "Unknown");
        authedPut(AUTHOR_PATH).bodyValue(nonExistent).exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturn400WhenUpdatingAuthorWithNullId() {
        assertBadRequestOnPut(AUTHOR_PATH, new AuthorDto(null, "Valid Name"), "id", "id must not be null");
    }

    @Test
    void shouldReturn400WhenUpdatingAuthorWithNullName() {
        var created = createAuthor(authedReadWriteUserClient(), CreateAuthorDtoFixture.withDefaults());
        assertBadRequestOnPut(AUTHOR_PATH, new AuthorDto(created.id(), null), "name", "name must not be blank");
    }

    @Test
    void shouldReturn400WhenUpdatingAuthorWithEmptyName() {
        var created = createAuthor(authedReadWriteUserClient(), CreateAuthorDtoFixture.withDefaults());
        assertBadRequestOnPut(AUTHOR_PATH, new AuthorDto(created.id(), ""), "name", "name must not be blank");
    }

    @Test
    void shouldReturn400WhenUpdatingAuthorWithBlankName() {
        var created = createAuthor(authedReadWriteUserClient(), CreateAuthorDtoFixture.withDefaults());
        assertBadRequestOnPut(AUTHOR_PATH, new AuthorDto(created.id(), "   "), "name", "name must not be blank");
    }

    @Test
    void shouldReturn400WhenUpdatingAuthorWithNameExceedingMaxLength() {
        var created = createAuthor(authedReadWriteUserClient(), CreateAuthorDtoFixture.withDefaults());
        var longName = "a".repeat(Author.NAME_MAX_LENGTH + 1);
        assertBadRequestOnPut(AUTHOR_PATH, new AuthorDto(created.id(), longName), "name", "name must not exceed " + Author.NAME_MAX_LENGTH + " characters");
    }

    @Test
    void shouldFailToUpdateAuthorWhenExceedingTimeLimit() {
        doAnswer(_ -> Mono.delay(Duration.of(2200, MILLIS)).then(Mono.just(AuthorFixture.withDefaults())))
            .when(updateAuthorInboundPort)
            .updateAuthor(any(Author.class));
        authedPut(AUTHOR_PATH)
                .bodyValue(AuthorDtoFixture.withDefaults())
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ProblemDetail.class)
                .value(pd -> assertInternalServerError(pd, AUTHOR_PATH));
    }
}
