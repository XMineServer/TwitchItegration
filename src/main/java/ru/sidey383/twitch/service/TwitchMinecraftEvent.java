package ru.sidey383.twitch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sidey383.twitch.dto.twitch.RedemptionEvent;

@Service
@RequiredArgsConstructor
public class TwitchMinecraftEvent implements TwitchEventConsumer {

    private final MinecraftService minecraftService;

    private static final String WHITELIST_REWARD = "Попасть на minecraft сервер";
    private static final String MESSAGE_REWARD = "Отправить сообщение в майнкрафт";

    @Override
    public void handleRewardRedemption(RedemptionEvent event) {
        if (event.reward() != null) {
            var reward = event.reward();
            if (WHITELIST_REWARD.equals(reward.title()) && event.userInput() != null) {
                minecraftService.addToWhiteListSync(event.userInput());
            }
            if (MESSAGE_REWARD.equals(reward.title()) && event.userInput() != null) {
                minecraftService.sendMessageSync(event.userName(), event.userInput());
            }
        }
    }
}
