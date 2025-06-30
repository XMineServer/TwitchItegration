package ru.sidey383.twitch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sidey383.twitch.model.EventRewardType;
import ru.sidey383.twitch.model.TwitchEventReward;
import ru.sidey383.twitch.model.TwitchOAuth2User;
import ru.sidey383.twitch.repository.TwitchEventRewardRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TwitchEventRewardService {

    private final TwitchEventRewardRepository twitchEventRewardRepository;



    @Transactional
    public TwitchEventReward setRewardEvent(TwitchOAuth2User broadcaster, String rewardId, EventRewardType type) {
        var useOfReward = twitchEventRewardRepository.findByOwnerAndRewardId(broadcaster, rewardId);
        if (useOfReward.isPresent()) {
            if (useOfReward.get().getRewardType() == type) return useOfReward.get();
            throw new IllegalArgumentException("Reward with ID " + rewardId + " already used for broadcaster " + broadcaster.getLogin());
        }
        var existingReward = twitchEventRewardRepository.findByOwnerAndRewardType(broadcaster, type);
        var reward = existingReward.orElseGet(TwitchEventReward::new);
        reward.setRewardId(rewardId);
        reward.setOwner(broadcaster);
        reward.setRewardType(type);
        return twitchEventRewardRepository.save(reward);
    }

    public void removeReward(TwitchOAuth2User broadcaster, String rewardId) {
        twitchEventRewardRepository.deleteByOwnerAndRewardId(broadcaster, rewardId);
    }

    public List<TwitchEventReward> getRewardEvents(TwitchOAuth2User broadcaster) {
        return twitchEventRewardRepository.findByOwner(broadcaster);
    }

    public void removeReward(TwitchOAuth2User broadcaster, EventRewardType type) {
        twitchEventRewardRepository.deleteByOwnerAndRewardType(broadcaster, type);
    }

}
