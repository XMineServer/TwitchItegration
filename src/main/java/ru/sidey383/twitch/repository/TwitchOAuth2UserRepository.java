package ru.sidey383.twitch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sidey383.twitch.model.TwitchOAuth2User;

public interface TwitchOAuth2UserRepository extends JpaRepository<TwitchOAuth2User, Long> {
}
