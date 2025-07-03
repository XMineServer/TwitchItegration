package ru.sidey383.twitch.webhook.controller;

import com.github.twitch4j.common.util.TypeConvert;
import com.github.twitch4j.eventsub.EventSubNotification;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sidey383.twitch.dto.twitch.TwitchMessageType;
import ru.sidey383.twitch.dto.twitch.TwitchEventSubHeaders;
import ru.sidey383.twitch.security.TwitchWebhookVerifier;
import ru.sidey383.twitch.webhook.event.TwitchSubEvent;
import ru.sidey383.twitch.webhook.repository.TwitchEventSubSecretRepository;

@Slf4j
@RestController
@RequestMapping("/webhook/twitch")
@RequiredArgsConstructor
public class TwitchWebhookController {
    private final ApplicationEventPublisher eventPublisher;
    private final TwitchEventSubSecretRepository subSecretRepository;

    @PostMapping
    public ResponseEntity<?> handleRewardRedemption(
            HttpServletRequest request,
            @RequestBody String body,
            TwitchEventSubHeaders headers
    ) {
        var event = TypeConvert.jsonToObject(body, EventSubNotification.class);
        boolean verified = verify(headers, body, event);

        TwitchSubEvent springEvent = new TwitchSubEvent(this, event, headers, verified);

        log.debug("Received Twitch EventSub notification: {}", event);

        eventPublisher.publishEvent(springEvent);

        if (!verified) {
            log.warn("Failed to verify Twitch EventSub notification: {}", event);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        if (headers.messageType() == TwitchMessageType.WEBHOOK_CALLBACK_VERIFICATION) {
            return callbackVerification(event);
        } else {
            return ResponseEntity.ok().build();
        }
    }

    private boolean verify(TwitchEventSubHeaders headers, String rawBody, EventSubNotification notification) {
        return subSecretRepository.findById(notification.getSubscription().getId())
                .map(secret -> TwitchWebhookVerifier.verifySignature(headers, rawBody, secret.getSecret()))
                .orElse(false);
    }

    private ResponseEntity<?> callbackVerification(EventSubNotification event) {
        log.info("New subscription verified: {}", event.getSubscription());
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(event.getChallenge());
    }

}