package ru.sidey383.twitch.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;

@Getter
@Setter
@Entity
@Table(name = "twitch_event_rewards",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"owner", "rewardType"}),
                @UniqueConstraint(columnNames = {"owner", "rewardId"})
        }
)
public class TwitchEventReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(targetEntity = TwitchOAuth2User.class, optional = false, fetch = FetchType.LAZY)
    public TwitchOAuth2User owner;

    @Nonnull
    @Enumerated(EnumType.STRING)
    public EventRewardType rewardType;

    @Nonnull
    public String rewardId;

}
