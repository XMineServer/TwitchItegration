package ru.sidey383.twitch.dto.auth;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String tokenType
) {
    public AuthResponse(String accessToken, String refreshToken, Long expiresIn) {
        this(accessToken, refreshToken, expiresIn, "Bearer");
    }
}
