package ru.sidey383.twitch.service;

import feign.FeignException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.sidey383.twitch.client.TwitchAPIClient;
import ru.sidey383.twitch.config.twitch.TwitchConfigurationProperties;
import ru.sidey383.twitch.dto.twitch.RewardDescription;
import ru.sidey383.twitch.model.TwitchOAuth2User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwitchRewardsService {

    private final TwitchAPIClient apiClient;
    private final TwitchConfigurationProperties twitchConfig;
    private final TwitchOAuthService twitchOAuthService;


    /**
     * Fetches the list of reward descriptions for a given Twitch user.
     *
     * @param user The authenticated Twitch user for whom to fetch rewards.
     * @return A list of {@link RewardDescription} objects or null when can't fetch rewards.
     */
    @Nullable
    @Cacheable(value = "twitchRewardDescriptions", key = "#user.id", condition = "!(#result == null || #result.isEmpty())")
    public List<RewardDescription> getRewardDescriptions(TwitchOAuth2User user) {
        try {
            twitchOAuthService.updateOAuthUserIfRequired(user);
            return apiClient.getRewardList(
                    "Bearer " + user.getAccessToken(),
                    twitchConfig.clientId(),
                    user.getId(),
                    null,
                    false
            ).getData();
        } catch (FeignException feignException) {
            log.warn("Failed to fetch reward descriptions for user {}", user.getId(), feignException);
            return null;
        }
    }

}
