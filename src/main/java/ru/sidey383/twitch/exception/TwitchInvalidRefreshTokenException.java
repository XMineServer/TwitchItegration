package ru.sidey383.twitch.exception;

public class TwitchInvalidRefreshTokenException extends RuntimeException {
    public TwitchInvalidRefreshTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
