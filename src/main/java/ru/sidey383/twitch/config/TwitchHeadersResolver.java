package ru.sidey383.twitch.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import ru.sidey383.twitch.dto.twitch.TwitchEventSubHeaders;
import ru.sidey383.twitch.dto.twitch.TwitchMessageType;

public class TwitchHeadersResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(TwitchEventSubHeaders.class);
    }

    @Override
    public Object resolveArgument(
            @NonNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        return new TwitchEventSubHeaders(
                request.getHeader("twitch-eventsub-message-id"),
                parseRetryCount(request),
                parseMessageType(request),
                request.getHeader("twitch-eventsub-message-signature"),
                request.getHeader("twitch-eventsub-message-timestamp"),
                request.getHeader("twitch-eventsub-subscription-type"),
                request.getHeader("twitch-eventsub-subscription-version")
        );
    }

    private Integer parseRetryCount(HttpServletRequest request) {
        String retry = request.getHeader("twitch-eventsub-message-retry");
        return retry != null ? Integer.parseInt(retry) : null;
    }

    private TwitchMessageType parseMessageType(HttpServletRequest request) {
        return TwitchMessageType.fromValue(
                request.getHeader("twitch-eventsub-message-type")
        );
    }
}
