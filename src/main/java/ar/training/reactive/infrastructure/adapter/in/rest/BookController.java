package ar.training.reactive.infrastructure.adapter.in.rest;

import ar.training.reactive.application.port.in.CreateBookInboundPort;
import ar.training.reactive.application.port.in.DeleteBookByIdInboundPort;
import ar.training.reactive.application.port.in.GetAllBooksInboundPort;
import ar.training.reactive.application.port.in.GetBookByIdInboundPort;
import ar.training.reactive.application.port.in.UpdateBookInboundPort;
import ar.training.reactive.domain.model.Book;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
public class BookController {

    static final String BOOK_PATH = "/v1/books";
    static final String BOOK_BY_ID_PATH = BOOK_PATH + "/{id}";

    private final CreateBookInboundPort createBookInboundPort;
    private final GetAllBooksInboundPort getAllBooksInboundPort;
    private final GetBookByIdInboundPort getBookByIdInboundPort;
    private final UpdateBookInboundPort updateBookInboundPort;
    private final DeleteBookByIdInboundPort deleteBookByIdInboundPort;
    private final RestBookDtoMapper restBookDtoMapper;
    private final Logger logger;

    public BookController(CreateBookInboundPort createBookInboundPort,
                          GetAllBooksInboundPort getAllBooksInboundPort,
                          GetBookByIdInboundPort getBookByIdInboundPort,
                          UpdateBookInboundPort updateBookInboundPort,
                          DeleteBookByIdInboundPort deleteBookByIdInboundPort,
                          RestBookDtoMapper restBookDtoMapper) {
        this.createBookInboundPort = createBookInboundPort;
        this.updateBookInboundPort = updateBookInboundPort;
        this.getAllBooksInboundPort = getAllBooksInboundPort;
        this.getBookByIdInboundPort = getBookByIdInboundPort;
        this.deleteBookByIdInboundPort = deleteBookByIdInboundPort;
        this.restBookDtoMapper = restBookDtoMapper;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    @PostMapping(BOOK_PATH)
    @TimeLimiter(name = "createBookTimeout")
    @RateLimiter(name = "createBookRateLimit")
    public Mono<ResponseEntity<BookDto>> createBook(@Valid @RequestBody CreateBookDto createBookDto) {
        logger.info("BookController::createBook()");
        var book = new Book(UUID.randomUUID(), createBookDto.isbn(), createBookDto.title());
        return createBookInboundPort.createBook(book)
                .map(restBookDtoMapper::toBookDto)
                .map(ResponseEntity::ok);
    }

    @GetMapping(BOOK_PATH)
    @TimeLimiter(name = "getAllBooksTimeout")
    @RateLimiter(name = "getAllBooksRateLimit")
    public Flux<BookDto> getAllBooks() {
        logger.info("BookController::getAllBooks()");
        return getAllBooksInboundPort.getAllBooks()
                .map(restBookDtoMapper::toBookDto);
    }

    @GetMapping(BOOK_BY_ID_PATH)
    @TimeLimiter(name = "getBookByIdTimeout")
    @RateLimiter(name = "getBookByIdRateLimit")
    public Mono<ResponseEntity<BookDto>> getBookById(@PathVariable UUID id) {
        logger.info("BookController::getBookById({})", id);
        return getBookByIdInboundPort.getBookById(id)
                .map(restBookDtoMapper::toBookDto)
                .map(ResponseEntity::ok);
    }

    @PutMapping(BOOK_PATH)
    @TimeLimiter(name = "updateBookTimeout")
    @RateLimiter(name = "updateBookRateLimit")
    public Mono<ResponseEntity<BookDto>> updateBook(@Valid @RequestBody BookDto bookDto) {
        logger.info("BookController::updateBook({})", bookDto);
        var book = new Book(bookDto.id(), bookDto.isbn(), bookDto.title());
        return updateBookInboundPort.updateBook(book)
                .map(restBookDtoMapper::toBookDto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(BOOK_BY_ID_PATH)
    @TimeLimiter(name = "deleteBookByIdTimeout")
    @RateLimiter(name = "deleteBookByIdRateLimit")
    public Mono<ResponseEntity<Void>> deleteBookById(@PathVariable UUID id) {
        logger.info("BookController::deleteBookById({})", id);
        return deleteBookByIdInboundPort.deleteBookById(id)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
