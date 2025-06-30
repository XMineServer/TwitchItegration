package ru.sidey383.twitch.dto.twitch.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record RedemptionEvent(
        @JsonProperty("user_id")
        String userId,
        @JsonProperty("user_login")
        String userLogin,
        @JsonProperty("user_name")
        String userName,
        @JsonProperty("broadcaster_user_id")
        Long broadcasterId,
        @JsonProperty("broadcaster_user_name")
        String broadcasterName,
        @JsonProperty("broadcaster_user_login")
        String broadcasterLogin,
        TwitchEventSubReward reward,
        @JsonProperty("user_input")
        String userInput,
        @JsonProperty("followed_at")
        Instant followedAt
) {}