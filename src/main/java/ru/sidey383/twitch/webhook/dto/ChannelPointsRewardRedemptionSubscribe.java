package ru.sidey383.twitch.webhook.dto;

public record ChannelPointsRewardRedemptionSubscribe(
        String subscriptionId,
        String broadcasterId,
        String rewardId
) {
}
