package com.moneymate.service;

import com.moneymate.entity.Expense;
import com.moneymate.entity.User;
import com.moneymate.repository.ExpenseRepository;
import com.moneymate.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    // 지출 저장
    public Expense saveExpense(Long userId, Expense expense) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        expense.setUser(user);
        return expenseRepository.save(expense);
    }

    // 월별 조회
    public List<Expense> getMonthExpenses(Long userId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        return expenseRepository.findByUser_IdAndSpendDateBetween(userId, start, end);
    }

    // 삭제
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }
}
