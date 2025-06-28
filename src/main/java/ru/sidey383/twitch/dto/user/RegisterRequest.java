package ru.sidey383.twitch.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record RegisterRequest(
        @NotBlank String username,
        @Size(min = 6) String password,
        Set<String> roles
) {}
