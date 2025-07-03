package ru.sidey383.twitch.dto.twitch;

import org.springframework.web.bind.annotation.RequestHeader;

public record TwitchEventSubHeaders(
        @RequestHeader("twitch-eventsub-message-id")
        String messageId,
        @RequestHeader(value = "twitch-eventsub-message-retry", required = false)
        Integer retryCount,
        @RequestHeader("twitch-eventsub-message-type")
        TwitchMessageType messageType,
        @RequestHeader(value = "twitch-eventsub-message-signature", required = false)
        String signature,
        @RequestHeader("twitch-eventsub-message-timestamp")
        String timestamp,
        @RequestHeader("twitch-eventsub-subscription-type")
        String subscriptionType,
        @RequestHeader("twitch-eventsub-subscription-version")
        String subscriptionVersion
) {
}
