package ru.sidey383.twitch.dto.twitch;

public enum TwitchMessageType {
    WEBHOOK_CALLBACK_VERIFICATION("webhook_callback_verification"),
    NOTIFICATION("notification"),
    REVOCATION("revocation");

    private final String twitchValue;

    TwitchMessageType(String twitchValue) {
        this.twitchValue = twitchValue;
    }

    public String getTwitchValue() {
        return twitchValue;
    }

    public static TwitchMessageType fromValue(String value) {
        for (TwitchMessageType type : values()) {
            if (type.twitchValue.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown Twitch message type: " + value);
    }
}
