package ru.sidey383.twitch.service;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.CustomReward;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.sidey383.twitch.model.TwitchOAuth2User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwitchRewardsService {

    private final TwitchHelix helix;
    private final TwitchOAuthService twitchOAuthService;

    @Nullable
    @Cacheable(value = "twitchRewardDescriptions", key = "#user.id", condition = "!(#result == null || #result.isEmpty())")
    public List<CustomReward> getRewardDescriptions(TwitchOAuth2User user) {
        try {
            twitchOAuthService.updateOAuthUserIfRequired(user);
            return helix.getCustomRewards(
                    user.getAccessToken(),
                    user.getId().toString(),
                    null,
                    false
            ).execute().getRewards();
        } catch (HystrixRuntimeException e) {
            log.warn("Failed to fetch reward descriptions for user {}", user.getId(), e);
            //TODO: handle specific Hystrix exceptions if needed
            return null;
        }
    }

}
