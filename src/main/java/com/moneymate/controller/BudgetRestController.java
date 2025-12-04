package com.moneymate.controller;

import com.moneymate.dto.BudgetRequest;
import com.moneymate.entity.Budget;
import com.moneymate.service.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
public class BudgetRestController {

    private final BudgetService budgetService;

    public BudgetRestController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // ⭐ 예산 저장
    @PostMapping("/{userId}")
    public ResponseEntity<Budget> saveBudget(
            @PathVariable Long userId,
            @RequestBody BudgetRequest request
    ) {
        int year = Integer.parseInt(request.getYearMonth().split("-")[0]);
        int month = Integer.parseInt(request.getYearMonth().split("-")[1]);

        Budget saved = budgetService.saveBudget(userId, year, month, request);
        return ResponseEntity.ok(saved);
    }

    // ⭐ 예산 조회
    @GetMapping("/{userId}/{year}/{month}")
    public ResponseEntity<?> getBudget(
            @PathVariable Long userId,
            @PathVariable int year,
            @PathVariable int month
    ) {
        Budget budget = budgetService.getBudget(userId, year, month);

        if (budget == null) {
            return ResponseEntity.noContent().build();  // 204 응답
        }

        return ResponseEntity.ok(budget);               // 200 + JSON 응답
    }

    // ⭐ 예산 삭제
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long budgetId) {
        budgetService.deleteBudget(budgetId);
        return ResponseEntity.noContent().build();
    }
}
