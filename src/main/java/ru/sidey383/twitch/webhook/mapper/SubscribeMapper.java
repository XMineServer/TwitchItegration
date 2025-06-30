package ru.sidey383.twitch.webhook.mapper;

import com.github.twitch4j.eventsub.EventSubSubscription;
import com.github.twitch4j.eventsub.condition.ChannelPointsCustomRewardRedemptionAddCondition;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import org.mapstruct.Mapper;
import ru.sidey383.twitch.webhook.dto.ChannelPointsRewardRedemptionSubscribe;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscribeMapper {

    default ChannelPointsRewardRedemptionSubscribe toChannelPointsRewardRedemptionSubscribe(EventSubSubscription subscription) {
        if (subscription.getType() != SubscriptionTypes.CHANNEL_POINTS_CUSTOM_REWARD_REDEMPTION_ADD) {
            throw new IllegalArgumentException("Subscription type is not CHANNEL_POINTS_CUSTOM_REWARD_REDEMPTION_ADD: " + subscription.getType());
        }
        ChannelPointsCustomRewardRedemptionAddCondition condition = (ChannelPointsCustomRewardRedemptionAddCondition) subscription.getCondition();
        return new ChannelPointsRewardRedemptionSubscribe(
                subscription.getId(),
                condition.getBroadcasterUserId(),
                condition.getRewardId()
        );
    }

    List<ChannelPointsRewardRedemptionSubscribe> toChannelPointsRewardRedemptionSubscribeList(List<EventSubSubscription> subscriptions);

}
