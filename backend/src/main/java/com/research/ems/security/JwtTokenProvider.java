package com.research.ems.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey signingKey;
    private final long accessTokenExpirationMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String base64Secret,
            @Value("${app.jwt.access-token-expiration-ms:900000}")
            long accessTokenExpirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        if (accessTokenExpirationMs <= 0) {
            throw new IllegalArgumentException(
                    "JWT access-token expiration must be positive");
        }
    }

    public String generateAccessToken(UserDetails userDetails) {
        Instant issuedAt = Instant.now();
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plusMillis(accessTokenExpirationMs)))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) throws JwtException {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername());
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }
}
