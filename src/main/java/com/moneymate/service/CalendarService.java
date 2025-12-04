package com.moneymate.service;

import com.moneymate.entity.Expense;
import com.moneymate.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class CalendarService {

    private final ExpenseRepository expenseRepository;

    public CalendarService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    // 전체 월 지출을 날짜별(total per day) map으로 가공
    public Map<LocalDate, Integer> getDailyExpenses(Long userId, int year, int month) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        List<Expense> monthExpenses =
                expenseRepository.findByUser_IdAndSpendDateBetween(userId, start, end);

        Map<LocalDate, Integer> dailyTotal = new HashMap<>();

        for (Expense e : monthExpenses) {
            LocalDate day = e.getSpendDate();
            dailyTotal.put(day, dailyTotal.getOrDefault(day, 0) + e.getAmount());
        }

        return dailyTotal;
    }
}
