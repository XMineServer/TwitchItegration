package ru.sidey383.twitch.dto.twitch;

import ru.sidey383.twitch.model.TwitchToken;
import ru.sidey383.twitch.model.TwitchUser;

public record TwitchInfoDTO(
        TwitchTokenDTO token,
        TwitchUserDTO user
) {
    public static TwitchInfoDTO fromEntity(TwitchUser user, TwitchToken token) {
        return new TwitchInfoDTO(
                TwitchTokenDTO.fromEntity(token),
                TwitchUserDTO.fromEntity(user)
        );
    }
}
