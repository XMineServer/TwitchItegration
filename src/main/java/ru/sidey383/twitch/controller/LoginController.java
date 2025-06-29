package ru.sidey383.twitch.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.sidey383.twitch.model.TwitchOAuth2User;

@Controller
public class LoginController {

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal TwitchOAuth2User user) {
        model.addAttribute("name", user.getDisplayName());
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
