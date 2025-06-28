package ru.sidey383.twitch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("minecraft.rcon")
public record MinecraftRconProperties (
        String host,
        int port,
        String password
) {
}

