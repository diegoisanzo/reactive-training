package ar.training.reactive.adapter.inbound.rest;

import ar.training.reactive.domain.model.Book;
import ar.training.reactive.domain.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/books")
public class BookController {

    private final BookService bookService;
    private final Logger logger;

    public BookController(BookService bookService) {
        this.bookService = bookService;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<BookDto>> getBookById(@PathVariable UUID id) {
        logger.info("BookController::getBookById({})", id);
        return bookService.getBookById(id)
                .map(BookDto::of)
                .map(ResponseEntity::ok);
    }

    @PutMapping
    public Mono<ResponseEntity<BookDto>> updateBookBy(@RequestBody BookDto bookDto) {
        logger.info("BookController::updateBookBy({})", bookDto);
        var book = new Book(bookDto.id(), bookDto.ISBN(), bookDto.title());
        return bookService.updateBook(book)
                .map(BookDto::of)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteBookById(@PathVariable UUID id) {
        logger.info("BookController::deleteBookById({})", id);
        return bookService.deleteById(id)
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }

    @GetMapping
    public Mono<ResponseEntity<List<BookDto>>> getAllBooks() {
        logger.info("BookController::getAllBooks()");
        return bookService.getAllBooks()
                .map(BookDto::of)
                .collectList()
                .map(ResponseEntity::ok);
    }
}
