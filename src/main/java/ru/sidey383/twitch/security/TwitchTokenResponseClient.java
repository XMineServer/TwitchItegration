package ru.sidey383.twitch.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Controller
public class TwitchTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private static final Logger log = LogManager.getLogger(TwitchTokenResponseClient.class);
    private final RestTemplate restTemplate;

    public TwitchTokenResponseClient() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
    }

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest request) {
        ClientRegistration registration = request.getClientRegistration();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> entity = createHttpEntity(request, registration, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                registration.getProviderDetails().getTokenUri(),
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        var body = response.getBody();

        if (body == null || !body.containsKey("access_token") || !body.containsKey("refresh_token") || !body.containsKey("expires_in")) {
            throw new IllegalStateException("Invalid response from token endpoint: " + body);
        }

        String accessToken = (String) body.get("access_token");
        String refreshToken = (String) body.get("refresh_token");
        long expiresIn = ((Number) body.get("expires_in")).longValue();
        log.debug("Received access token: {}, refresh token: {}, expires in: {}", accessToken, refreshToken, expiresIn);

        return OAuth2AccessTokenResponse.withToken(accessToken)
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(expiresIn)
                .refreshToken(refreshToken)
                .scopes(request.getClientRegistration().getScopes())
                .additionalParameters(body)
                .build();
    }

    private static HttpEntity<?> createHttpEntity(OAuth2AuthorizationCodeGrantRequest request, ClientRegistration registration, HttpHeaders headers) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", request.getAuthorizationExchange().getAuthorizationResponse().getCode());
        form.add("redirect_uri", request.getAuthorizationExchange().getAuthorizationRequest().getRedirectUri());
        form.add("client_id", registration.getClientId());
        form.add("client_secret", registration.getClientSecret());

        return new HttpEntity<>(form, headers);
    }

}
