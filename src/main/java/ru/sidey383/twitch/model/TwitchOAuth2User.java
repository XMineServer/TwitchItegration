package ru.sidey383.twitch.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Entity
@Table(name = "twitch_oauth2_user")
public class TwitchOAuth2User implements OAuth2User {

    /**
     * User id from Twitch API.
     * **/
    @Id
    private Long id;

    private String login;

    private String displayName;

    private String broadcastType;

    public String accessToken;

    public String refreshToken;

    public Instant expiresIn;

    @Column(columnDefinition="TEXT")
    public String scope;

    @Column(columnDefinition="TEXT")
    private String description;

    private String profileImageUrl;

    private String offlineImageUrl;

    private String email;

    private Instant createdAt;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TwitchWebhook> webhooks;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of(
                "id", id,
                "login", login,
                "displayName", displayName,
                "broadcastType", broadcastType,
                "description", description,
                "profileImageUrl", profileImageUrl,
                "offlineImageUrl", offlineImageUrl,
                "email", email,
                "createdAt", createdAt
        );
    }

    public Stream<String> getScopes() {
        return Arrays.stream(scope.split(" "));
    }

    public boolean isExpire(Instant now, Duration stillAvailable) {
        return now.plus(stillAvailable).isAfter(expiresIn);
    }

    public boolean isExpire(Instant now) {
        return isExpire(now, Duration.of(5, ChronoUnit.SECONDS));
    }

    public boolean isExpire() {
        return isExpire(Instant.now());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return login;
    }
}
