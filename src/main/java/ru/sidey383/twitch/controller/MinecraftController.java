package ru.sidey383.twitch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/minecraft")
@RequiredArgsConstructor
public class MinecraftController {

    @GetMapping
    public String adminUsers() {
        return "admin/minecraft/index";
    }

}
