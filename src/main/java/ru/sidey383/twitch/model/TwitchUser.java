package ru.sidey383.twitch.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "twitch_user")
public class TwitchUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    private TwitchToken token;

    private Long userId;

    private String login;

    private String displayName;

    private String broadcastType;

    @Column(columnDefinition="TEXT")
    private String description;

    private String profileImageUrl;

    private String offlineImageUrl;

    private String email;

    private Instant createdAt;

    @Override
    public String toString() {
        return "TwitchUser{" +
                "id=" + id +
                ", userId=" + userId +
                ", login='" + login + '\'' +
                ", displayName='" + displayName + '\'' +
                ", broadcastType='" + broadcastType + '\'' +
                ", description='" + description + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", offlineImageUrl='" + offlineImageUrl + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
