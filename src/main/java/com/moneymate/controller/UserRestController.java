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
    // ⭐ 출석 체크 보상
    @PostMapping("/{userId}/attendance")
    public User attendance(@PathVariable Long userId) {
        return userService.giveAttendanceReward(userId);
    }
    
    // ⭐ 광고 보기 보상
    @PostMapping("/{userId}/ad")
    public User adReward(@PathVariable Long userId) {
        return userService.giveAdReward(userId);
    }
    
    // ⭐ 랜덤박스 보상
    @PostMapping("/{userId}/random-box")
    public int randomBox(@PathVariable Long userId) {
        return userService.giveRandomBoxReward(userId);
    }

}
