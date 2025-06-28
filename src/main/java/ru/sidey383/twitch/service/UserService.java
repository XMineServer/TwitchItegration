package ru.sidey383.twitch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sidey383.twitch.model.User;
import ru.sidey383.twitch.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String username, String password, Set<String> roles) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles);
        user.setRoles(Collections.singleton("USER"));  // Роль по умолчанию
        return userRepository.save(user);
    }

    @Transactional
    public User changePassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public User removeUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Can't found user by id %s".formatted(userId)));
        userRepository.delete(user);
        return user;
    }

    @Transactional
    public User updateUserRoles(Long userId, Set<String> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Can't found user by id %s".formatted(userId)));
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Transactional
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Can't found user by name %s".formatted(username)));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}