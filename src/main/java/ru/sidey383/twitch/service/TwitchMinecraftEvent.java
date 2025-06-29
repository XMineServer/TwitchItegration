package ru.sidey383.twitch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sidey383.twitch.dto.twitch.RedemptionEvent;
import ru.sidey383.twitch.dto.twitch.event.TwitchEventSubNotification;
import ru.sidey383.twitch.repository.TwitchEventRewardRepository;
import ru.sidey383.twitch.repository.TwitchOAuth2UserRepository;

@Service
@RequiredArgsConstructor
public class TwitchMinecraftEvent implements TwitchNotificationConsumer {

    private final MinecraftService minecraftService;
    private final TwitchEventRewardRepository twitchEventRewardRepository;
    private final TwitchOAuth2UserRepository twitchOAuth2UserRepository;

    @Override
    public void handleNotification(TwitchEventSubNotification event) {
        RedemptionEvent redemptionEvent = event.event();
        if (redemptionEvent == null) return;
        if (redemptionEvent.reward() == null) return;
        var reward = redemptionEvent.reward();
        var broadcasterOpt = twitchOAuth2UserRepository.findById(redemptionEvent.broadcasterId());
        if (broadcasterOpt.isEmpty()) return;
        var broadcaster = broadcasterOpt.get();
        var eventOpt = twitchEventRewardRepository.findByOwnerAndRewardId(broadcaster, reward.id());
        if (eventOpt.isEmpty()) return;
        var eventReward = eventOpt.get();
        switch (eventReward.getRewardType()) {
            case MINECRAFT_WHITELIST_REWARD -> {
                if (redemptionEvent.userInput() != null) {
                    minecraftService.addToWhiteListSync(redemptionEvent.userInput());
                }
            }
            case MINECRAFT_MESSAGE_REWARD -> {
                if (redemptionEvent.userInput() != null) {
                    minecraftService.sendMessageSync(redemptionEvent.userName(), redemptionEvent.userInput());
                }
            }
        }
    }

}
