package ru.sidey383.twitch.dto.twitch.event;

import lombok.Builder;

@Builder
public record TwitchEventSubTransport(
        String method,
        String callback,
        String secret
) {
}
