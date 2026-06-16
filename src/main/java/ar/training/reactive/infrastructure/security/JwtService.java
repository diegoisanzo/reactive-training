package ar.training.reactive.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static ar.training.reactive.infrastructure.security.Role.SPRING_SECURITY_ROLE_PREFIX;

@Service
public class JwtService {

    private static final String ROLES_CLAIM = "roles";

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(UserDetails userDetails) {
        var roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .map(this::removeRolePrefix)
                .toList();
        return generateToken(userDetails.getUsername(), roles);
    }

    private String removeRolePrefix(String role) {
        return role.startsWith(SPRING_SECURITY_ROLE_PREFIX) ?
                    role.substring(SPRING_SECURITY_ROLE_PREFIX.length()) :
                    role;
    }

    public String generateToken(String username, List<String> roles) {
        final long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(username)
                .claim(ROLES_CLAIM, roles)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .signWith(key)
                .compact();
    }

    @SuppressWarnings("unchecked")
    public UsernamePasswordAuthenticationToken getAuthenticationFrom(String token) {
        if (token == null) {
            return null;
        }
        try {
            var claims = parseClaims(token);
            var authorities = ((List<String>) claims.get(ROLES_CLAIM)).stream()
                    .map(role -> new SimpleGrantedAuthority(SPRING_SECURITY_ROLE_PREFIX + role))
                    .toList();
            return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
        } catch (Exception e) {
            return null;
        }
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRolesFrom(String token) {
        return (List<String>) parseClaims(token).get(ROLES_CLAIM);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
