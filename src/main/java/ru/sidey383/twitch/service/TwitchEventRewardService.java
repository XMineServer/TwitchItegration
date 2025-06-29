package ru.sidey383.twitch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sidey383.twitch.model.EventRewardType;
import ru.sidey383.twitch.model.TwitchEventReward;
import ru.sidey383.twitch.model.TwitchOAuth2User;
import ru.sidey383.twitch.repository.TwitchEventRewardRepository;

@Service
@RequiredArgsConstructor
public class TwitchEventRewardService {

    private final TwitchEventRewardRepository twitchEventRewardRepository;

    @Transactional
    public TwitchEventReward addWhitelistEventReward(TwitchOAuth2User broadcaster, String rewardId, EventRewardType type) {
        var oldEventReward = twitchEventRewardRepository.findByOwnerAndRewardId(broadcaster, rewardId);
        if (oldEventReward.isPresent()) {
            throw new IllegalArgumentException("Reward with ID " + rewardId + " already used for broadcaster " + broadcaster.getLogin());
        }
        var reward = new TwitchEventReward();
        reward.setRewardId(rewardId);
        reward.setOwner(broadcaster);
        reward.setRewardType(type);
        return twitchEventRewardRepository.save(reward);
    }

    public void removeReward(TwitchOAuth2User broadcaster, String rewardId) {
        twitchEventRewardRepository.deleteByOwnerAndRewardId(broadcaster, rewardId);
    }

    public void removeReward(TwitchOAuth2User broadcaster, EventRewardType type) {
        twitchEventRewardRepository.deleteByOwnerAndRewardType(broadcaster, type);
    }

}
