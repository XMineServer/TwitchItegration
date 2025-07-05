package ru.sidey383.twitch.webhook.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import ru.sidey383.twitch.webhook.dto.ChannelPointsRewardRedemptionSubscribe;
import ru.sidey383.twitch.webhook.dto.DataList;
import ru.sidey383.twitch.webhook.mapper.SubscribeMapper;
import ru.sidey383.twitch.webhook.service.TwitchChannelPointsSubscriptionService;

import javax.annotation.Nonnull;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TwitchChannelPointsSubscriptionFacade {

    private final TwitchChannelPointsSubscriptionService webhookSubscriptionService;
    private final SubscribeMapper subscribeMapper;

    public ChannelPointsRewardRedemptionSubscribe createChannelPointsRedemptionWebhook(@Nonnull Long userId) {
        log.info("Try to create ChannelPointsRewardRedemptionSubscribe fir user {}", userId);
        var values = webhookSubscriptionService.subscribeRewardRedemptionAdd(userId.toString(), null);
        return subscribeMapper.toChannelPointsRewardRedemptionSubscribe(values);
    }

    public DataList<ChannelPointsRewardRedemptionSubscribe> getChannelPointsRedemptionWebhooks(@Nonnull Long userId) {
        var values = webhookSubscriptionService.getRewardRedemptionAdd(userId.toString(), null);
        var dtoList = subscribeMapper.toChannelPointsRewardRedemptionSubscribeList(values);
        return new DataList<>(dtoList, dtoList.size());
    }

    public DataList<ChannelPointsRewardRedemptionSubscribe> deleteChannelPointsRedemptionWebhook(@Nonnull Long userId, String subscriptionId) {
        log.info("Try to delete ChannelPointsRewardRedemptionSubscribe fir user {} by id {}", userId, subscriptionId);
        var values = webhookSubscriptionService.unsubscribeRewardRedemptionAdd(userId.toString(), null, subscriptionId);
        var dtoList = subscribeMapper.toChannelPointsRewardRedemptionSubscribeList(values);
        return new DataList<>(dtoList, dtoList.size());
    }

}
