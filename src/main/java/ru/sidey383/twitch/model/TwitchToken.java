package ru.sidey383.twitch.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Stream;

@Getter
@Setter
@Entity
@Table(name = "twitch_tokens")
public class TwitchToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    public User owner;

    @Column(nullable = false)
    public String code;

    public String accessToken;

    public String refreshToken;

    public Instant expiresIn;

    public String tokenType;

    @Column(columnDefinition="TEXT")
    public String scope;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "token", cascade = CascadeType.REMOVE)
    public TwitchUser twitchUser;

    public Stream<String> getScopes() {
        return Arrays.stream(scope.split(" "));
    }

    public boolean isExpire(Instant now) {
        return now.isAfter(expiresIn);
    }

    @Override
    public String toString() {
        return "TwitchToken{" +
                "code='" + code + '\'' +
                ", id=" + id +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", tokenType='" + tokenType + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}
