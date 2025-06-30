package ru.sidey383.twitch.dto.twitch;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record WebhookSubscription(
        String id,
        //TODO: enum
        String status,
        String type,
        String version,
        Long cost,
        @JsonProperty("created_at")
        Instant createdAt
) {}
