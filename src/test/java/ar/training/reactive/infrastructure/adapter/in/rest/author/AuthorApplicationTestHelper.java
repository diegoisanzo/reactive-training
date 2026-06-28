package ar.training.reactive.infrastructure.adapter.in.rest.author;

import org.springframework.test.web.reactive.server.WebTestClient;

import static ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorController.AUTHOR_PATH;

final class AuthorApplicationTestHelper {

    private AuthorApplicationTestHelper() {}

    static AuthorDto createAuthor(WebTestClient client, CreateAuthorDto dto) {
        return client.post()
                .uri(AUTHOR_PATH)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthorDto.class)
                .returnResult()
                .getResponseBody();
    }
}
