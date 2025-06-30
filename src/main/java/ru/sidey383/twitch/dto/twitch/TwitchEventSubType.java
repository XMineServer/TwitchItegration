package ru.sidey383.twitch.dto.twitch;

import lombok.Getter;
import ru.sidey383.twitch.model.TwitchWebhookConditionType;

import java.util.List;
import java.util.Optional;

@Getter
public enum TwitchEventSubType {
    STREAM_ONLINE(
            "stream.online",
            "1",
            TwitchWebhookConditionType.BROADCASTER_USER_ID
    ),
    STREAM_OFFLINE(
            "stream.offline",
            "1",
            TwitchWebhookConditionType.BROADCASTER_USER_ID
    ),
    CHANNEL_POINTS_REDEMPTION_ADD(
            "channel.channel_points_custom_reward_redemption.add",
            "1",
            TwitchWebhookConditionType.BROADCASTER_USER_ID
    ),
    CHANNEL_POINTS_REDEMPTION_UPDATE(
            "channel.channel_points_custom_reward_redemption.update",
            "1",
            TwitchWebhookConditionType.BROADCASTER_USER_ID
    ),
    CHANNEL_FOLLOW("channel.follow", "1"),
    CHANNEL_SUBSCRIBE("channel.subscribe", "1"),
    CHANNEL_RAID("channel.raid", "1"),
    CHANNEL_BAN("channel.ban", "1"),
    CHANNEL_UNBAN("channel.unban", "1"),
    SUBSCRIPTION_CREATE("channel.subscription.create", "1"),
    SUBSCRIPTION_UPDATE("channel.subscription.update", "1"),
    SUBSCRIPTION_DELETE("channel.subscription.delete", "1")
    ;

    private final String value;
    private final List<TwitchWebhookConditionType> conditionTypes;
    private final String version;

    TwitchEventSubType(String value, String version, TwitchWebhookConditionType... conditionTypes) {
        this.value = value;
        this.version = version;
        this.conditionTypes = List.of(conditionTypes);
    }

    public static Optional<TwitchEventSubType> fromValue(String value) {
        for (TwitchEventSubType type : values()) {
            if (type.value.equals(value)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

}

