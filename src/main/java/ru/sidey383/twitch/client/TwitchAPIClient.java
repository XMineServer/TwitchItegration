package ru.sidey383.twitch.client;

import jakarta.annotation.Nullable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sidey383.twitch.dto.twitch.DataList;
import ru.sidey383.twitch.dto.twitch.RewardDescription;
import ru.sidey383.twitch.dto.twitch.TwitchUserResponse;

import java.util.List;


@FeignClient(
        name = "twitchAPIClient",
        url = "https://api.twitch.tv/helix",
        configuration = ru.sidey383.twitch.config.twitch.JsonFeignConfig.class
)
//TODO: remove this client and use Twitch4J instead
public interface TwitchAPIClient {

    @GetMapping("/users")
    DataList<TwitchUserResponse> getUserInfo(
            @RequestHeader("Authorization")
            String bearer,
            @RequestHeader("Client-Id")
            String clientId,
            @Nullable
            @RequestParam("id")
            Long id,
            @Nullable
            @RequestParam("login")
            String login
    );

    @GetMapping("/channel_points/custom_rewards")
    DataList<RewardDescription> getRewardList(
            @RequestHeader("Authorization")
            String bearer,
            @RequestHeader("Client-Id")
            String clientId,
            @RequestParam("broadcaster_id")
            Long broadcasterId,
            @Nullable
            @RequestParam("id")
            List<String> id,
            @RequestParam("only_manageable_rewards")
            Boolean onlyManageableRewards
    );

}
