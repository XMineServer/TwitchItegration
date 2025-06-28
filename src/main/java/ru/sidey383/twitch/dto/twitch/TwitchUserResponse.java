package ru.sidey383.twitch.dto.twitch;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record TwitchUserResponse(
        long id,
        String login,
        @JsonProperty("display_name")
        String displayName,
        String type,
        @JsonProperty("broadcaster_type")
        String broadcastType,
        String description,
        @JsonProperty("profile_image_url")
        String profileImageUrl,
        @JsonProperty("offline_image_url")
        String offlineImageUrl,
        String email,
        @JsonProperty("created_at")
        Instant createdAt
) {
}
