package ru.sidey383.twitch.controller;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.sidey383.twitch.model.EventRewardType;
import ru.sidey383.twitch.model.TwitchEventReward;
import ru.sidey383.twitch.model.TwitchOAuth2User;
import ru.sidey383.twitch.service.TwitchEventRewardService;
import ru.sidey383.twitch.service.TwitchRewardsService;
import ru.sidey383.twitch.webhook.facade.TwitchChannelPointsSubscriptionFacade;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/streamer")
@RequiredArgsConstructor
public class StreamerController {

    private final TwitchChannelPointsSubscriptionFacade channelPointsSubscriptionFacade;
    private final TwitchEventRewardService twitchEventRewardService;
    private final TwitchRewardsService twitchRewardsService;

    @GetMapping("/subscribe/channelPointsRewardRedemption")
    public String getChannelPointsRedemptionWebhooks(
            Model model,
            @AuthenticationPrincipal TwitchOAuth2User user
    ) {
        var subscribes = channelPointsSubscriptionFacade.getChannelPointsRedemptionWebhooks(user.getId());
        model.addAttribute("channelPointsRedemptionWebhooks", subscribes);
        return "streamer/components/channelPointsRewardRedemption :: webhookTable";
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
        model.addAttribute("channelPointsRedemptionWebhooks", channelPointsSubscriptionFacade.getChannelPointsRedemptionWebhooks(user.getId()));
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
