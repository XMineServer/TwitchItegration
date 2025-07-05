package ru.sidey383.twitch.security.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import ru.sidey383.twitch.model.Session;
import ru.sidey383.twitch.repository.SessionRepository;
import ru.sidey383.twitch.security.TwitchOAuth2UserService;

import java.time.Instant;

@Component
public class CurrentSessionArgumentResolver implements HandlerMethodArgumentResolver {

    private final SessionRepository sessionRepository;

    public CurrentSessionArgumentResolver(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentSession.class)
                && parameter.getParameterType().equals(Session.class);
    }

    @Override
    public Object resolveArgument(
            @NotNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        var annotation = parameter.getParameterAnnotation(CurrentSession.class);
        if (annotation != null && request != null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (TwitchOAuth2UserService.SESSION_HEADER.equals(cookie.getName())) {
                    var session = sessionRepository.findByIdAndExpiresAtAfter(cookie.getValue(), Instant.now());
                    if (annotation.required()) {
                        return session.orElseThrow(() ->
                                new IllegalArgumentException("No valid session found for the provided cookie."));
                    } else {
                        return session.orElse(null);
                    }
                }
            }
        }
        return null;
    }
}