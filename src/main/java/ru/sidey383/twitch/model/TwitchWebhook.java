package ru.sidey383.twitch.model;

import jakarta.persistence.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.sidey383.twitch.dto.twitch.TwitchEventSubType;

import java.util.Objects;

@Getter
@Setter
@Builder
@Entity
@Table(name = "active_twitch_webhooks")
@NoArgsConstructor
@AllArgsConstructor
public class TwitchWebhook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = TwitchOAuth2User.class, optional = false)
    public TwitchOAuth2User owner;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TwitchEventSubType type;

    @NotNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ActiveStatus status = ActiveStatus.DEACTIVATED;

    private String callback;

    @Nullable
    @Builder.Default
    private String twitchStatus = null;

    @Column(nullable = false, unique = true)
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

    @Override
    public String toString() {
        return "TwitchWebhook{" +
                "id=" + id +
                ", type=" + type +
                ", status=" + status +
                ", twitchStatus='" + twitchStatus + '\'' +
                ", subscriptionId='" + subscriptionId + '\'' +
                '}';
    }

    @NotNull
    public String getCallback() {
        return Objects.requireNonNullElse(callback, "https://twitch.sidey383.ru/webhook/twitch");
    }

    @NotNull
    public ActiveStatus getStatus() {
        return Objects.requireNonNullElse(status, ActiveStatus.PROBLEM);
    }

    public boolean isNew() {
        return id == null;
    }

    @Getter
    @RequiredArgsConstructor
    public enum ActiveStatus {
        /**
         * Send request to Twitch to deactivate subscription, but didn't receive confirmation yet
         * **/
        PENDING_DEACTIVATION(false, false, true),
        /**
         * Success deactivated by user, but still exists in database
         * **/
        DEACTIVATED(false, false, true),
        /**
         * Receive webhook events
         * **/
        ACTIVE(true, true, false),
        /**
         * Revoked by Twitch
         * **/
        REVOKED(true, false, true),
        /**
         * Subscribe event sub, but didn't receive verification callback yet
         * **/
        PENDING_ACTIVATION(false, true, false),
        PROBLEM(true, true, true);

        private final boolean canBeDeactivated;
        private final boolean needToAutoReactivateOnStartup;
        private final boolean canBeActivated;
    }

}
