package ar.training.reactive.infrastructure.security.jwt.grpc;

import ar.training.reactive.infrastructure.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtGrpcAuthenticationProvider implements AuthenticationProvider {

    private final JwtService jwtService;

    public JwtGrpcAuthenticationProvider(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var auth = jwtService.getAuthenticationFrom((String) authentication.getCredentials());
        if (auth == null) {
            throw new BadCredentialsException("Invalid JWT token");
        }
        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
