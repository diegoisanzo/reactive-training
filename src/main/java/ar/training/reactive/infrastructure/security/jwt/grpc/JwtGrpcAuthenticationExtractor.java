package ar.training.reactive.infrastructure.security.jwt.grpc;

import ar.training.reactive.infrastructure.security.jwt.JwtService;
import io.grpc.Attributes;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import org.springframework.grpc.server.security.GrpcAuthenticationExtractor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtGrpcAuthenticationExtractor implements GrpcAuthenticationExtractor {

    private static final Metadata.Key<String> AUTHORIZATION_KEY =
            Metadata.Key.of(HttpHeaders.AUTHORIZATION, Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public Authentication extract(Metadata headers, Attributes attributes, MethodDescriptor<?, ?> method) {
        var token = JwtService.extractBearerToken(headers.get(AUTHORIZATION_KEY));
        if (token == null) {
            return null;
        }
        return new PreAuthenticatedAuthenticationToken(token, token);
    }
}
