package ru.sidey383.twitch.model;

import ru.sidey383.twitch.dto.twitch.TwitchEventSubType;

import java.util.List;

public enum TwitchWebhookType {

    REDEMPTION(
            TwitchEventSubType.CHANNEL_POINTS_REDEMPTION_ADD,
            TwitchEventSubType.CHANNEL_POINTS_REDEMPTION_UPDATE
    ),
    SUBSCRIPTION(
            TwitchEventSubType.SUBSCRIPTION_CREATE,
            TwitchEventSubType.SUBSCRIPTION_UPDATE,
            TwitchEventSubType.SUBSCRIPTION_DELETE
    );

    private final List<TwitchEventSubType> eventSubTypes;

    TwitchWebhookType(TwitchEventSubType... eventSubTypes) {
        this.eventSubTypes = List.of(eventSubTypes);
    }

    public List<TwitchEventSubType> getEventSubTypes() {
        return eventSubTypes;
    }

}
