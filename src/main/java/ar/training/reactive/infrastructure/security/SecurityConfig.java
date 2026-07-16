package ar.training.reactive.infrastructure.security;

import ar.training.reactive.infrastructure.adapter.in.grpc.author.AuthorGrpcService;
import ar.training.reactive.infrastructure.adapter.in.grpc.book.BookGrpcService;
import ar.training.reactive.infrastructure.adapter.in.rest.auth.AuthController;
import ar.training.reactive.infrastructure.adapter.in.rest.author.AuthorController;
import ar.training.reactive.infrastructure.adapter.in.rest.book.BookController;
import ar.training.reactive.infrastructure.security.jwt.grpc.JwtGrpcAuthenticationExtractor;
import ar.training.reactive.infrastructure.security.jwt.grpc.JwtGrpcAuthenticationProvider;
import ar.training.reactive.infrastructure.security.jwt.rest.JwtAuthenticationWebFilter;
import io.grpc.ServerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.grpc.server.security.GrpcSecurity;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity(proxyTargetClass = true)
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
                        .pathMatchers(HttpMethod.GET, AuthorController.AUTHOR_PATH, AuthorController.AUTHOR_PATH + "/**").hasRole(Role.READ.name())
                        .pathMatchers(HttpMethod.POST, AuthorController.AUTHOR_PATH).hasRole(Role.WRITE.name())
                        .pathMatchers(HttpMethod.PUT, AuthorController.AUTHOR_PATH).hasRole(Role.WRITE.name())
                        .pathMatchers(HttpMethod.DELETE, AuthorController.AUTHOR_PATH + "/**").hasRole(Role.ADMIN.name())
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public ReactiveUserDetailsService reactiveUserDetailsService(PasswordEncoder passwordEncoder) {
        return new MapReactiveUserDetailsService(users(passwordEncoder));
    }

    private List<UserDetails> users(PasswordEncoder passwordEncoder) {
        return List.of(
            user(passwordEncoder, "read-user",       Role.READ.name()),
            user(passwordEncoder, "read-write-user", Role.READ.name(), Role.WRITE.name()),
            user(passwordEncoder, "admin-user",      Role.ALL_NAMES.toArray(String[]::new))
        );
    }

    private UserDetails user(
            PasswordEncoder passwordEncoder, String username, String... roles) {
        return User.withUsername(username)
                .password(passwordEncoder.encode("password"))
                .roles(roles)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @GlobalServerInterceptor
    ServerInterceptor grpcSecurityChain(
            GrpcSecurity grpc,
            JwtGrpcAuthenticationExtractor jwtExtractor,
            JwtGrpcAuthenticationProvider jwtProvider) throws Exception {

        return grpc.authorizeRequests(requestMapperConfigurer -> requestMapperConfigurer
            .methods(
                BookGrpcService.class.getSimpleName() + "/GetAllBooks",
                AuthorGrpcService.class.getSimpleName() + "/GetAllAuthors"
            ).hasAuthority(Role.READ.getSpringSecurityRole())
            .allRequests().authenticated()
        )
        .authenticationExtractor(jwtExtractor)
        .authenticationProvider(jwtProvider)
        .build();
    }
}
