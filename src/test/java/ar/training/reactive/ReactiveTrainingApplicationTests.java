package ar.training.reactive;

import ar.training.reactive.controller.BookController;
import ar.training.reactive.dto.BookDto;
import ar.training.reactive.fixture.BookDtoFixture;
import ar.training.reactive.repository.BookRepository;
import ar.training.reactive.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.postgresql.PostgreSQLContainer;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ImportTestcontainers
@AutoConfigureWebTestClient
class ReactiveTrainingApplicationTests {

	@ServiceConnection
	private static final PostgreSQLContainer postgresSQLContainer =
		new PostgreSQLContainer("postgres:16-alpine");

	private final WebTestClient webTestClient;
	private final BookService bookService;
	private final BookRepository bookRepository;
	private final ApplicationContext applicationContext;
	private final CommandLineRunner commandLineRunner;

	@Autowired
    ReactiveTrainingApplicationTests(WebTestClient webTestClient,
                                     BookService bookService,
                                     BookRepository bookRepository,
                                     ApplicationContext applicationContext,
                                     CommandLineRunner commandLineRunner) {
        this.webTestClient = webTestClient;
        this.bookService = bookService;
		this.bookRepository = bookRepository;
        this.applicationContext = applicationContext;
        this.commandLineRunner = commandLineRunner;
    }

	@BeforeEach
	void beforeEach() throws Exception {
		bookRepository.deleteAll().block();
		commandLineRunner.run();
    }

    @Test
	void contextLoads() {
        assertContainsBeanOfType(BookController.class);
		assertContainsBeanOfType(BookService.class);
		assertContainsBeanOfType(BookRepository.class);
	}

	@Test
	void shouldUpdateBook() {
        StepVerifier
			.create(bookService.updateBook(BookDtoFixture.withUpdatesToDefault()))
			.expectNext(BookDtoFixture.withUpdatesToDefault())
			.verifyComplete();
	}

	@Test
	void shouldReturnAllBooks() {
		webTestClient.get()
				.uri("/v1/books")
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(BookDto.class)
				.hasSize(3)
				.contains(BookDtoFixture.withDefaults());
	}

	private void assertContainsBeanOfType(Class<?> requiredType) {
		var bean = applicationContext.getBean(requiredType);
		assertThat(bean)
				.isNotNull()
				.isInstanceOf(requiredType);
	}

}
