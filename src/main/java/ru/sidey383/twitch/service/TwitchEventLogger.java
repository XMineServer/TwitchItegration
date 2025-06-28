package ru.sidey383.twitch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sidey383.twitch.dto.twitch.RedemptionEvent;

@Slf4j
@Service
public class TwitchEventLogger implements TwitchEventConsumer {
    @Override
    public void handleRewardRedemption(RedemptionEvent event) {
        log.info("Twitch event {}", event);
    }
}
