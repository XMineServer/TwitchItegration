package ru.sidey383.twitch.dto.twitch;

public enum TwitchEventSubType {
    STREAM_ONLINE("stream.online"),
    STREAM_OFFLINE("stream.offline"),
    CHANNEL_POINTS_REDEMPTION_ADD("channel.channel_points_custom_reward_redemption.add"),
    CHANNEL_POINTS_REDEMPTION_UPDATE("channel.channel_points_custom_reward_redemption.update"),
    CHANNEL_FOLLOW("channel.follow"),
    CHANNEL_SUBSCRIBE("channel.subscribe"),
    CHANNEL_RAID("channel.raid"),
    CHANNEL_BAN("channel.ban"),
    CHANNEL_UNBAN("channel.unban");

    private final String value;

    TwitchEventSubType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

