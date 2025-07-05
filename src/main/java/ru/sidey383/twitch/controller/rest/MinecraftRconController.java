package ru.sidey383.twitch.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.sidey383.twitch.service.MinecraftService;

@Slf4j
@RestController
@RequestMapping("/api/minecraft")
@RequiredArgsConstructor
public class MinecraftRconController {

    private final MinecraftService minecraftService;

    @PostMapping("/message")
    public void sendMessage(
            @RequestParam String user,
            @RequestParam String message
    ) {
        minecraftService.sendMessageSync(user, message);
    }

    @PostMapping("/subscribe")
    public void onSubscribe(
            @RequestParam String user,
            @RequestParam String channel
    ) {
        minecraftService.subscribeMessageSync(user, channel);
    }

    @PostMapping("/whitelist")
    public void onWhitelist(
            @RequestParam String user
    ) {
        minecraftService.addToWhiteListSync(user);
    }

}
