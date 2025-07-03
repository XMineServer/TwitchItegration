package ru.sidey383.twitch.security;

import ru.sidey383.twitch.dto.twitch.TwitchEventSubHeaders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

public final class TwitchWebhookVerifier {
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String HMAC_PREFIX = "sha256=";

    private TwitchWebhookVerifier() {}

    public static boolean verifySignature(
            TwitchEventSubHeaders headers, String rawBody, String secret
    ) {
        String message = getHmacMessage(headers, rawBody);
        String expectedSignature = HMAC_PREFIX + calculateHmac(message, secret);
        return expectedSignature.equals(headers.signature());
    }

    private static String getHmacMessage(TwitchEventSubHeaders headers, String rawBody) {
        return  headers.messageId() + headers.timestamp() + rawBody;
    }

    private static String calculateHmac(String message, String secret) {
        try {
            Mac hmac = Mac.getInstance(HMAC_SHA256);
            hmac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            byte[] hash = hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating HMAC: " + e.getMessage(), e);
        }
    }
}
