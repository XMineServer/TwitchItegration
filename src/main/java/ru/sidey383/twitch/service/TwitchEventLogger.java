package ru.sidey383.twitch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.sidey383.twitch.webhook.event.TwitchSubEvent;

@Slf4j
@Service
public class TwitchEventLogger {

    @EventListener(TwitchSubEvent.class)
    public void handleNotification(TwitchSubEvent event) {
        log.info("Twitch event {}", event);
    }
}
