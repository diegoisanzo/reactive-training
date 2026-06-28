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

import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorController.AUTHOR_BY_ID_PATH;
import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorController.AUTHOR_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class CreateAuthorApplicationTest extends BaseApplicationTest {

    @Autowired
    CreateAuthorApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup, JwtService jwtService) {
        super(webTestClient, testDataSetup, jwtService);
    }

    @Test
    void shouldCreateAuthor() {
        var createAuthorDto = CreateAuthorDtoFixture.withDefaults();
        authedReadWriteUserClient().post()
                .uri(AUTHOR_PATH)
                .bodyValue(createAuthorDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .value(newAuthorDto -> {
                    assertNotNull(newAuthorDto);
                    assertNotNull(newAuthorDto.id());
                    assertEquals(createAuthorDto.name(), newAuthorDto.name());
                });
    }

    @Test
    void shouldPersistCreatedAuthorWhenFetchedAgain() {
        var createAuthorDto = CreateAuthorDtoFixture.withDefaults();
        var created = authedReadWriteUserClient().post()
                .uri(AUTHOR_PATH)
                .bodyValue(createAuthorDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .returnResult()
                .getResponseBody();

        authedReadUserClient().get()
                .uri(AUTHOR_BY_ID_PATH, created.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .isEqualTo(created);
    }

    @Test
    void shouldReturn400WhenCreatingAuthorWithNullName() {
        var invalidDto = new CreateAuthorDto(null);
        authedReadWriteUserClient().post().uri(AUTHOR_PATH).bodyValue(invalidDto).exchange()
                .expectStatus().isBadRequest().expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenCreatingAuthorWithEmptyName() {
        var invalidDto = new CreateAuthorDto("");
        authedReadWriteUserClient().post().uri(AUTHOR_PATH).bodyValue(invalidDto).exchange()
                .expectStatus().isBadRequest().expectBody(ProblemDetail.class);
    }

    @Test
    void shouldReturn400WhenCreatingAuthorWithBlankName() {
        var invalidDto = new CreateAuthorDto("   ");
        authedReadWriteUserClient().post().uri(AUTHOR_PATH).bodyValue(invalidDto).exchange()
                .expectStatus().isBadRequest().expectBody(ProblemDetail.class);
    }
}
