package ru.sidey383.twitch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sidey383.twitch.dto.twitch.TwitchEventSubType;
import ru.sidey383.twitch.model.TwitchOAuth2User;
import ru.sidey383.twitch.model.TwitchWebhook;

import java.util.List;
import java.util.Optional;

public interface TwitchWebhookRepository extends JpaRepository<TwitchWebhook, Long> {

    Optional<TwitchWebhook> findBySubscriptionId(String subscriptionId);

    Optional<TwitchWebhook> findByOwnerAndTypeAndConditionBroadcasterUserId(TwitchOAuth2User owner, TwitchEventSubType type, String conditionBroadcasterUserId);

    List<TwitchWebhook> findByOwnerAndType(TwitchOAuth2User owner, TwitchEventSubType type);

    List<TwitchWebhook> findByOwner(TwitchOAuth2User owner);

}
