package ru.sidey383.twitch.webhook.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.sidey383.twitch.model.TwitchOAuth2User;
import ru.sidey383.twitch.webhook.dto.ChannelPointsRewardRedemptionSubscribe;
import ru.sidey383.twitch.webhook.dto.DataList;
import ru.sidey383.twitch.webhook.facade.TwitchChannelPointsSubscriptionFacade;

@RestController
@RequestMapping("/api/streamer/subscribe")
@RequiredArgsConstructor
public class TwitchSubscribeRestController {

    private final TwitchChannelPointsSubscriptionFacade channelPointsSubscriptionFacade;

    @PostMapping("/channelPointsRewardRedemption")
    public ResponseEntity<ChannelPointsRewardRedemptionSubscribe> createChannelPointsRedemptionSubscribe(
            @AuthenticationPrincipal TwitchOAuth2User user
    ) {
        var result = channelPointsSubscriptionFacade.createChannelPointsRedemptionWebhook(user.getId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/channelPointsRewardRedemption")
    public ResponseEntity<DataList<ChannelPointsRewardRedemptionSubscribe>> getChannelPointsRedemptionSubscribe(
            @AuthenticationPrincipal TwitchOAuth2User user
    ) {
        var result = channelPointsSubscriptionFacade.getChannelPointsRedemptionWebhooks(user.getId());
        return ResponseEntity.ok(result);
    }


    @DeleteMapping("/channelPointsRewardRedemption")
    public ResponseEntity<DataList<ChannelPointsRewardRedemptionSubscribe>> deleteChannelPointsRedemptionSubscribe(
            @AuthenticationPrincipal TwitchOAuth2User user,
            @RequestParam(required = false) String subscriptionId
    ) {
        var result = channelPointsSubscriptionFacade.deleteChannelPointsRedemptionWebhook(user.getId(), subscriptionId);
        return ResponseEntity.ok(result);
    }

}
