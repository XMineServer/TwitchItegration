package ru.sidey383.twitch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.sidey383.twitch.client.TwitchAPIClient;
import ru.sidey383.twitch.config.twitch.TwitchConfigurationProperties;
import ru.sidey383.twitch.dto.twitch.TwitchEventSubType;
import ru.sidey383.twitch.dto.twitch.event.TwitchEventSub;
import ru.sidey383.twitch.dto.twitch.event.TwitchEventSubConditions;
import ru.sidey383.twitch.dto.twitch.event.TwitchEventSubTransport;
import ru.sidey383.twitch.model.TwitchWebhook;
import ru.sidey383.twitch.repository.TwitchWebhookRepository;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwitchWebhookSubscriptionService {

    private final TwitchAPIClient apiClient;
    private final TwitchWebhookRepository webhookRepository;
    private final TwitchConfigurationProperties twitchConfig;


    @EventListener(ApplicationReadyEvent.class)
    public void onLoad() {
        checkAliveWebhooks();
    }

    public void checkAliveWebhooks() {
        Map<String, TwitchEventSub> activeSubscriptions = apiClient.getSubscriptions(
                twitchConfig.id(),
                "Bearer " + twitchConfig.secret(),
                null
        ).getData()
                .stream()
                        .collect(Collectors.toMap(
                                TwitchEventSub::id,
                                webhook -> webhook,
                                (existing, replacement) -> existing
                        ));
        webhookRepository.findAll().forEach(webhook -> {
            if (webhook.isActive()) {
                TwitchEventSub activeSubscription = activeSubscriptions.get(webhook.getSubscriptionId());
                if (activeSubscription == null) {
                    try {
                        revokeWebhook(webhook);
                    } catch (Exception e) {
                        log.warn(
                                "Failed to revoke webhook subscription for {}: {}",
                                webhook.getType(),
                                e.getMessage()
                        );
                        webhook.setActive(false);
                        webhookRepository.save(webhook);
                    }
                }
            }
        });
    }

    private void revokeWebhook(TwitchWebhook webhook) {
        apiClient.subscribeRequest(
                twitchConfig.id(),
                "Bearer " + twitchConfig.secret(),
                TwitchEventSub.builder()
                        .type(webhook.getType().name())
                        .version("1")
                        .condition(createConditions(webhook))
                        .transport(createTransport())
                        .build()
        );
        webhook.setActive(true);
        webhookRepository.save(webhook);
    }

    private void addWebhook(TwitchEventSubType type, TwitchWebhook webhook) {
        TwitchEventSub twitchEventSub = TwitchEventSub.builder()
                .type(type.name())
                .version("1")
                .condition(createConditions(webhook))
                .transport(createTransport())
                .build();

        var dataList = apiClient.subscribeRequest(
                twitchConfig.id(),
                "Bearer " + twitchConfig.secret(),
                twitchEventSub
        ).getData();
        if (dataList.isEmpty()) {
            log.error("Failed to create webhook subscription for type: {}", type);
            return;
        }
        TwitchEventSub response = dataList.getFirst();
        webhook.setSubscriptionId(response.id());
        webhook.setActive(true);
        webhookRepository.save(webhook);

    }

    private TwitchEventSubTransport createTransport() {
        return TwitchEventSubTransport.builder()
                .method("webhook")
                .callback(twitchConfig.webhookUri())
                .secret(twitchConfig.secret())
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


}
