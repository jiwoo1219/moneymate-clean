package com.moneymate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExpensePageController {

	@GetMapping("/moneymate")
    public String moneyMatePage() {
        return "moneymate";  // templates/moneymate.html 실행
    }
}