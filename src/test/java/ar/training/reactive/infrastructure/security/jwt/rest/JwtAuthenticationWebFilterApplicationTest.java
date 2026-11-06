package ar.training.reactive.infrastructure.security.jwt.rest;

import ar.training.reactive.SharedContainers;
import ar.training.reactive.infrastructure.adapter.in.rest.BaseApplicationTest;
import ar.training.reactive.infrastructure.adapter.in.rest.TestDataSetup;
import ar.training.reactive.infrastructure.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static ar.training.reactive.infrastructure.adapter.in.rest.book.BookController.BOOK_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers(SharedContainers.class)
@AutoConfigureWebTestClient
class JwtAuthenticationWebFilterApplicationTest extends BaseApplicationTest {

    @MockitoSpyBean
    private JwtService jwtServiceSpy;

    @Autowired
    JwtAuthenticationWebFilterApplicationTest(WebTestClient webTestClient, TestDataSetup testDataSetup, JwtService jwtService) {
        super(webTestClient, testDataSetup, jwtService);
    }

    @Test
    void shouldRunJwtFilterOncePerRequest() {
        authedGet(BOOK_PATH)
                .exchange()
                .expectStatus().isOk();
        verify(jwtServiceSpy, times(1)).getAuthenticationFrom(any());
    }
}
