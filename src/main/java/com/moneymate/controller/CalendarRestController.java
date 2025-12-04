package com.moneymate.controller;

import com.moneymate.service.CalendarService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/calendar")
public class CalendarRestController {

    private final CalendarService calendarService;

    public CalendarRestController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    // 날짜별 지출 합계 조회
    @GetMapping("/{userId}/{year}/{month}")
    public Map<LocalDate, Integer> getDailyExpenses(
            @PathVariable Long userId,
            @PathVariable int year,
            @PathVariable int month
    ) {
        return calendarService.getDailyExpenses(userId, year, month);
    }
}
