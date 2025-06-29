package ru.sidey383.twitch.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.sidey383.twitch.dto.twitch.TwitchEventSubType;

import javax.annotation.Nonnull;

@Getter
@Setter
@Entity
@Table(name = "active_twitch_webhooks")
public class TwitchWebhook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(targetEntity = TwitchOAuth2User.class, optional = false)
    public TwitchOAuth2User owner;

    @Enumerated(EnumType.STRING)
    private TwitchEventSubType type;

    private boolean isActive;

    @Nonnull
    private String subscriptionId;

    private String conditionBroadcasterUserId;
    private String conditionModeratorUserId;
    private String conditionUserId;
    private String conditionUserLogin;
    private String conditionUserName;
    private String conditionRewardId;
    private String conditionRewardTitle;
    private String conditionRewardCost;
    private String conditionRewardPrompt;
    private String conditionStatus;
    private String conditionId;
    private String conditionType;

}
