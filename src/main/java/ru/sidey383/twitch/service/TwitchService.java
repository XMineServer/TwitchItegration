package ru.sidey383.twitch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sidey383.twitch.client.TwitchAPIClient;
import ru.sidey383.twitch.client.TwitchAuthClient;
import ru.sidey383.twitch.config.TwitchConfigurationProperties;
import ru.sidey383.twitch.dto.twitch.*;
import ru.sidey383.twitch.model.TwitchToken;
import ru.sidey383.twitch.model.TwitchUser;
import ru.sidey383.twitch.model.User;
import ru.sidey383.twitch.repository.TwitchTokenRepository;
import ru.sidey383.twitch.repository.TwitchUserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwitchService {
    private final TwitchAuthClient authClient;
    private final TwitchAPIClient apiClient;
    private final TwitchConfigurationProperties properties;
    private final TwitchTokenRepository twitchTokenRepository;
    private final TwitchUserRepository twitchUserRepository;

    private final ObjectProvider<TwitchService> selfProvider;


    public Optional<TwitchToken> acceptCode(User user, String code) {
        log.info("Receive twitch oauth2 code {} for user {}", code, user);
        TwitchTokenResponse response = exchangeCode(code);
        if (response != null) {
            log.info("Get twitch token {}", response);
            return Optional.of(selfProvider.getObject().saveTwitchToken(response, user, code));
        }
        return Optional.empty();
    }

    public Optional<TwitchToken> getTwitchToken(User user) {
        return twitchTokenRepository.findByOwner(user);
    }

    public Optional<TwitchUser> getTwitchUser(User user) {
        return twitchTokenRepository.findByOwner(user)
                .flatMap(twitchUserRepository::findByToken);
    }

    @Transactional
    public TwitchToken saveTwitchToken(TwitchTokenResponse response, User user, String code) {
        Optional<TwitchToken> oldToken = twitchTokenRepository.findByOwner(user);
        TwitchToken token = oldToken.orElse(new TwitchToken());
        token.setOwner(user);
        token.setAccessToken(response.accessToken());
        token.setRefreshToken(response.refreshToken());
        token.setCode(code);
        token.setExpiresIn(Instant.now().plus(response.expiresIn(), ChronoUnit.SECONDS));
        token.setTokenType(response.tokenType());
        token.setScope(String.join(" ", response.scope()));
        log.info("Saving twitch token {}", token);
        return twitchTokenRepository.save(token);
    }

    @Transactional
    public TwitchToken updateTwitchToken(TwitchToken oldToken, TwitchTokenResponse response) {
        var token = twitchTokenRepository.findById(oldToken.getId()).orElseThrow(() ->
          new IllegalArgumentException("Can't found twitch token with id %d".formatted(oldToken.getId()))
        );
        token.setAccessToken(response.accessToken());
        token.setRefreshToken(response.refreshToken());
        token.setExpiresIn(Instant.now().plus(response.expiresIn(), ChronoUnit.SECONDS));
        token.setTokenType(response.tokenType());
        token.setScope(String.join(" ", response.scope()));
        log.info("Update twitch token {}", token);
        return twitchTokenRepository.save(token);
    }

    @Transactional
    public Optional<TwitchUser> loadUser(TwitchToken token) {
        token = tryRenewToken(token);
        var users = apiClient.getUserInfo(
                "Bearer " + token.getAccessToken(),
                properties.id(),
                null,
                null
        ).getData();
        if (users.isEmpty()) {
            log.info("Can't receive user by toke {}", token);
            return Optional.empty();
        }
        var oldUser = twitchUserRepository.findByToken(token);
        TwitchUser user = mapTwitchUser(oldUser.orElse(new TwitchUser()), token, users);
        log.info("Savin user {} of token {}", user, token);
        return Optional.of(twitchUserRepository.save(user));
    }

    private static TwitchUser mapTwitchUser(TwitchUser user, TwitchToken token, List<TwitchUserResponse> users) {
        var userDto = users.getFirst();
        user.setToken(token);
        user.setUserId(userDto.id());
        user.setLogin(userDto.login());
        user.setDisplayName(userDto.displayName());
        user.setBroadcastType(userDto.broadcastType());
        user.setDescription(userDto.description());
        user.setProfileImageUrl(userDto.profileImageUrl());
        user.setOfflineImageUrl(userDto.offlineImageUrl());
        user.setCreatedAt(userDto.createdAt());
        return user;
    }

    public TwitchToken tryRenewToken(TwitchToken token) {
        if (token.isExpire(Instant.now())) {
            TwitchTokenResponse response = authClient.refresh(
                    token.refreshToken,
                    properties.id(),
                    properties.secret());
            if (response != null) {
                return selfProvider.getObject().updateTwitchToken(token, response);
            }
        }
        return token;
    }

    public void subscribeRewards(TwitchUser user) {
        var request = TwitchEventSubRequest.builder()
                .type(TwitchEventSubType.CHANNEL_POINTS_REDEMPTION_ADD.getValue())
                .version("1")
                .condition(Map.of("broadcaster_user_id", user.getUserId().toString()))
                .transport(
                        TwitchEventSubRequest.Transport.builder()
                                .method("webhook")
                                .callback(properties.callbackRedirectUri())
                                .secret(properties.secret())
                                .build()
                )
                .build();
        TwitchTokenResponse appToken = authClient.appToken(properties.id(), properties.secret());
        String auth = appToken.tokenType() + " " + appToken.accessToken();
        log.info("Subscribing for reward for user {}", user);
        apiClient.subscribeRequest(properties.id(), auth, request);
    }

    public TwitchTokenResponse exchangeCode(String code) {
        return authClient.token(
                properties.id(),
                properties.secret(),
                code,
                properties.redirectUri()
        );
    }

}
