package ru.sidey383.twitch.dto.twitch;

import lombok.Builder;

import java.util.Map;

@Builder
public record TwitchEventSubRequest(
        String type,
        String version,
        Map<String, String> condition,
        Transport transport
) {

    @Builder
    public record Transport(
            String method,
            String callback,
            String secret
    ) {}
}

