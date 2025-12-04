package com.moneymate.controller;

import com.moneymate.entity.Expense;
import com.moneymate.service.ExpenseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expense")   // ← 여기 중요!
public class ExpenseRestController {

    private final ExpenseService expenseService;

    public ExpenseRestController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // ✅ 지출 등록 (POST)
    //  예) POST http://localhost:8080/api/expense/1
    @PostMapping("/{userId}")
    public Expense createExpense(@PathVariable Long userId,
                                 @RequestBody Expense expense) {
        return expenseService.saveExpense(userId, expense);
    }

    // ✅ 월별 지출 조회 (GET)
    //  예) GET http://localhost:8080/api/expense/1/2025/11
    @GetMapping("/{userId}/{year}/{month}")
    public List<Expense> getMonthExpenses(@PathVariable Long userId,
                                          @PathVariable int year,
                                          @PathVariable int month) {
        return expenseService.getMonthExpenses(userId, year, month);
    }

    // ✅ 지출 삭제 (DELETE)
    //  예) DELETE http://localhost:8080/api/expense/3
    @DeleteMapping("/{expenseId}")
    public void deleteExpense(@PathVariable Long expenseId) {
        expenseService.deleteExpense(expenseId);
    }
}