package ru.sidey383.twitch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sidey383.twitch.model.Session;
import ru.sidey383.twitch.model.TwitchOAuth2User;
import ru.sidey383.twitch.repository.SessionRepository;
import ru.sidey383.twitch.repository.TwitchOAuth2UserRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final TwitchOAuth2UserRepository twitchOAuth2UserRepository;
    private final SessionRepository sessionRepository;

    public List<TwitchOAuth2User> getUsers() {
        return twitchOAuth2UserRepository.findAll();
    }

    public void setRoles(Long userId, Set<String> roles) {
        TwitchOAuth2User user = twitchOAuth2UserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Can't found user by clientId %s".formatted(userId)));
        user.setRoles(roles);
        twitchOAuth2UserRepository.save(user);
    }

    public Set<String> getAvailableRoles() {
        return Set.of("ADMIN", "STREAMER");
    }

    @Transactional
    public void logout(Session session) {
        sessionRepository.delete(session);
    }

}
