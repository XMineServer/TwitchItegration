package ru.sidey383.twitch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sidey383.twitch.model.TwitchWebhook;

public interface TwitchWebhookRepository extends JpaRepository<TwitchWebhook, Long> {
}
