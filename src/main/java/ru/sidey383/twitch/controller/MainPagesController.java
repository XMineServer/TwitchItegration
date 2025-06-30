package ru.sidey383.twitch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MainPagesController {

    @GetMapping
    public String mainPage() {
        return "index";
    }
}
