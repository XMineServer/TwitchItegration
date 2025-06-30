package ru.sidey383.twitch.webhook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sidey383.twitch.webhook.model.TwitchEventSubSecret;

public interface TwitchEventSubSecretRepository extends JpaRepository<TwitchEventSubSecret, String> {
}
