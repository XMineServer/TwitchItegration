package ru.sidey383.twitch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
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
            var answer = rcon.command("/whitelist add %s".formatted(user));
            log.info("WhilteList command result: {}", answer);
        } catch (IOException e) {
            log.error("Fail to send whilt list add command for player {}", user, e);
        } catch (AuthenticationException e) {
            log.error("RCON: Authentication fail", e);
        }
    }

    public void sendMessageSync(String user, String message) {
        try (Rcon rcon = getRconInstance()) {
            var messageComponent = Component.text("<%s> %s".formatted(user, message));
            var answer = rcon.command("/tellraw @a %s".formatted(messageComponent.toString()));
            log.info("Send message command result: {}", answer);
        } catch (IOException e) {
            log.error("Fail to send message for user {} with content {}", user, message, e);
        } catch (AuthenticationException e) {
            log.error("RCON: Authentication fail", e);
        }
    }

    public void subscribeMessageSync(String user, String channel) {
        try (Rcon rcon = getRconInstance()) {
            var messageComponent = Component.text("%s подписался на %s!".formatted(user, channel));
            var answer = rcon.command("/tellraw @a %s".formatted(messageComponent.toString()));
            log.info("Send subscribe message command result: {}", answer);
        } catch (IOException e) {
            log.error("Fail to send subscribe for user {} and channel {}", user, channel, e);
        } catch (AuthenticationException e) {
            log.error("RCON: Authentication fail", e);
        }
    }

}

