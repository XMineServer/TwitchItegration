package ru.sidey383.twitch.client;

import jakarta.annotation.Nullable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sidey383.twitch.dto.twitch.DataList;
import ru.sidey383.twitch.dto.twitch.event.TwitchEventSub;
import ru.sidey383.twitch.dto.twitch.TwitchUserResponse;


@FeignClient(
        name = "twitchAPIClient",
        url = "https://api.twitch.tv"
)
public interface TwitchAPIClient {

    @PostMapping(value = "/helix/eventsub/subscriptions", consumes = MediaType.APPLICATION_JSON_VALUE)
    DataList<TwitchEventSub> subscribeRequest(
            @RequestHeader("Client-Id")
            String clientID,
            @RequestHeader("Authorization")
            String authorization,
            @RequestBody TwitchEventSub request

    );

    @GetMapping(value = "/helix/eventsub/subscriptions", consumes = MediaType.APPLICATION_JSON_VALUE)
    DataList<TwitchEventSub> getSubscriptions(
            @RequestHeader("Client-Id")
            String clientID,
            @RequestHeader("Authorization")
            String authorization,
            @Nullable
            @RequestParam("subscription_id")
            String subscriptionId
    );

    @DeleteMapping(value = "/helix/eventsub/subscriptions", consumes = MediaType.APPLICATION_JSON_VALUE)
    DataList<TwitchEventSub> deleteSubscriptions(
            @RequestHeader("Client-Id")
            String clientID,
            @RequestHeader("Authorization")
            String authorization,
            @Nullable
            @RequestParam("subscription_id")
            String subscriptionId
    );

    @GetMapping("/helix/users")
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

}
