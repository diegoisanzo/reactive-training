package ar.training.reactive.infrastructure.adapter.in.rest.author;

import ar.training.reactive.application.port.in.author.CreateAuthorInboundPort;
import ar.training.reactive.application.port.in.author.DeleteAuthorByIdInboundPort;
import ar.training.reactive.application.port.in.author.GetAllAuthorsInboundPort;
import ar.training.reactive.application.port.in.author.GetAuthorByIdInboundPort;
import ar.training.reactive.application.port.in.author.UpdateAuthorInboundPort;
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
public class AuthorController {

    public static final String AUTHOR_PATH = "/v1/authors";
    public static final String AUTHOR_BY_ID_PATH = AUTHOR_PATH + "/{id}";

    private final CreateAuthorInboundPort createAuthorInboundPort;
    private final GetAllAuthorsInboundPort getAllAuthorsInboundPort;
    private final GetAuthorByIdInboundPort getAuthorByIdInboundPort;
    private final UpdateAuthorInboundPort updateAuthorInboundPort;
    private final DeleteAuthorByIdInboundPort deleteAuthorByIdInboundPort;
    private final RestAuthorDtoMapper restAuthorDtoMapper;
    private final RestAuthorMapper restAuthorMapper;
    private final Logger logger;

    public AuthorController(CreateAuthorInboundPort createAuthorInboundPort,
                             GetAllAuthorsInboundPort getAllAuthorsInboundPort,
                             GetAuthorByIdInboundPort getAuthorByIdInboundPort,
                             UpdateAuthorInboundPort updateAuthorInboundPort,
                             DeleteAuthorByIdInboundPort deleteAuthorByIdInboundPort,
                             RestAuthorDtoMapper restAuthorDtoMapper,
                             RestAuthorMapper restAuthorMapper) {
        this.createAuthorInboundPort = createAuthorInboundPort;
        this.getAllAuthorsInboundPort = getAllAuthorsInboundPort;
        this.getAuthorByIdInboundPort = getAuthorByIdInboundPort;
        this.updateAuthorInboundPort = updateAuthorInboundPort;
        this.deleteAuthorByIdInboundPort = deleteAuthorByIdInboundPort;
        this.restAuthorDtoMapper = restAuthorDtoMapper;
        this.restAuthorMapper = restAuthorMapper;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    @PostMapping(AUTHOR_PATH)
    public Mono<ResponseEntity<AuthorDto>> createAuthor(@Valid @RequestBody CreateAuthorDto createAuthorDto) {
        logger.info("AuthorController::createAuthor()");
        return createAuthorInboundPort.createAuthor(restAuthorMapper.toAuthor(createAuthorDto))
                .map(restAuthorDtoMapper::toAuthorDto)
                .map(ResponseEntity::ok);
    }

    @GetMapping(AUTHOR_PATH)
    public Flux<AuthorDto> getAllAuthors() {
        logger.info("AuthorController::getAllAuthors()");
        return getAllAuthorsInboundPort.getAllAuthors()
                .map(restAuthorDtoMapper::toAuthorDto);
    }

    @GetMapping(AUTHOR_BY_ID_PATH)
    public Mono<ResponseEntity<AuthorDto>> getAuthorById(@PathVariable UUID id) {
        logger.info("AuthorController::getAuthorById({})", id);
        return getAuthorByIdInboundPort.getAuthorById(id)
                .map(restAuthorDtoMapper::toAuthorDto)
                .map(ResponseEntity::ok);
    }

    @PutMapping(AUTHOR_PATH)
    public Mono<ResponseEntity<AuthorDto>> updateAuthor(@Valid @RequestBody AuthorDto authorDto) {
        logger.info("AuthorController::updateAuthor({})", authorDto);
        return updateAuthorInboundPort.updateAuthor(restAuthorMapper.toAuthor(authorDto))
                .map(restAuthorDtoMapper::toAuthorDto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(AUTHOR_BY_ID_PATH)
    public Mono<ResponseEntity<Void>> deleteAuthorById(@PathVariable UUID id) {
        logger.info("AuthorController::deleteAuthorById({})", id);
        return deleteAuthorByIdInboundPort.deleteAuthorById(id)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
