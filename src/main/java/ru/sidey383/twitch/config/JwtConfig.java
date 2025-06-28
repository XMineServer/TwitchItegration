package ru.sidey383.twitch.config;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;
import java.util.Base64;

@RequiredArgsConstructor
public class JwtConfig {

    private final String secret;
    @Getter
    private final long expiration;

    public SecretKey getSecretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(decodedKey);
    }

}
