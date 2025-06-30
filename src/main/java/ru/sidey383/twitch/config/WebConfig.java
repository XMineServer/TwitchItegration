package ru.sidey383.twitch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.sidey383.twitch.config.twitch.TwitchHeadersResolver;
import ru.sidey383.twitch.dto.twitch.TwitchMessageType;
import ru.sidey383.twitch.security.utils.CurrentSessionArgumentResolver;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CurrentSessionArgumentResolver currentSessionArgumentResolver;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new TwitchMessageTypeConverter());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new TwitchHeadersResolver());
        resolvers.add(currentSessionArgumentResolver);
    }

    public static class TwitchMessageTypeConverter implements Converter<String, TwitchMessageType> {
        @Override
        public TwitchMessageType convert(@NonNull String source) {
            return TwitchMessageType.fromValue(source);
        }
    }

}
