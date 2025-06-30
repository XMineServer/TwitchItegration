package ru.sidey383.twitch.controller;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sidey383.twitch.model.EventRewardType;
import ru.sidey383.twitch.model.TwitchEventReward;
import ru.sidey383.twitch.model.TwitchOAuth2User;
import ru.sidey383.twitch.service.TwitchEventRewardService;
import ru.sidey383.twitch.service.TwitchRewardsService;
import ru.sidey383.twitch.service.TwitchWebhookSubscriptionService;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/streamer")
@RequiredArgsConstructor
public class StreamerController {

    private final TwitchWebhookSubscriptionService webhookSubscriptionService;
    private final TwitchEventRewardService twitchEventRewardService;
    private final TwitchRewardsService twitchRewardsService;

    @GetMapping("/rewards")
    public String getRewards(
            Model model,
            @AuthenticationPrincipal TwitchOAuth2User user
    ) {
        var rewards = twitchRewardsService.getRewardDescriptions(user);
        model.addAttribute("rewards", rewards);
        model.addAttribute("events", twitchEventRewardService.getRewardEvents(user).stream().collect(
                Collectors.toMap(
                        TwitchEventReward::getRewardType,
                        TwitchEventReward::getRewardId,
                        (existing, replacement) -> existing
                )
        ));
        return "admin/streamer/rewards";
    }

    @GetMapping
    public String mainPage(
            Model model,
            @AuthenticationPrincipal TwitchOAuth2User user
    ) {
        var rewards = twitchRewardsService.getRewardDescriptions(user);
        model.addAttribute("rewards", rewards);
        model.addAttribute("events", twitchEventRewardService.getRewardEvents(user).stream().collect(
                Collectors.toMap(
                        TwitchEventReward::getRewardType,
                        TwitchEventReward::getRewardId,
                        (existing, replacement) -> existing
                )
        ));
        model.addAttribute("webhooks", webhookSubscriptionService.getAllWebhooks(user));
        return "streamer/index";
    }

    @PostMapping("/eventReward")
    public String setRewardEvent(
            @AuthenticationPrincipal TwitchOAuth2User user,
            @RequestParam @Nullable
            String rewardId,
            @RequestParam
            EventRewardType eventType
    ) {
        if (rewardId != null) {
            twitchEventRewardService.setRewardEvent(user, rewardId, eventType);
        } else {
            twitchEventRewardService.removeReward(user, eventType);
        }
        return "redirect:/streamer";
    }

}
