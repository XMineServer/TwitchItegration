package ru.sidey383.twitch.dto.twitch;

import ru.sidey383.twitch.model.TwitchUser;

import java.time.Instant;

public record TwitchUserDTO(
        Long id,
        Long userId,
        String login,
        String displayName,
        String broadcastType,
        String description,
        String profileImageUrl,
        String offlineImageUrl,
        String email,
        Instant createdAt
) {
    public static TwitchUserDTO fromEntity(TwitchUser user) {
        if (user == null) return null;
        return new TwitchUserDTO(
                user.getId(),
                user.getUserId(),
                user.getLogin(),
                user.getDisplayName(),
                user.getBroadcastType(),
                user.getDescription(),
                user.getProfileImageUrl(),
                user.getOfflineImageUrl(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}

