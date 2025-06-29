package ru.sidey383.twitch.service;

import ru.sidey383.twitch.dto.twitch.event.TwitchEventSubNotification;

public interface TwitchNotificationConsumer {

    void handleNotification(TwitchEventSubNotification event);

}
