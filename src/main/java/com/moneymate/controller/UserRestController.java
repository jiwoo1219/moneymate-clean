package com.moneymate.controller;

import com.moneymate.entity.User;
import com.moneymate.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public User login(@RequestBody User loginUser) {
        return userService.login(loginUser.getUsername(), loginUser.getPassword());
    }
}
