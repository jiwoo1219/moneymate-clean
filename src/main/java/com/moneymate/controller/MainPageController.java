package com.moneymate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {

    @GetMapping("/")
    public String home() {
        return "moneymate";  // templates/moneymate.html
    }

    
}
