package ar.training.reactive.infrastructure.adapter.in.rest.book;

import ar.training.reactive.application.port.in.book.CreateBookInboundPort;
import ar.training.reactive.application.port.in.book.DeleteBookByIdInboundPort;
import ar.training.reactive.application.port.in.book.GetAllBooksInboundPort;
import ar.training.reactive.application.port.in.book.GetBookByIdInboundPort;
import ar.training.reactive.application.port.in.book.UpdateBookInboundPort;
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

    public static final String BOOK_PATH = "/v1/books";
    public static final String BOOK_BY_ID_PATH = BOOK_PATH + "/{id}";

    private final CreateBookInboundPort createBookInboundPort;
    private final GetAllBooksInboundPort getAllBooksInboundPort;
    private final GetBookByIdInboundPort getBookByIdInboundPort;
    private final UpdateBookInboundPort updateBookInboundPort;
    private final DeleteBookByIdInboundPort deleteBookByIdInboundPort;
    private final RestBookDtoMapper restBookDtoMapper;
    private final RestBookMapper restBookMapper;
    private final Logger logger;

    public BookController(CreateBookInboundPort createBookInboundPort,
                          GetAllBooksInboundPort getAllBooksInboundPort,
                          GetBookByIdInboundPort getBookByIdInboundPort,
                          UpdateBookInboundPort updateBookInboundPort,
                          DeleteBookByIdInboundPort deleteBookByIdInboundPort,
                          RestBookDtoMapper restBookDtoMapper,
                          RestBookMapper restBookMapper) {
        this.createBookInboundPort = createBookInboundPort;
        this.updateBookInboundPort = updateBookInboundPort;
        this.getAllBooksInboundPort = getAllBooksInboundPort;
        this.getBookByIdInboundPort = getBookByIdInboundPort;
        this.deleteBookByIdInboundPort = deleteBookByIdInboundPort;
        this.restBookDtoMapper = restBookDtoMapper;
        this.restBookMapper = restBookMapper;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    @PostMapping(BOOK_PATH)
    @TimeLimiter(name = "createBookTimeout")
    @RateLimiter(name = "createBookRateLimit")
    public Mono<ResponseEntity<BookDto>> createBook(@Valid @RequestBody CreateBookDto createBookDto) {
        logger.info("BookController::createBook()");
        return createBookInboundPort.createBook(restBookMapper.toBook(createBookDto))
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
        return updateBookInboundPort.updateBook(restBookMapper.toBook(bookDto))
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
