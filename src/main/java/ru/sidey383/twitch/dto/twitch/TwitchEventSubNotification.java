package ru.sidey383.twitch.dto.twitch;

import jakarta.annotation.Nullable;

public record TwitchEventSubNotification(
        @Nullable
        String challenge,
        Subscription subscription,
        RedemptionEvent event
) {}