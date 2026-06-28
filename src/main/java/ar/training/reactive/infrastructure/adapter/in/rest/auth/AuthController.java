package ar.training.reactive.infrastructure.adapter.in.rest.auth;

import ar.training.reactive.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
public class AuthController {

    public static final String LOGIN_PATH = "/auth/login";

    private final ReactiveUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            ReactiveUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping(LOGIN_PATH)
    public Mono<TokenDto> login(@Valid @RequestBody LoginDto loginDto) {
        return userDetailsService.findByUsername(loginDto.username())
                .filter(user -> passwordEncoder.matches(loginDto.password(), user.getPassword()))
                .map(user -> new TokenDto(jwtService.generateToken(user)))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")));
    }
}
