package ru.sidey383.twitch.webhook.event;

import com.github.twitch4j.eventsub.EventSubNotification;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.sidey383.twitch.dto.twitch.TwitchMessageType;
import ru.sidey383.twitch.dto.twitch.TwitchEventSubHeaders;

@Getter
public class TwitchSubEvent extends ApplicationEvent {

    protected final EventSubNotification notification;
    private final TwitchEventSubHeaders headers;
    private final boolean verified;

    public TwitchSubEvent(Object source, EventSubNotification notification, TwitchEventSubHeaders headers, boolean verified) {
        super(source);
        this.notification = notification;
        this.headers = headers;
        this.verified = verified;
    }

    public TwitchMessageType getMessageType() {
        return headers.messageType();
    }

    public SubscriptionType<?, ?, ?> getSubscriptionType() {
        return notification.getSubscription().getType();
    }

}
