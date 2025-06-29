package ru.sidey383.twitch.dto.twitch.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.Instant;

@Builder
public record TwitchEventSub(
        String id,
        String type,
        String version,
        TwitchEventSubConditions condition,
        TwitchEventSubTransport transport,
        @JsonProperty("created_at")
        Instant createdAt
) {
}

