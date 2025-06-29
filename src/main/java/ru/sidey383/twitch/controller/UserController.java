package ru.sidey383.twitch.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.sidey383.twitch.dto.user.UpdateRolesRequest;
import ru.sidey383.twitch.model.TwitchOAuth2User;
import ru.sidey383.twitch.repository.TwitchOAuth2UserRepository;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final TwitchOAuth2UserRepository twitchOAuth2UserRepository;

    @PatchMapping("/{userId}/roles")
    public void updateRoles(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateRolesRequest request
    ) {
        TwitchOAuth2User user = twitchOAuth2UserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Can't found user by id %s".formatted(userId)));
        Set<String> roles = request.roles();
        user.setRoles(roles);
        twitchOAuth2UserRepository.save(user);
    }
}