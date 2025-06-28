package ru.sidey383.twitch.client;

import jakarta.annotation.Nullable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sidey383.twitch.config.JsonFeignConfig;
import ru.sidey383.twitch.dto.twitch.DataList;
import ru.sidey383.twitch.dto.twitch.TwitchEventSubRequest;
import ru.sidey383.twitch.dto.twitch.TwitchUserResponse;


@FeignClient(
        name = "twitchAPIClient",
        url = "https://api.twitch.tv",
        configuration = JsonFeignConfig.class
)
public interface TwitchAPIClient {

    @PostMapping(value = "/helix/eventsub/subscriptions", consumes = MediaType.APPLICATION_JSON_VALUE)
    void subscribeRequest(
            @RequestHeader("Client-Id") String clientID,
            @RequestHeader("Authorization") String authorization,
            @RequestBody TwitchEventSubRequest request

    );

    @GetMapping("/helix/users")
    DataList<TwitchUserResponse> getUserInfo(
            @RequestHeader("Authorization") String bearer,
            @RequestHeader("Client-Id") String clientId,
            @Nullable
            @RequestParam("id") Long id,
            @Nullable
            @RequestParam("login") String login
    );

}
