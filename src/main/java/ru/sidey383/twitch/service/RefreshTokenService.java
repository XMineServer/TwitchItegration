package ru.sidey383.twitch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sidey383.twitch.model.RefreshToken;
import ru.sidey383.twitch.model.User;
import ru.sidey383.twitch.repository.RefreshTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public String createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS));

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    @Transactional
    public User validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalStateException("Refresh token was expired");
        }

        return refreshToken.getUser();
    }

    @Transactional
    public String updateRefreshToken(String oldToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(oldToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        String newToken = UUID.randomUUID().toString();
        refreshToken.setToken(newToken);
        refreshToken.setExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS));

        refreshTokenRepository.save(refreshToken);
        return newToken;
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}