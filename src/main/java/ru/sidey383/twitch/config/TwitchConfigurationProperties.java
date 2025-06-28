package ru.sidey383.twitch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("twitch")
public record TwitchConfigurationProperties(
        String secret,
        String id,
        String redirectUri,
        String callbackRedirectUri,
        String webhookUri
) {
}
