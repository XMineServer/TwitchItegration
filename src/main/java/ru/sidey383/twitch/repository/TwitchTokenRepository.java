package ru.sidey383.twitch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sidey383.twitch.model.RefreshToken;
import ru.sidey383.twitch.model.TwitchToken;
import ru.sidey383.twitch.model.User;

import java.util.Optional;

public interface TwitchTokenRepository extends JpaRepository<TwitchToken, Long> {

    Optional<TwitchToken> findByOwner(User owner);

    void deleteByOwner(User owner);

}