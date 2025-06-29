package ru.sidey383.twitch.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import ru.sidey383.twitch.client.TwitchAPIClient;
import ru.sidey383.twitch.config.twitch.TwitchConfigurationProperties;
import ru.sidey383.twitch.dto.twitch.TwitchUserResponse;
import ru.sidey383.twitch.model.TwitchOAuth2User;
import ru.sidey383.twitch.repository.TwitchOAuth2UserRepository;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class TwitchOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final TwitchAPIClient twitchAPIClient;
    private final TwitchConfigurationProperties configurationProperties;
    private final TwitchOAuth2UserRepository twitchOAuth2UserRepository;

    @Override
    public TwitchOAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        var accessToken = userRequest.getAccessToken();
        Map<String, Object> additionalParameters = userRequest.getAdditionalParameters();

        var users = twitchAPIClient.getUserInfo(
                "Bearer " + accessToken.getTokenValue(),
                configurationProperties.id(),
                null,
                null
        ).getData();
        if (users.isEmpty()) {
            throw new OAuth2AuthenticationException("No user data found for the provided access token.");
        }
        var userDto = users.getFirst();
        var user = twitchOAuth2UserRepository.findById(userDto.id())
                .orElseGet(() -> {
                    var userCreate = new TwitchOAuth2User();
                    userCreate.setId(userDto.id());
                    return userCreate;
                });
        return updateUserData(user, userDto, accessToken, additionalParameters);
    }

    private TwitchOAuth2User updateUserData(
            TwitchOAuth2User user,
            TwitchUserResponse usrDTO,
            OAuth2AccessToken accessToken,
            Map<String, Object> additionalParameters
    ) {
        user.setId(usrDTO.id());
        user.setLogin(usrDTO.login());
        user.setDisplayName(usrDTO.displayName());
        user.setBroadcastType(usrDTO.broadcastType());
        user.setAccessToken(accessToken.getTokenValue());
        user.setRefreshToken((String) additionalParameters.getOrDefault("refresh_token", ""));
        user.setExpiresIn(accessToken.getExpiresAt());
        user.setScope(String.join(" ", accessToken.getScopes()));
        user.setDescription(usrDTO.description());
        user.setProfileImageUrl(usrDTO.profileImageUrl());
        user.setOfflineImageUrl(usrDTO.offlineImageUrl());
        user.setEmail(usrDTO.email());
        user.setCreatedAt(usrDTO.createdAt());

        twitchOAuth2UserRepository.save(user);
        return user;
    }
}