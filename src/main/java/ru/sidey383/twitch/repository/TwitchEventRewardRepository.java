package ru.sidey383.twitch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sidey383.twitch.model.EventRewardType;
import ru.sidey383.twitch.model.TwitchEventReward;
import ru.sidey383.twitch.model.TwitchOAuth2User;

import java.util.List;
import java.util.Optional;

public interface TwitchEventRewardRepository extends JpaRepository<TwitchEventReward, Long> {

    List<TwitchEventReward> findByOwner(TwitchOAuth2User oAuth2User);

    Optional<TwitchEventReward> findByOwnerAndRewardType(TwitchOAuth2User oAuth2User, EventRewardType rewardType);

    Optional<TwitchEventReward> findByOwnerAndRewardId(TwitchOAuth2User oAuth2User, String rewardId);

    long deleteByOwnerAndRewardId(TwitchOAuth2User oAuth2User, String rewardId);

    long deleteByOwnerAndRewardType(TwitchOAuth2User oAuth2User, EventRewardType type);
}
