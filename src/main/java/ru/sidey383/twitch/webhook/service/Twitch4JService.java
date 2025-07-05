package ru.sidey383.twitch.webhook.service;

import com.github.twitch4j.eventsub.EventSubSubscription;
import com.github.twitch4j.eventsub.EventSubSubscriptionStatus;
import com.github.twitch4j.eventsub.EventSubTransport;
import com.github.twitch4j.eventsub.EventSubTransportMethod;
import com.github.twitch4j.eventsub.condition.EventSubCondition;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionType;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.EventSubSubscriptionList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.sidey383.twitch.config.twitch.TwitchConfigurationProperties;
import ru.sidey383.twitch.dto.twitch.TwitchMessageType;
import ru.sidey383.twitch.service.TwitchOAuthService;
import ru.sidey383.twitch.webhook.event.TwitchSubEvent;
import ru.sidey383.twitch.webhook.model.TwitchEventSubSecret;
import ru.sidey383.twitch.webhook.repository.TwitchEventSubSecretRepository;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class Twitch4JService {

    private final SecureRandom secureRandom = new SecureRandom();
    private final TwitchHelix helix;
    private final TwitchConfigurationProperties twitchProperties;
    private final TwitchOAuthService oAuthService;
    private final TwitchEventSubSecretRepository eventSubSecretRepository;
    private final TwitchSubscriptionCacheService subscriptionCacheService;

    @EventListener(ContextRefreshedEvent.class)
    private void onLoad() {
        log.info("Twitch4JService is ready, loading existing subscriptions...");
        String cursor = null;
        long count = 0;
        do {
            var answer = helix.getEventSubSubscriptions(oAuthService.getAuthToken(), null, null, null, cursor, 100).execute();
            var subscriptions = answer.getSubscriptions();
            if (subscriptions.isEmpty()) break;

            count += getEventSubSubscriptions(subscriptions);

            var pagination = answer.getPagination();
            cursor = pagination != null ? pagination.getCursor() : null;

        } while (cursor != null);
        log.info("Twitch4JService load {} EventSub subscriptions", count);
    }

    private long getEventSubSubscriptions(List<EventSubSubscription> subscriptions) {
        return subscriptions.stream()
                .map(subscription -> {
                    if (isActual(subscription.getStatus())) {
                        var subInfo = eventSubSecretRepository.findById(subscription.getId());
                        if (subInfo.isPresent()) {
                            subscriptionCacheService.put(subscription);
                            return subscription;
                        } else {
                            log.warn("Unknown webhook subscription {}", subscription.getId());
                            return null;
                        }
                    } else {
                        try {
                            unsubscribe(subscription.getId());
                        } catch (Throwable t) {
                            log.error("Failed to unsubscribe from EventSub subscription with ID: {}", subscription.getId(), t);
                        }
                        return null;
                    }
                }).filter(Objects::nonNull).count();
    }

    public boolean isActual(EventSubSubscriptionStatus status) {
        return switch (status) {
            case ENABLED,
                 WEBHOOK_CALLBACK_VERIFICATION_PENDING,
                 WEBSOCKET_CONNECTION_UNUSED,
                 WEBSOCKET_RECEIVED_INBOUND_TRAFFIC -> true;
            case WEBHOOK_CALLBACK_VERIFICATION_FAILED,
                 NOTIFICATION_FAILURES_EXCEEDED,
                 AUTHORIZATION_REVOKED,
                 MODERATOR_REMOVED,
                 USER_REMOVED,
                 VERSION_REMOVED,
                 BETA_MAINTENANCE,
                 CHAT_USER_BANNED,
                 CONDUIT_DELETED,
                 WEBSOCKET_DISCONNECTED,
                 WEBSOCKET_FAILED_PING_PONG,
                 WEBSOCKET_FAILED_TO_RECONNECT,
                 WEBSOCKET_INTERNAL_ERROR,
                 WEBSOCKET_NETWORK_ERROR,
                 WEBSOCKET_NETWORK_TIMEOUT -> false;
        };
    }

    @EventListener
    private void onVerifyFai(TwitchSubEvent event) {
        if (!event.isVerified()) {
            unsubscribe(event.getNotification().getSubscription().getId());
        } else {
            if (event.getMessageType() == TwitchMessageType.REVOCATION) {
                unsubscribe(event.getNotification().getSubscription().getId());
            }
        }
    }


    /**
     * @see com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes
     **/
    @NotNull
    public <C extends EventSubCondition, B>
    EventSubSubscription subscribe(
            SubscriptionType<C, B, ?> type,
            Function<B, C> conditions
    ) {
        String secret = HexFormat.of().formatHex(secureRandom.generateSeed(40));
        EventSubSubscription postSubscription = type.prepareSubscription(
                conditions,
                EventSubTransport.builder()
                        .callback(twitchProperties.webhookUri())
                        .secret(secret)
                        .method(EventSubTransportMethod.WEBHOOK)
                        .build()
        );
        EventSubSubscriptionList list;
        try {
            list = helix.createEventSubSubscription(oAuthService.getAuthToken(), postSubscription).execute();
        } catch (Throwable t) {
            log.error("Fail to create subscription {}", type, t);
            throw t;
        }
        var subscriptions = list.getSubscriptions();
        if (subscriptions.isEmpty()) {
            log.error("Failed to create EventSub subscription: {}", postSubscription);
            throw new IllegalStateException("Failed to create EventSub subscription");
        }
        EventSubSubscription subscription = subscriptions.getFirst();
        try {
            eventSubSecretRepository.save(new TwitchEventSubSecret(subscription.getId(), secret));
        } catch (Throwable t) {
            log.error("Failed to save EventSub secret for subscription ID: {}", subscription.getId());
            unsubscribe(subscription.getId());
            throw t;
        } finally {
            log.info("Successfully created EventSub subscription: {}", subscription);
        }
        subscriptionCacheService.put(subscription);
        return subscription;
    }

    public void unsubscribe(String id) {
        log.info("Unsubscribing from EventSub subscription with ID: {}", id);
        try {
            helix.deleteEventSubSubscription(oAuthService.getAuthToken(), id).execute();
        } catch (Throwable t) {
            log.error("Failed to unsubscribe from EventSub subscription with ID: {}", id, t);
        }
        subscriptionCacheService.remove(id);
        eventSubSecretRepository.deleteById(id);
    }

}
