package ru.sidey383.twitch.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sidey383.twitch.dto.twitch.event.TwitchEventSubHeaders;
import ru.sidey383.twitch.dto.twitch.event.TwitchEventSubNotification;
import ru.sidey383.twitch.security.TwitchWebhookVerifier;
import ru.sidey383.twitch.service.TwitchNotificationConsumer;
import ru.sidey383.twitch.service.TwitchWebhookSubscriptionService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/webhook/twitch")
@RequiredArgsConstructor
public class TwitchWebhookController {
    private final Collection<TwitchNotificationConsumer> eventConsumers;
    private final TwitchWebhookVerifier verifier;
    private final ObjectMapper objectMapper;
    private final TwitchWebhookSubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<?> handleRewardRedemption(
            HttpServletRequest request,
            @RequestBody String rawBody,
            TwitchEventSubHeaders headers
    ) throws JsonProcessingException {
        if (!verifier.verifySignature(headers, rawBody)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var bodyValue = objectMapper.readValue(rawBody, TwitchEventSubNotification.class);
        switch (headers.messageType()) {
            case WEBHOOK_CALLBACK_VERIFICATION -> {
                subscriptionService.onVerification(bodyValue.subscription());
                return callbackVerification(bodyValue);
            }
            case NOTIFICATION -> eventConsumers.forEach(consumer -> consumer.handleNotification(bodyValue));
            case REVOCATION -> subscriptionService.onRevocation(bodyValue.subscription());
        }
        return ResponseEntity.ok().build();
    }

    private ResponseEntity<?> callbackVerification(TwitchEventSubNotification bodyValue) {
        log.info("New subscription verified: {}", bodyValue.subscription());
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(bodyValue.challenge());
    }
}