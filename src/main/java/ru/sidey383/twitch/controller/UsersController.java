package ru.sidey383.twitch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.sidey383.twitch.dto.user.UpdateRolesRequest;
import ru.sidey383.twitch.service.UserService;

@Controller
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;


    @GetMapping("/")
    public String adminUsers() {
        return "admin/user/list";
    }

    @PatchMapping("/{userId}/roles")
    public String updateRoles(
            @PathVariable Long userId,
            @ModelAttribute UpdateRolesRequest request,
            Model model
    ) {
        userService.setRoles(userId, request.roles());
        model.addAttribute("users", userService.getUsers());
        model.addAttribute("roles", userService.getAvailableRoles());
        return "admin/user/components/userTable :: userTable";
    }

    @GetMapping("/table")
    public String userTable(Model model) {
        model.addAttribute("users", userService.getUsers());
        model.addAttribute("roles", userService.getAvailableRoles());
        return "admin/user/components/userTable";
    }

}
