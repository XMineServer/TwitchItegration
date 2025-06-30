package ru.sidey383.twitch.webhook.dto;

import java.util.List;

public record DataList<T>(
        List<T> subscriptions,
        long size
) {
}
