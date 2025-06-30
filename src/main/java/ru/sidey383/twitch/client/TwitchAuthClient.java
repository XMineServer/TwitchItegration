package ru.sidey383.twitch.client;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.sidey383.twitch.config.twitch.FeignFormConfig;
import ru.sidey383.twitch.dto.twitch.TwitchTokenResponse;

@FeignClient(
        name = "twitchAuthClient",
        url = "https://id.twitch.tv/oauth2",
        configuration = FeignFormConfig.class
)
public interface TwitchAuthClient {

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Headers("Content-Type: application/x-www-form-urlencoded")
    TwitchTokenResponse exchange(@RequestBody MultiValueMap<String, String> form);

    default TwitchTokenResponse token(String clientId, String clientSecret, String code, String redirectUri) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("code", code);
        form.add("grant_type", "authorization_code");
        form.add("redirect_uri", redirectUri);
        return exchange(form);
    }

    default TwitchTokenResponse appToken(String clientId, String clientSecret) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("grant_type", "client_credentials");
        return exchange(form);
    }

    default TwitchTokenResponse refresh(String clientId, String clientSecret, String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", refreshToken);
        return exchange(form);
    }

}
