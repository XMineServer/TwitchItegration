package ru.sidey383.twitch.dto.twitch.event;

import jakarta.annotation.Nullable;
import ru.sidey383.twitch.dto.twitch.RedemptionEvent;
import ru.sidey383.twitch.dto.twitch.Subscription;

public record TwitchEventSubNotification(
        @Nullable
        String challenge,
        Subscription subscription,
        RedemptionEvent event
) {}