package ru.sidey383.twitch.config.twitch;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.security.oauth2.client.registration.twitch")
public record TwitchConfigurationProperties(
        String clientSecret,
        String clientId,
        String webhookUri,
        String redirectUri
) {
}
