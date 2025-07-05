package ru.sidey383.twitch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.glavo.rcon.AuthenticationException;
import org.glavo.rcon.Rcon;
import org.springframework.stereotype.Service;
import ru.sidey383.twitch.config.MinecraftRconProperties;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinecraftService {

    private final MinecraftRconProperties rconProperties;

    private Rcon getRconInstance() throws IOException, AuthenticationException {
        return new Rcon(rconProperties.host(), rconProperties.port(), rconProperties.password());
    }

    public void addToWhiteListSync(String user) {
        try (Rcon rcon = getRconInstance()) {
            var answer = rcon.command("whitelist add %s".formatted(user));
            log.info("WhilteList command result: {}", answer);
        } catch (IOException e) {
            log.error("Fail to send whilt list add command for player {}", user, e);
        } catch (AuthenticationException e) {
            log.error("RCON: Authentication fail", e);
        }
    }

    public void sendMessageSync(String user, String message) {
        var messageComponent = Component.text("<%s> %s".formatted(user, message));
        tellRawAll(messageComponent);
    }

    public void subscribeMessageSync(String user, String channel) {

        var messageComponent = Component.textOfChildren(
                Component.text(user).color(TextColor.color(0x1DB954)),
                Component.text(" подписался на ").color(TextColor.color(0xB3B3B3)),
                Component.text(channel).color(TextColor.color(0xF7B32B))
        );
        tellRawAll(messageComponent);

    }

    public void tellRawAll(Component message) {
        try (Rcon rcon = getRconInstance()) {
            String rawMessage = JSONComponentSerializer.json().serialize(message);
            var answer = rcon.command("tellraw @a %s".formatted(rawMessage));
            log.info("Send tellraw command with result: {}", answer);
        } catch (IOException e) {
            log.error("Fail to send all tellraw command with message {}", message, e);
        } catch (AuthenticationException e) {
            log.error("RCON: Authentication fail", e);
        }
    }

}

