package ar.training.reactive.infrastructure.security;

import ar.training.reactive.infrastructure.adapter.in.rest.auth.AuthController;
import ar.training.reactive.infrastructure.adapter.in.rest.book.BookController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            JwtAuthenticationWebFilter jwtFilter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(auth -> auth
                        .pathMatchers(HttpMethod.POST, AuthController.LOGIN_PATH).permitAll()
                        .pathMatchers(HttpMethod.GET, BookController.BOOK_PATH, BookController.BOOK_PATH + "/**").hasRole(Role.READ.name())
                        .pathMatchers(HttpMethod.POST, BookController.BOOK_PATH).hasRole(Role.WRITE.name())
                        .pathMatchers(HttpMethod.PUT, BookController.BOOK_PATH).hasRole(Role.WRITE.name())
                        .pathMatchers(HttpMethod.DELETE, BookController.BOOK_PATH + "/**").hasRole(Role.ADMIN.name())
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(PasswordEncoder encoder) {
        return new MapReactiveUserDetailsService(
                user(encoder, "read-user",       Role.READ.name()),
                user(encoder, "read-write-user", Role.READ.name(), Role.WRITE.name()),
                user(encoder, "admin-user",      Role.ALL_NAMES.toArray(String[]::new))
        );
    }

    private UserDetails user(
            PasswordEncoder encoder, String username, String... roles) {
        return User.withUsername(username)
                .password(encoder.encode("password"))
                .roles(roles)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
