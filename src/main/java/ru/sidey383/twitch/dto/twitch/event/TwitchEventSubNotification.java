package ru.sidey383.twitch.dto.twitch.event;

import jakarta.annotation.Nullable;
import ru.sidey383.twitch.dto.twitch.WebhookSubscription;

public record TwitchEventSubNotification(
        @Nullable
        String challenge,
        WebhookSubscription subscription,
        RedemptionEvent event
) {}