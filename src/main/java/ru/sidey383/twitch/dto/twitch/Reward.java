package ru.sidey383.twitch.dto.twitch;

public record Reward(
        String id,
        String title,
        int cost,
        String prompt
) {}
