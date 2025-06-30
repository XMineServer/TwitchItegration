package ru.sidey383.twitch.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.sidey383.twitch.dto.twitch.TwitchEventSubType;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum EventRewardType {
    MINECRAFT_WHITELIST_REWARD(
            "Minecraft Whitelist Reward",
            "Allows users to add their Minecraft username to the whitelist. Require active 'Channel Points Redemption' webhook.",
            TwitchEventSubType.CHANNEL_POINTS_REDEMPTION_ADD
    ),
    MINECRAFT_MESSAGE_REWARD(
            "Minecraft Message Reward",
            "Allows users to send a message in Minecraft chat. Require active 'Channel Points Redemption' webhook.",
            TwitchEventSubType.CHANNEL_POINTS_REDEMPTION_ADD
    );
    private final String displayName;
    private final String description;
    private final List<TwitchEventSubType> events;

    EventRewardType(String displayName, String description, TwitchEventSubType... events) {
        this.displayName = displayName;
        this.description = description;
        this.events = List.of(events);
    }
}
