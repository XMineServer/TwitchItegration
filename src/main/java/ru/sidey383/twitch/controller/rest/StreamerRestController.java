package ru.sidey383.twitch.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.sidey383.twitch.model.TwitchOAuth2User;
import ru.sidey383.twitch.service.TwitchWebhookSubscriptionService;

@RestController
@RequestMapping("/streamer")
@RequiredArgsConstructor
//TODO: fix TwitchWebhookSubscriptionService. It work terribly badly and unstable
public class StreamerRestController {

    private final TwitchWebhookSubscriptionService webhookSubscriptionService;


    @PostMapping("/webhook/channelPointsRedemption")
    public ResponseEntity<Void> createChannelPointsRedemptionWebhook(
            @AuthenticationPrincipal TwitchOAuth2User user
    ) {
        webhookSubscriptionService.createChannelPointsRedemptionHook(user);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/webhook/channelPointsRedemption")
    public ResponseEntity<Void> patchChannelPointsRedemptionWebhook(
            @AuthenticationPrincipal TwitchOAuth2User user,
            @RequestParam boolean enable
    ) {
        webhookSubscriptionService.setChannelPointsRedemptionWebhookActive(user, enable);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/webhook/channelPointsRedemption")
    public ResponseEntity<Void> deleteChannelPointsRedemptionWebhook(
            @AuthenticationPrincipal TwitchOAuth2User user
    ) {
        webhookSubscriptionService.deleteChannelPointsRedemptionWebhook(user);
        return ResponseEntity.ok().build();
    }
}
