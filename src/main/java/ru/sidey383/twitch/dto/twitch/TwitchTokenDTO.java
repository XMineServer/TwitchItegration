package ru.sidey383.twitch.dto.twitch;

import ru.sidey383.twitch.model.TwitchToken;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public record TwitchTokenDTO(
        Long id,
        String code,
        String accessToken,
        String refreshToken,
        Instant expiresIn,
        String tokenType,
        List<String> scope
) {
    public static TwitchTokenDTO fromEntity(TwitchToken token) {
        if (token == null) return null;
        return new TwitchTokenDTO(
                token.getId(),
                token.getCode(),
                token.getAccessToken(),
                token.getRefreshToken(),
                token.getExpiresIn(),
                token.getTokenType(),
                token.getScope() != null
                        ? Arrays.asList(token.getScope().split(" "))
                        : List.of()
        );
    }
}

