package ru.sidey383.twitch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.sidey383.twitch.client.TwitchAPIClient;
import ru.sidey383.twitch.config.twitch.TwitchConfigurationProperties;
import ru.sidey383.twitch.dto.twitch.DataList;
import ru.sidey383.twitch.dto.twitch.TwitchEventSubType;
import ru.sidey383.twitch.dto.twitch.WebhookSubscription;
import ru.sidey383.twitch.dto.twitch.event.TwitchEventSub;
import ru.sidey383.twitch.dto.twitch.event.TwitchEventSubConditions;
import ru.sidey383.twitch.dto.twitch.event.TwitchEventSubTransport;
import ru.sidey383.twitch.model.TwitchOAuth2User;
import ru.sidey383.twitch.model.TwitchWebhook;
import ru.sidey383.twitch.repository.TwitchWebhookRepository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwitchWebhookSubscriptionService {

    private final TwitchAPIClient apiClient;
    private final TwitchWebhookRepository webhookRepository;
    private final TwitchConfigurationProperties twitchConfig;
    private final TwitchOAuthService oAuthService;
    private final ObjectMapper objectMapper;


    @EventListener(ApplicationReadyEvent.class)
    public void onLoad() {
        webhooksRevoke();
    }

    public void webhooksRevoke() {
        log.info("Start revoke all dead webhooks");
        Map<String, TwitchEventSub> activeSubscriptions = apiClient.getSubscriptions(
                        twitchConfig.clientId(),
                        oAuthService.getAppAuthorizationHeader(),
                        null
                ).getData()
                .stream()
                .collect(Collectors.toMap(
                        TwitchEventSub::id,
                        webhook -> webhook,
                        (existing, replacement) -> existing
                ));
        webhookRepository.findAll().forEach(webhook -> {
            if (webhook.getStatus().isNeedToAutoReactivateOnStartup()) {
                var activeSubscription = findInActive(activeSubscriptions, webhook);
                if (activeSubscription.isPresent()) {
                    updateData(webhook, activeSubscription.get());
                    webhook.setStatus(TwitchWebhook.ActiveStatus.ACTIVE);
                    webhookRepository.findBySubscriptionId(webhook.getSubscriptionId()).ifPresent(webhookRepository::delete);
                    webhookRepository.save(webhook);
                } else {
                    try {
                        revokeWebhook(webhook);
                    } catch (Exception e) {
                        log.warn("Failed to revoke webhook subscription for {}", webhook, e);
                    }
                }
            }
        });
        log.info("Complete revoke all dead webhooks");
    }

    private void updateData(TwitchWebhook webhook, TwitchEventSub twitchEventSub) {
        TwitchEventSubType.fromValue(twitchEventSub.type()).ifPresent(webhook::setType);
        webhook.setTwitchStatus(twitchEventSub.status());
        webhook.setSubscriptionId(twitchEventSub.id());
        webhook.setCallback(twitchEventSub.transport().callback());
        webhook.setConditionBroadcasterUserId(twitchEventSub.condition().broadcasterUserId());
        webhook.setConditionRewardId(twitchEventSub.condition().rewardId());
        //TODO: add other conditions when needed
    }

    public Optional<TwitchEventSub> findInActive(Map<String, TwitchEventSub> activeSubscriptions, TwitchWebhook webhook) {
        TwitchEventSub twitchEventSub = activeSubscriptions.get(webhook.getSubscriptionId());
        if (twitchEventSub != null) return Optional.of(twitchEventSub);
        return switch (webhook.getType()) {
            case CHANNEL_POINTS_REDEMPTION_ADD -> checkForSamePointsRedemptionAdd(activeSubscriptions, webhook);
            default -> Optional.empty();
        };
    }

    private Optional<TwitchEventSub> checkForSamePointsRedemptionAdd(Map<String, TwitchEventSub> activeSubscriptions, TwitchWebhook webhook) {
        return activeSubscriptions.values().stream()
                .filter(seubsribtion -> isSamePointsRedemptionAdd(seubsribtion, webhook))
                .findFirst();
    }

    private boolean isSamePointsRedemptionAdd(TwitchEventSub event, TwitchWebhook webhook) {
        if (!Objects.equals(event.type(), webhook.getType().getValue())) return false;
        if (!Objects.equals(event.transport().callback(), webhook.getCallback())) return false;
        if (!Objects.equals(event.condition().broadcasterUserId(), webhook.getConditionBroadcasterUserId())) return false;
        String eventReward = normalize(event.condition().rewardTitle());
        String webhookReward = normalize(webhook.getConditionRewardTitle());
        if (!Objects.equals(eventReward, webhookReward)) return false;
        return true;
    }

    private static String normalize(String s) {
        return (s == null || s.isEmpty()) ? "" : s;
    }

    public TwitchWebhook tryRevokeWebhook(TwitchWebhook webhook) {
        if (webhook.getStatus().isCanBeActivated()) return webhook;
        return revokeWebhook(webhook);
    }

    public TwitchWebhook revokeWebhook(TwitchWebhook webhook) {
        TwitchEventSub response;
        try {
            response = subscribeWithErrorCatches(webhook);
            if (response == null) {
                throw new IllegalStateException("Twitch returned nothing for revoke webhook: " + webhook);
            }
            webhook.setStatus(TwitchWebhook.ActiveStatus.PENDING_ACTIVATION);
            webhook.setTwitchStatus(response.status());
            webhook.setSubscriptionId(response.id());
            webhook.setCallback(response.transport().callback());
        } catch (Exception e) {
            webhook.setStatus(TwitchWebhook.ActiveStatus.PROBLEM);
            webhookRepository.save(webhook);
            throw e;
        }
        return webhookRepository.save(webhook);
    }

    public TwitchEventSub subscribeWithErrorCatches(TwitchWebhook webhook) {
        DataList<TwitchEventSub> data;
        try {
            webhook.setCallback(twitchConfig.webhookUri());
            data = apiClient.subscribeRequest(
                    twitchConfig.clientId(),
                    oAuthService.getAppAuthorizationHeader(),
                    TwitchEventSub.builder()
                            .type(webhook.getType().getValue())
                            .version(webhook.getType().getVersion())
                            .condition(createConditions(webhook))
                            .transport(createTransport(webhook.getCallback()))
                            .build()
            );
        } catch (FeignException.Conflict conflict) {
            log.warn("Conflict occurred while subscribing webhook: {}", webhook, conflict);
            return null;
        }
        if (data == null) return null;
        if (data.getData() == null) {
            log.error("Twitch returned null data for create webhook: {}", webhook);
            return null;
        }
        if (data.getData().isEmpty()) {
            log.error("Twitch returned empty data for create webhook: {}", webhook);
            return null;
        }
        return data.getData().getFirst();
    }

    public void onVerification(WebhookSubscription subscription) {
        var optWebhook = webhookRepository.findBySubscriptionId(subscription.id());
        if (optWebhook.isEmpty()) {
            throw new IllegalStateException("Received verification for unknown webhook subscription: " + subscription.id());
        } else {
            TwitchWebhook webhook = optWebhook.get();
            switch (webhook.getStatus()) {
                case PENDING_ACTIVATION, PROBLEM ->
                        log.info("Webhook subscription activated: {}", subscription.id());
                case ACTIVE, PENDING_DEACTIVATION ->
                        log.warn("Webhook in status {}, but received verification: {}", webhook.getStatus(), subscription);
                case DEACTIVATED, REVOKED -> {
                    log.error("Webhook in status {}, but received verification: {}", webhook.getStatus(), subscription);
                    throw new IllegalStateException("Webhook was deactivated, but received verification: " + webhook.getStatus());
                }
            }
            webhook.setTwitchStatus(subscription.status());
            webhook.setStatus(TwitchWebhook.ActiveStatus.ACTIVE);
            webhookRepository.save(webhook);
        }
    }

    public void onRevocation(WebhookSubscription subscription) {
        webhookRepository.findBySubscriptionId(subscription.id()).ifPresent(webhook -> {
            switch (webhook.getStatus()) {
                case ACTIVE, PENDING_ACTIVATION, PROBLEM -> webhook.setStatus(TwitchWebhook.ActiveStatus.REVOKED);
                case PENDING_DEACTIVATION -> webhook.setStatus(TwitchWebhook.ActiveStatus.DEACTIVATED);
                case REVOKED, DEACTIVATED -> {
                    log.warn("Webhook in status {}, but was revoked: {}", webhook.getStatus(), webhook);
                    webhook.setStatus(TwitchWebhook.ActiveStatus.REVOKED);
                }
            }
            webhook.setTwitchStatus(subscription.status());
            webhookRepository.save(webhook);
            log.info("Webhook subscription revoked: {}", subscription.id());
        });
    }

    public TwitchWebhook createChannelPointsRedemptionHook(@Nonnull TwitchOAuth2User owner) {
        return webhookRepository.findByOwnerAndTypeAndConditionBroadcasterUserId(
                        owner,
                        TwitchEventSubType.CHANNEL_POINTS_REDEMPTION_ADD,
                        owner.getBroadcastType()
                )
                //TODO check if exist
                .map(this::tryRevokeWebhook)
                .orElseGet(() -> {
                    TwitchWebhook webhook = TwitchWebhook.builder()
                            .callback(twitchConfig.webhookUri())
                            .type(TwitchEventSubType.CHANNEL_POINTS_REDEMPTION_ADD)
                            .owner(owner)
                            .conditionBroadcasterUserId(owner.getId().toString())
                            .build();
                    return initializeWebhook(webhook);
                });

    }

    public List<TwitchWebhook> getAllWebhooks(@Nonnull TwitchOAuth2User owner) {
        return webhookRepository.findByOwner(owner);
    }

    public TwitchWebhook initializeWebhook(
            @Nonnull TwitchWebhook webhook
    ) {
        webhook.setTwitchStatus(null);
        if (webhook.isNew()) {
            addWebhook(webhook);
        } else {
            throw new IllegalArgumentException("Webhook is not new, cannot initialize: " + webhook);
        }
        return webhookRepository.save(webhook);
    }

    public void unsubscribeWebhook(TwitchWebhook webhook) {
        try {
            if (webhook.getSubscriptionId() == null) return;
            try {
                apiClient.deleteSubscriptions(
                        twitchConfig.clientId(),
                        oAuthService.getAppAuthorizationHeader(),
                        webhook.getSubscriptionId()
                );
                webhook.setStatus(TwitchWebhook.ActiveStatus.DEACTIVATED);
            } catch (FeignException.NotFound notFound) {
                log.warn("Try to remove webhook subscription {}, but it was not found: {}", webhook, notFound.getMessage());
                webhook.setStatus(TwitchWebhook.ActiveStatus.DEACTIVATED);
            }
            webhook.setTwitchStatus(null);
            log.info("Webhook subscription removed: {}", webhook);
        } catch (FeignException e) {
            log.error("Failed to remove webhook subscription: {}", webhook, e);
            webhook.setStatus(TwitchWebhook.ActiveStatus.PROBLEM);
        } finally {
            webhookRepository.save(webhook);
        }

    }

    private void addWebhook(TwitchWebhook webhook) {
        try {
            TwitchEventSub twitchEventSub = TwitchEventSub.builder()
                    .type(webhook.getType().getValue())
                    .version(webhook.getType().getVersion())
                    .condition(createConditions(webhook))
                    .transport(createTransport(webhook.getCallback()))
                    .build();

            var dataList = apiClient.subscribeRequest(
                    twitchConfig.clientId(),
                    oAuthService.getAppAuthorizationHeader(),
                    twitchEventSub
            ).getData();
            if (dataList.isEmpty()) {
                log.error("Twitch returned nothing for create webhook: {}", twitchEventSub);
                webhook.setStatus(TwitchWebhook.ActiveStatus.PROBLEM);
                return;
            }
            TwitchEventSub response = dataList.getFirst();
            webhook.setSubscriptionId(response.id());
            webhook.setStatus(TwitchWebhook.ActiveStatus.PENDING_ACTIVATION);
        } catch (FeignException e) {
            log.error("Failed to create webhook subscription for: {}", webhook, e);
            webhook.setStatus(TwitchWebhook.ActiveStatus.PROBLEM);
        } finally {
            webhookRepository.save(webhook);
        }


    }

    private TwitchEventSubTransport createTransport(String callback) {
        return TwitchEventSubTransport.builder()
                .method("webhook")
                .callback(callback)
                .secret(twitchConfig.clientSecret())
                .build();
    }

    private TwitchEventSubConditions createConditions(TwitchWebhook webhook) {
        return TwitchEventSubConditions.builder()
                .broadcasterUserId(webhook.getConditionBroadcasterUserId())
                .moderatorUserId(webhook.getConditionModeratorUserId())
                .userId(webhook.getConditionUserId())
                .userLogin(webhook.getConditionUserLogin())
                .userName(webhook.getConditionUserName())
                .rewardId(webhook.getConditionRewardId())
                .rewardTitle(webhook.getConditionRewardTitle())
                .rewardCost(webhook.getConditionRewardCost())
                .rewardPrompt(webhook.getConditionRewardPrompt())
                .status(webhook.getConditionStatus())
                .id(webhook.getConditionId())
                .type(webhook.getConditionType())
                .build();
    }

    public void setChannelPointsRedemptionWebhookActive(@Nonnull TwitchOAuth2User owner, boolean enable) {
        webhookRepository.findByOwnerAndType(
                owner,
                TwitchEventSubType.CHANNEL_POINTS_REDEMPTION_ADD
        ).forEach(webhook -> {
            try {
                if (enable && !webhook.getStatus().isCanBeActivated()) {
                    addWebhook(webhook);
                } else if (!enable && webhook.getStatus().isCanBeDeactivated()) {
                    unsubscribeWebhook(webhook);
                }
                log.info("Successfully changed channel points redemption webhook active: {}, for webhook: {}", enable, webhook);
            } catch (Exception e) {
                log.error("Failed to change channel points redemption webhook active: {}", webhook, e);
            }
        });
    }

    public void deleteChannelPointsRedemptionWebhook(@Nonnull TwitchOAuth2User owner) {
        webhookRepository.findByOwnerAndType(
                owner,
                TwitchEventSubType.CHANNEL_POINTS_REDEMPTION_ADD
        ).forEach(webhook -> {
            try {
                if (webhook.getSubscriptionId() != null) {
                    unsubscribeWebhook(webhook);
                }
                webhookRepository.deleteById(webhook.getId());
                log.info("Successfully deleted channel points redemption webhook: {}", webhook);
            } catch (Exception e) {
                log.error("Failed to delete channel points redemption webhook: {}", webhook, e);
            }
        });
    }


}
