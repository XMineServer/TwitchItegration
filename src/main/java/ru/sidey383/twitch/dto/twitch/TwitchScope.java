package ru.sidey383.twitch.dto.twitch;

public enum TwitchScope {
    ANALYTICS_READ_EXTENSIONS("analytics:read:extensions"),
    ANALYTICS_READ_GAMES("analytics:read:games"),
    BITS_READ("bits:read"),
    CHANNEL_BROADCAST("channel:manage:broadcast"),
    CHANNEL_READ_HYPE_TRAIN("channel:read:hype_train"),
    CHANNEL_MANAGE_POLLS("channel:manage:polls"),
    CHANNEL_READ_POLLS("channel:read:polls"),
    CHANNEL_MANAGE_PREDICTIONS("channel:manage:predictions"),
    CHANNEL_READ_PREDICTIONS("channel:read:predictions"),
    /**
     * Управлять наградами
     * **/
    CHANNEL_MANAGE_REDEMPTIONS("channel:manage:redemptions"),
    /**
     * Читать награды
     * **/
    CHANNEL_READ_REDEMPTIONS("channel:read:redemptions"),
    CHANNEL_READ_SUBSCRIPTIONS("channel:read:subscriptions"),
    CHANNEL_MANAGE_VIPS("channel:manage:vips"),
    CHANNEL_READ_VIPS("channel:read:vips"),
    CHANNEL_MANAGE_SHOUTOUTS("channel:manage:shoutouts"),
    CHANNEL_MODERATE("channel:moderate"),
    CHANNEL_MODERATE_AUTOMOD_SETTINGS("moderator:manage:automod_settings"),
    CHANNEL_MODERATE_BANNED_USERS("moderator:manage:banned_users"),
    CHAT_READ("chat:read"),
    CHAT_EDIT("chat:edit"),
    USER_READ_EMAIL("user:read:email"),
    USER_READ_BROADCAST("user:read:broadcast"),
    USER_READ_FOLLOWS("user:read:follows"),
    USER_MANAGE_WHISPERS("user:manage:whispers"),
    CHANNEL_READ_CHATTERS("moderator:read:chatters"),
    MODERATOR_READ_FOLLOWERS("moderator:read:followers"),
    MODERATOR_MANAGE_CHAT_SETTINGS("moderator:manage:chat_settings"),
    MODERATOR_MANAGE_SHIELD_MODE("moderator:manage:shield_mode"),
    MODERATOR_MANAGE_WAR_NINGS("moderator:manage:warnings"),
    CHANNEL_READ_GOALS("channel:read:goals"),
    MODERATION_READ("moderation:read"),
    MODERATION_MANAGE_AUTOMOD("moderator:manage:automod"),
    CLIPS_EDIT("clips:edit");

    private final String value;

    TwitchScope(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

