package ru.sidey383.twitch.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.sidey383.twitch.dto.user.RegisterRequest;
import ru.sidey383.twitch.dto.user.UpdateRolesRequest;
import ru.sidey383.twitch.dto.user.UserDTO;
import ru.sidey383.twitch.model.User;
import ru.sidey383.twitch.service.UserService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(
                request.username(),
                request.password(),
                request.roles() != null ? request.roles() : Set.of("USER")
        );
        return UserDTO.fromUser(user);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDTO removeUser(@PathVariable Long userId) {
        User user = userService.removeUser(userId);
        return UserDTO.fromUser(user);
    }

    @GetMapping
    public List<UserDTO> getUsers() {
        return userService.getAllUsers().stream()
                .map(UserDTO::fromUser)
                .toList();
    }

    @PatchMapping("/{userId}/roles")
    public UserDTO updateRoles(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateRolesRequest request
    ) {
        User user = userService.updateUserRoles(userId, request.roles());
        return UserDTO.fromUser(user);
    }
}