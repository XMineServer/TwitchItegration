package ru.sidey383.twitch.dto.twitch.event;

public record TwitchEventSubReward(
        String id,
        String title,
        int cost,
        String prompt
) {}
