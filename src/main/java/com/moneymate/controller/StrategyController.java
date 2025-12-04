package com.moneymate.controller;

import com.moneymate.entity.Expense;
import com.moneymate.repository.ExpenseRepository;
import com.moneymate.service.HuggingFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/strategy")
public class StrategyController {

    @Autowired
    private HuggingFaceService huggingFaceService;

    @Autowired
    private ExpenseRepository expenseRepository;

    @GetMapping("/{userId}/{year}/{month}")
    public Map<String, Object> generateStrategy(
            @PathVariable Long userId,
            @PathVariable int year,
            @PathVariable int month,
            @RequestParam(required = false, defaultValue = "0") int budget
    ) {
        System.out.println("=== AI 전략 생성 요청 ===");
        System.out.println("userId: " + userId + ", year: " + year + ", month: " + month + ", budget: " + budget);

        // 해당 월의 지출 데이터 조회
        List<Expense> expenses = expenseRepository.findByUserIdAndYearMonth(userId, year, month);
        
        System.out.println("조회된 지출 내역: " + expenses.size() + "건");
        
        // 총 지출 계산
        int totalSpent = expenses.stream()
                .mapToInt(Expense::getAmount)
                .sum();

        // 카테고리별 지출 집계
        Map<String, Integer> categorySpending = expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCategory() != null ? e.getCategory() : "기타",
                        Collectors.summingInt(Expense::getAmount)
                ));

        System.out.println("총 지출: " + totalSpent + "원");
        System.out.println("카테고리별 지출: " + categorySpending);

        // AI 서비스에 전달할 데이터
        Map<String, Object> userData = new HashMap<>();
        userData.put("totalSpent", totalSpent);
        userData.put("budget", budget);
        userData.put("categorySpending", categorySpending);

        // AI 전략 생성
        String strategy = huggingFaceService.generateSpendingStrategy(userData);

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("strategy", strategy);
        response.put("totalSpent", totalSpent);
        response.put("categorySpending", categorySpending);
        
        // 가장 많이 쓴 카테고리
        String topCategory = categorySpending.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey() + " (" + e.getValue() + "원)")
                .orElse("없음");
        response.put("topCategory", topCategory);
        
        // 예산 대비 지출 비율
        int budgetRatio = budget > 0 ? (int)((totalSpent * 100.0) / budget) : 0;
        response.put("budgetRatio", budgetRatio);

        System.out.println("=== 응답 데이터 ===");
        System.out.println(response);

        return response;
    }
}