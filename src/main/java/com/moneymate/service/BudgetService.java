package com.moneymate.service;

import com.moneymate.dto.BudgetRequest;
import com.moneymate.entity.Budget;
import com.moneymate.entity.User;
import com.moneymate.repository.BudgetRepository;
import com.moneymate.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    public BudgetService(BudgetRepository budgetRepository, UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
    }

    // 저장 or 수정
    public Budget saveBudget(Long userId, int year, int month, BudgetRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Budget existing = budgetRepository
        	    .findByUser_IdAndYearAndMonth(userId, year, month)
        	    .orElse(null);



        Budget budget = (existing != null) ? existing : new Budget();

        budget.setUser(user);
        budget.setYear(year);
        budget.setMonth(month);

        budget.setTotalBudget(request.getTotalBudget());
        budget.setFoodBudget(request.getFoodBudget());
        budget.setCafeBudget(request.getCafeBudget());
        budget.setShoppingBudget(request.getShoppingBudget());
        budget.setTransportBudget(request.getTransportBudget());
        budget.setCultureBudget(request.getCultureBudget());
        budget.setLifeBudget(request.getLifeBudget());
        budget.setHealthBudget(request.getHealthBudget());
        budget.setBeautyBudget(request.getBeautyBudget());

        return budgetRepository.save(budget);
    }

    // 조회
    public Budget getBudget(Long userId, int year, int month) {
    	return budgetRepository
    			.findByUser_IdAndYearAndMonth(userId, year, month)
    	        .orElse(null);
    }

    // 삭제
    public void deleteBudget(Long budgetId) {
        budgetRepository.deleteById(budgetId);
    }
}
