package ru.sidey383.twitch.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import ru.sidey383.twitch.security.SessionAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> customUserService,
            SessionAuthenticationFilter jwtFilter
    ) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/streamer/**")
                        .ignoringRequestMatchers("/admin/**")
                        .ignoringRequestMatchers("/api/**")
                        .ignoringRequestMatchers("/webhook/**")
                )
                .oauth2Login(oauth -> oauth
                        .loginPage("/")
                        .defaultSuccessUrl("/oauth2/twitch/callback")
                        .userInfoEndpoint(user -> user.userService(customUserService))
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/oauth2/twitch/**").authenticated()
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/minecraft/**").hasRole("ADMIN")
                        .requestMatchers("/api/streamer/**").hasRole("STREAMER")
                        .requestMatchers( "/admin/*").hasRole("ADMIN")
                        .requestMatchers("/streamer/**").hasRole("STREAMER")
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                        .accessDeniedHandler(customAccessDeniedHandler())
                );
        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            // Если пользователь неавторизован и требуется авторизация — редирект на /login
            response.sendRedirect("/login");
        };
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("text/plain");
            response.getWriter().write("Access denied: insufficient permissions.");
            log.debug("403 Error: " + accessDeniedException.getMessage());
        };
    }
}