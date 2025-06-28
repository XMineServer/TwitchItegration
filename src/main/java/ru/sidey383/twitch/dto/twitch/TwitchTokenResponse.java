package ru.sidey383.twitch.dto.twitch;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TwitchTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("expires_in") long expiresIn,
        @JsonProperty("scope") List<String> scope,
        @JsonProperty("token_type") String tokenType
) {}
