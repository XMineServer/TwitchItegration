package ru.sidey383.twitch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sidey383.twitch.model.TwitchToken;
import ru.sidey383.twitch.model.TwitchUser;

import java.util.Optional;

public interface TwitchUserRepository extends JpaRepository<TwitchUser, Long> {

    Optional<TwitchUser> findByToken(TwitchToken token);

}
