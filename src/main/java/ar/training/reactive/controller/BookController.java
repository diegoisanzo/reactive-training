package ar.training.reactive.controller;

import ar.training.reactive.dto.BookDto;
import ar.training.reactive.service.BookService;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
                .map(ResponseEntity::ok);
    }

    @PutMapping
    public Mono<ResponseEntity<BookDto>> updateBookBy(@RequestBody BookDto bookDto) {
        logger.info("BookController::updateBookBy({})", bookDto);
        return bookService.updateBook(bookDto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteBookById(@PathVariable UUID id) {
        logger.info("BookController::deleteBookById({})", id);
        return bookService.deleteById(id)
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }

    @GetMapping
    public Flux<BookDto> getAllBooks() {
        logger.info("BookController::getAllBooks()");
        return bookService.getAllBooks();
    }
}
