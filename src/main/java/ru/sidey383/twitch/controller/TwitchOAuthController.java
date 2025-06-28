package ru.sidey383.twitch.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.sidey383.twitch.config.TwitchConfigurationProperties;
import ru.sidey383.twitch.dto.twitch.TwitchInfoDTO;
import ru.sidey383.twitch.model.User;
import ru.sidey383.twitch.service.TwitchService;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/oauth2/twitch")
@RequiredArgsConstructor
public class TwitchOAuthController {
    private final TwitchConfigurationProperties twitchConfigurationProperties;
    private final TwitchService twitchService;

    @GetMapping("/callback")
    public ResponseEntity<?> twitchCallback(
            HttpServletRequest request,
            @AuthenticationPrincipal User user,
            @RequestParam String code
    ) {
        var twitchUser = twitchService.acceptCode(user, code)
                .flatMap(twitchService::loadUser);
        twitchUser.ifPresent(twitchService::subscribeRewards);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(twitchConfigurationProperties.callbackRedirectUri() + "?success=" + twitchUser.isPresent()))
                .build();
    }

    @GetMapping("/info")
    public ResponseEntity<TwitchInfoDTO> myTwitchInfo(
            @AuthenticationPrincipal User user
    ) {
        var token = twitchService.getTwitchToken(user);
        var twitchUser = twitchService.getTwitchUser(user);
        return ResponseEntity.ok(TwitchInfoDTO.fromEntity(twitchUser.orElse(null), token.orElse(null)));
    }

    @PostMapping("/profile/update")
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal User user
    ) {
        var token = twitchService.getTwitchToken(user)
                .orElseThrow(() ->
                        new IllegalStateException("Can't found token")
                );
        twitchService.loadUser(token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Void> updateSubscribe(
            @AuthenticationPrincipal User user
    ) {
        var twitchUser = twitchService.getTwitchUser(user)
                .orElseThrow(() ->
                        new IllegalStateException("Can't found token")
                );
        twitchService.subscribeRewards(twitchUser);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
