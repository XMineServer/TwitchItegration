package ru.sidey383.twitch.webhook.service;

import com.github.twitch4j.eventsub.EventSubSubscription;
import com.github.twitch4j.eventsub.condition.ChannelPointsCustomRewardRedemptionAddCondition;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import ru.sidey383.twitch.utils.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwitchChannelPointsSubscriptionService {

    private final Twitch4JService twitch4JService;
    private final TwitchSubscriptionCacheService subscriptionCacheService;

    public EventSubSubscription subscribeRewardRedemptionAdd(
            @NotNull
            String broadcasterId,
            @Nullable
            String rewardId
    ) {
        var sub = subscriptionCacheService.findByTypeAndConditionFilter(
                SubscriptionTypes.CHANNEL_POINTS_CUSTOM_REWARD_REDEMPTION_ADD,
                (cond) -> compare(cond, broadcasterId, rewardId)
        );
        if (!sub.isEmpty()) return sub.getFirst();
        return twitch4JService.subscribe(
                SubscriptionTypes.CHANNEL_POINTS_CUSTOM_REWARD_REDEMPTION_ADD,
                conditions -> {
                    conditions.broadcasterUserId(broadcasterId);
                    if (rewardId != null) {
                        conditions.rewardId(rewardId);
                    }
                    return conditions.build();
                }

        );
    }

    public List<EventSubSubscription> unsubscribeRewardRedemptionAdd(
            @NotNull
            String broadcasterId,
            @Nullable
            String rewardId,
            String subscriptionId
    ) {
        List<EventSubSubscription> subscriptions = subscriptionCacheService.findByTypeAndConditionFilter(
                SubscriptionTypes.CHANNEL_POINTS_CUSTOM_REWARD_REDEMPTION_ADD,
                (cond) -> compare(cond, broadcasterId, rewardId)
        );
        if (subscriptionId != null) {
            subscriptions = subscriptions.stream().filter(
                    sub -> sub.getId().equals(subscriptionId)
            ).toList();
        }
        for (var sub : subscriptions) {
            try {
                twitch4JService.unsubscribe(sub.getId());
                subscriptionCacheService.remove(sub.getId());
            } catch (Exception e) {
                log.error(
                        "Failed to unsubscribe from CHANNEL_POINTS_CUSTOM_REWARD_REDEMPTION_ADD for broadcasterId: {} and rewardId: {}. Subscription ID: {}",
                        broadcasterId,
                        rewardId,
                        sub.getId(),
                        e
                );
            }
        }
        return subscriptions;
    }

    public List<EventSubSubscription> getRewardRedemptionAdd(
            @NotNull
            String broadcasterId,
            @Nullable
            String rewardId
    ) {
        return subscriptionCacheService.findByTypeAndConditionFilter(
                SubscriptionTypes.CHANNEL_POINTS_CUSTOM_REWARD_REDEMPTION_ADD,
                (cond) -> compare(cond, broadcasterId, rewardId)
        );
    }

    public boolean compare(
            ChannelPointsCustomRewardRedemptionAddCondition condition,
            @NotNull
            String broadcasterId,
            @Nullable
            String rewardId
    ) {
        return StringUtils.normalizeEquals(condition.getBroadcasterUserId(), broadcasterId) &&
                StringUtils.normalizeEquals(condition.getRewardId(), rewardId);
    }

}
