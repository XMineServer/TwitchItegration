package ru.sidey383.twitch.config;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import com.github.twitch4j.helix.TwitchHelix;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sidey383.twitch.config.twitch.TwitchConfigurationProperties;

@Configuration
@RequiredArgsConstructor
public class Twitch4JConfig {

    private final TwitchConfigurationProperties properties;

    @Bean
    public TwitchClient twitchClient() {
        CredentialManager credentialManager = CredentialManagerBuilder.builder().build();
        credentialManager.registerIdentityProvider(new TwitchIdentityProvider(properties.clientId(), properties.clientSecret(), properties.redirectUri()));
        return TwitchClientBuilder.builder()
                .withEnableHelix(true)
                .withTimeout(20000)
                .withCredentialManager(credentialManager)
                .withClientId(properties.clientId())
                .withClientSecret(properties.clientSecret())
                .build();
    }

    @Bean
    public TwitchHelix twitchHelixHttpClient(TwitchClient client) {
        return client.getHelix();
    }

}
