package com.moneymate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BudgetPageController {

    @GetMapping("/budget")
    public String budgetPage() {
        return "budget";  // templates/budget.html
    }
}
