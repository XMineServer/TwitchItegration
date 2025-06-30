package ru.sidey383.twitch.security;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sidey383.twitch.config.twitch.TwitchConfigurationProperties;
import ru.sidey383.twitch.dto.twitch.event.TwitchEventSubHeaders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Service
@AllArgsConstructor
public class TwitchWebhookVerifier {
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String HMAC_PREFIX = "sha256=";
    private final TwitchConfigurationProperties twitchConfig;

    public boolean verifySignature(
            TwitchEventSubHeaders headers, String rawBody
    ) {
        String message = getHmacMessage(headers, rawBody);
        String expectedSignature = HMAC_PREFIX + calculateHmac(message);
        return expectedSignature.equals(headers.signature());
    }

    private String getHmacMessage(TwitchEventSubHeaders headers, String rawBody) {
        return  headers.messageId() + headers.timestamp() + rawBody;
    }

    private String calculateHmac(String message) {
        try {
            Mac hmac = Mac.getInstance(HMAC_SHA256);
            hmac.init(new SecretKeySpec(twitchConfig.clientSecret().getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            byte[] hash = hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error calculating HMAC: " + e.getMessage(), e);
        }
    }
}
