package ru.sidey383.twitch.dto.twitch.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record TwitchEventSubConditions(
        @JsonProperty("broadcaster_user_id")
        String broadcasterUserId,
        @JsonProperty("moderator_user_id")
        String moderatorUserId,
        @JsonProperty("user_id")
        String userId,
        @JsonProperty("user_login")
        String userLogin,
        @JsonProperty("user_name")
        String userName,
        @JsonProperty("reward_id")
        String rewardId,
        @JsonProperty("reward_title")
        String rewardTitle,
        @JsonProperty("reward_cost")
        String rewardCost,
        @JsonProperty("reward_prompt")
        String rewardPrompt,
        @JsonProperty("status")
        String status,
        @JsonProperty("id")
        String id,
        @JsonProperty("type")
        String type
) {
}