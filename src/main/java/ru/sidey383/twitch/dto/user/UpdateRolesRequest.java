package ru.sidey383.twitch.dto.user;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UpdateRolesRequest(
        @NotNull Set<String> roles
) {}
