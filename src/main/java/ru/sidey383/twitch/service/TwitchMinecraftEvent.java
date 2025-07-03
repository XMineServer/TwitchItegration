package ru.sidey383.twitch.service;

import com.github.twitch4j.eventsub.events.CustomRewardRedemptionAddEvent;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.sidey383.twitch.dto.twitch.TwitchMessageType;
import ru.sidey383.twitch.repository.TwitchEventRewardRepository;
import ru.sidey383.twitch.repository.TwitchOAuth2UserRepository;
import ru.sidey383.twitch.webhook.event.TwitchSubEvent;

@Service
@RequiredArgsConstructor
public class TwitchMinecraftEvent {

    private final MinecraftService minecraftService;
    private final TwitchEventRewardRepository twitchEventRewardRepository;
    private final TwitchOAuth2UserRepository twitchOAuth2UserRepository;


    @EventListener(TwitchSubEvent.class)
    public void handleNotification(TwitchSubEvent event) {
        if (!eventFiler(event)) return;
        var redemptionEvent = (CustomRewardRedemptionAddEvent) event.getNotification().getEvent();
        if (redemptionEvent == null) return;
        var reward = redemptionEvent.getReward();
        if (reward == null) return;
        var broadcasterOpt = twitchOAuth2UserRepository.findById(Long.valueOf(redemptionEvent.getBroadcasterUserId()));
        if (broadcasterOpt.isEmpty()) return;
        var broadcaster = broadcasterOpt.get();
        var eventOpt = twitchEventRewardRepository.findByOwnerAndRewardId(broadcaster, reward.getId());
        if (eventOpt.isEmpty()) return;
        var eventReward = eventOpt.get();
        switch (eventReward.getRewardType()) {
            case MINECRAFT_WHITELIST_REWARD -> {
                if (redemptionEvent.getUserInput() != null) {
                    minecraftService.addToWhiteListSync(redemptionEvent.getUserInput());
                }
            }
            case MINECRAFT_MESSAGE_REWARD -> {
                if (redemptionEvent.getUserInput() != null) {
                    minecraftService.sendMessageSync(redemptionEvent.getUserName(), redemptionEvent.getUserInput());
                }
            }
        }
    }

    public boolean eventFiler(TwitchSubEvent event) {
        return event.isVerified() &&
               event.getMessageType() == TwitchMessageType.NOTIFICATION &&
               event.getSubscriptionType() == SubscriptionTypes.CHANNEL_POINTS_CUSTOM_REWARD_REDEMPTION_ADD;
    }

}
