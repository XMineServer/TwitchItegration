package ru.sidey383.twitch.client;

import jakarta.annotation.Nullable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sidey383.twitch.dto.twitch.DataList;
import ru.sidey383.twitch.dto.twitch.RewardDescription;
import ru.sidey383.twitch.dto.twitch.event.TwitchEventSub;
import ru.sidey383.twitch.dto.twitch.TwitchUserResponse;

import java.util.List;


@FeignClient(
        name = "twitchAPIClient",
        url = "https://api.twitch.tv/helix",
        configuration = ru.sidey383.twitch.config.twitch.JsonFeignConfig.class
)
public interface TwitchAPIClient {

    @PostMapping(value = "/eventsub/subscriptions", consumes = MediaType.APPLICATION_JSON_VALUE)
    DataList<TwitchEventSub> subscribeRequest(
            @RequestHeader("Client-Id")
            String clientID,
            @RequestHeader("Authorization")
            String authorization,
            @RequestBody TwitchEventSub request

    );

    @GetMapping(value = "/eventsub/subscriptions", consumes = MediaType.APPLICATION_JSON_VALUE)
    DataList<TwitchEventSub> getSubscriptions(
            @RequestHeader("Client-Id")
            String clientID,
            @RequestHeader("Authorization")
            String authorization,
            @Nullable
            @RequestParam("subscription_id")
            String subscriptionId
    );

    @DeleteMapping(value = "/eventsub/subscriptions", consumes = MediaType.APPLICATION_JSON_VALUE)
    void deleteSubscriptions(
            @RequestHeader("Client-Id")
            String clientID,
            @RequestHeader("Authorization")
            String authorization,
            @Nullable
            @RequestParam("id")
            String subscriptionId
    );

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
