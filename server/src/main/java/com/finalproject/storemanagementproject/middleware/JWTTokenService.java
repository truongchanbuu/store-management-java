package com.finalproject.storemanagementproject.middleware;

import com.finalproject.storemanagementproject.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
public class JWTTokenService {
    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    @Value("${jwt.expiration.ms}")
    private int expiration;

    public JWTTokenService(JwtEncoder encoder, JwtDecoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        String role = user.getRole().toString();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(expiration, ChronoUnit.MILLIS))
                .subject(user.getEmail())
                .claim("role", role)
                .build();

        var encoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS512).build(), claims);
        return this.encoder.encode(encoderParameters).getTokenValue();
    }

    public String validateToken(String token) {
        try {
            Jwt jwt = this.decoder.decode(token);

            Instant now = Instant.now();
            if (Objects.requireNonNull(jwt.getExpiresAt()).isBefore(now)) return null;
            return jwt.getSubject();
        } catch (JwtException | NullPointerException e) {
            return null;
        }
    }

}
