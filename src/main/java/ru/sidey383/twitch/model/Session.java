package ru.sidey383.twitch.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "sessions")
public class Session {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private TwitchOAuth2User user;

    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant expiresAt;
    @Column(nullable = false)
    private Instant lastSeenAt;

    @Override
    public String toString() {
        return "Session{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                ", lastSeenAt=" + lastSeenAt +
                '}';
    }
}