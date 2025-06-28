package ru.sidey383.twitch.service;

import io.graversen.minecraft.rcon.MinecraftRcon;
import io.graversen.minecraft.rcon.RconResponse;
import io.graversen.minecraft.rcon.commands.WhiteListCommand;
import io.graversen.minecraft.rcon.commands.tellraw.TellRawCommandBuilder;
import io.graversen.minecraft.rcon.service.ConnectOptions;
import io.graversen.minecraft.rcon.service.MinecraftRconService;
import io.graversen.minecraft.rcon.service.RconDetails;
import io.graversen.minecraft.rcon.util.Colors;
import io.graversen.minecraft.rcon.util.Selectors;
import io.graversen.minecraft.rcon.util.Target;
import io.graversen.minecraft.rcon.util.WhiteListModes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sidey383.twitch.config.MinecraftRconProperties;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinecraftService {

    private MinecraftRconService rconService;
    private final MinecraftRconProperties rconProperties;

    private MinecraftRconService getRconInstance() {
        if (rconService != null && rconService.isConnected()) {
            return rconService;
        }
        RconDetails details = new RconDetails(rconProperties.host(), rconProperties.port(), rconProperties.password());
        MinecraftRconService svc = new MinecraftRconService(details, ConnectOptions.defaults());
        svc.connectBlocking(Duration.ofSeconds(5));
        return rconService = svc;
    }

    public void addToWhiteListSync(String user) {
        MinecraftRcon client = getRconInstance().minecraftRcon()
                .orElseThrow(() -> new IllegalStateException("Не удалось подключиться к RCON"));
        client.query(new WhiteListCommand(Target.player(user), WhiteListModes.ADD), (response) -> {
            log.info("WhilteList command result: {}", resultToString(response));
            return null;
        });
    }

    public void sendMessageSync(String user, String message) {
        MinecraftRcon client = getRconInstance().minecraftRcon()
                .orElseThrow(() -> new IllegalStateException("Не удалось подключиться к RCON"));
        var command = new TellRawCommandBuilder()
                .targeting(Selectors.ALL_PLAYERS)
                .withText("<%s> %s".formatted(user, message))
                .build();
        client.query(command, (response) -> {
            log.info("Send message command result: {}", resultToString(response));
            return null;
        });
    }

    public void subscribeMessageSync(String user, String channel) {
        MinecraftRcon client = getRconInstance().minecraftRcon()
                .orElseThrow(() -> new IllegalStateException("Не удалось подключиться к RCON"));
        var command = new TellRawCommandBuilder()
                .targeting(Selectors.ALL_PLAYERS)
                .withText("%s подписался на %s!".formatted(user, channel))
                .withColor(Colors.GOLD)
                .build();
        client.query(command, (response) -> {
            log.info("Send subscribe message command result: {}", resultToString(response));
            return null;
        });
    }

    private String resultToString(RconResponse response) {
        StringBuilder builder = new StringBuilder();
        builder.append("RCON Response #").append(response.getRequestCounter()).append(" (ID=").append(response.getResponseId()).append(") ");
        builder.append("Start: ").append(response.getRequestStart()).append(" ms, ");
        builder.append("End: ").append(response.getRequestEnd()).append(" ms, ");
        builder.append("Duration: ").append(response.getRequestDuration()).append(" ms ");
        builder.append("Payload: ");
        builder.append(response.getResponseString()).append(" ");
        return builder.toString();
    }

}

