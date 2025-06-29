package ru.sidey383.twitch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sidey383.twitch.dto.twitch.event.TwitchEventSubNotification;

@Slf4j
@Service
public class TwitchEventLogger implements TwitchNotificationConsumer {

    @Override
    public void handleNotification(TwitchEventSubNotification event) {
        log.info("Twitch event {}", event);
    }
}
