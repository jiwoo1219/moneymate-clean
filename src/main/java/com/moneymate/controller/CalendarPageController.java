package com.moneymate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CalendarPageController {

    @GetMapping("/calendar")
    public String calendarPage() {
        return "calendar";   // templates/calendar.html
    }
}
