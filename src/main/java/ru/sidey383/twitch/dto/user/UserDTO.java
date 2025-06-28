package ru.sidey383.twitch.dto.user;

import ru.sidey383.twitch.model.User;

import java.util.Set;

public record UserDTO(
        Long id,
        String username,
        Set<String> roles
) {
    public static UserDTO fromUser(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getRoles());
    }
}
