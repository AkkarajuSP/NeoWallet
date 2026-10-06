package com.neowallet.identity.security;

import com.neowallet.config.AuthProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService {

    private final AuthProperties authProperties;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        String secret = authProperties.getJwtSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("neowallet.auth.jwt-secret must be configured");
        }
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateAccessToken(UUID userId, UUID familyId) {
        Instant now = Instant.now();
        Instant expiry = now.plus(authProperties.getAccessTokenTtl());

        return Jwts.builder()
            .subject(userId.toString())
            .issuer(authProperties.getIssuer())
            .audience().add(authProperties.getAudience()).and()
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .id(UUID.randomUUID().toString())
            .claim("family_id", familyId != null ? familyId.toString() : null)
            .signWith(signingKey, Jwts.SIG.HS256)
            .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
            .verifyWith(signingKey)
            .requireIssuer(authProperties.getIssuer())
            .requireAudience(authProperties.getAudience())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parseToken(token).getSubject());
    }

    public Instant getExpiry(String token) {
        return parseToken(token).getExpiration().toInstant();
    }

    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            log.debug("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    public Instant getAccessTokenExpiry() {
        return Instant.now().plus(authProperties.getAccessTokenTtl());
    }

}
