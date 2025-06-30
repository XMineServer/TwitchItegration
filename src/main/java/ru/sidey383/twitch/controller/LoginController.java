package ru.sidey383.twitch.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import ru.sidey383.twitch.model.Session;
import ru.sidey383.twitch.security.utils.CurrentSession;
import ru.sidey383.twitch.service.UserService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping("/logout")
    public String logout(
            @CurrentSession(required = false) Session session,
            @SessionAttribute(required = false) HttpSession httpSession,
            HttpServletResponse response
    ) {
        if (session != null) {
            log.info("Logging out user with session: {}", session);
            Cookie cookie = new Cookie("JSESSIONID", "");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            userService.logout(session);
        }
        if (httpSession != null) {
            log.info("Invalidating HTTP session: {}", httpSession.getId());
            httpSession.invalidate();
        }
        return "redirect:/login?logout";
    }

}
