package ru.sidey383.twitch.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public enum TwitchWebhookConditionType {

    BROADCASTER_USER_ID(
            TwitchWebhook::getConditionBroadcasterUserId,
            TwitchWebhook::setConditionBroadcasterUserId
    ),
    MODERATOR_USER_ID(
            TwitchWebhook::getConditionModeratorUserId,
            TwitchWebhook::setConditionModeratorUserId
    ),
    USER_ID(
            TwitchWebhook::getConditionUserId,
            TwitchWebhook::setConditionUserId
    ),
    USER_LOGIN(
            TwitchWebhook::getConditionUserLogin,
            TwitchWebhook::setConditionUserLogin
    ),
    USER_NAME(
            TwitchWebhook::getConditionUserName,
            TwitchWebhook::setConditionUserName
    ),
    REWARD_ID(
            TwitchWebhook::getConditionRewardId,
            TwitchWebhook::setConditionRewardId
    ),
    REWARD_TITLE(
            TwitchWebhook::getConditionRewardTitle,
            TwitchWebhook::setConditionRewardTitle
    ),
    REWARD_COST(
            TwitchWebhook::getConditionRewardCost,
            TwitchWebhook::setConditionRewardCost
    ),
    REWARD_PROMPT(
            TwitchWebhook::getConditionRewardPrompt,
            TwitchWebhook::setConditionRewardPrompt
    ),
    STATUS(
            TwitchWebhook::getConditionStatus,
            TwitchWebhook::setConditionStatus
    ),
    ID(
            TwitchWebhook::getConditionId,
            TwitchWebhook::setConditionId
    ),
    TYPE(
            TwitchWebhook::getConditionType,
            TwitchWebhook::setConditionType
    );

    private final Function<TwitchWebhook, String> accessor;
    private final BiConsumer<TwitchWebhook, String> setter;
}
