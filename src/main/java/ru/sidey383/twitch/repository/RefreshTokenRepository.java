package ru.sidey383.twitch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sidey383.twitch.model.RefreshToken;
import ru.sidey383.twitch.model.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUser(User user);

    Optional<RefreshToken> findByToken(String token);

    void deleteByToken(String token);

}