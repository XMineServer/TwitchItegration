package ru.sidey383.twitch.service;

import ru.sidey383.twitch.dto.twitch.RedemptionEvent;

public interface TwitchEventConsumer {

    void handleRewardRedemption(RedemptionEvent event);

}
