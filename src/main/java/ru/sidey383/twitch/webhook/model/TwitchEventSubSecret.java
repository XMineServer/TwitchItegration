package ru.sidey383.twitch.webhook.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(schema="twitch_eventsub_secret")
@NoArgsConstructor
public class TwitchEventSubSecret {
    @Id
    private String subscriptionId;
    @Column(nullable = false)
    private String secret;
    @CreationTimestamp
    private Instant createdAt;

    public TwitchEventSubSecret(String subscriptionId, String secret) {
        this.subscriptionId = subscriptionId;
        this.secret = secret;
    }
}
