package ru.sidey383.twitch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sidey383.twitch.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}