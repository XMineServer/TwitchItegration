package ru.sidey383.twitch.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sidey383.twitch.client.TwitchAuthClient;
import ru.sidey383.twitch.config.twitch.TwitchConfigurationProperties;
import ru.sidey383.twitch.dto.twitch.TwitchTokenResponse;
import ru.sidey383.twitch.exception.TwitchInvalidRefreshTokenException;
import ru.sidey383.twitch.model.TwitchOAuth2User;
import ru.sidey383.twitch.repository.TwitchOAuth2UserRepository;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwitchOAuthService {

    private final TwitchOAuth2UserRepository userRepository;
    private final TwitchAuthClient authClient;
    private final TwitchConfigurationProperties properties;


    private String authToken;
    private Instant tokenExpiration;

    public void updateOAuthUserIfRequired(@Nonnull TwitchOAuth2User user) {
        if (!user.isExpire(Instant.now(), Duration.ofSeconds(10))) return;
        TwitchTokenResponse answer;
        try {
             answer = authClient.refresh(
                     properties.clientId(),
                     properties.clientSecret(),
                    user.getRefreshToken()
            );
        } catch (FeignException feignException) {
            String msg = feignException.contentUTF8();
            if (feignException.status() == 400 && msg != null && msg.contains("Invalid refresh token")) {
                log.warn("Invalid refresh token for user {}. User should re-authenticate.", user.getId());
                throw new TwitchInvalidRefreshTokenException("Invalid refresh token", feignException);
            }
            log.warn("Failed to refresh OAuth2 user {}", user.getId(), feignException);
            return;
        }
        user.setAccessToken(answer.accessToken());
        user.setRefreshToken(answer.refreshToken());
        user.setExpiresIn(Instant.now().plusSeconds(answer.expiresIn()));
        user.setScope(String.join(" ", answer.scope()));
        userRepository.save(user);
    }

    public String getAuthToken() throws FeignException {
        if (authToken != null && tokenExpiration != null && Instant.now().minusSeconds(10).isBefore(tokenExpiration)) {
            return authToken;
        }
        var response = authClient.appToken(
                properties.clientId(),
                properties.clientSecret()
        );
        authToken = response.accessToken();
        tokenExpiration = Instant.now().plusSeconds(response.expiresIn());
        return authToken;
    }

}
