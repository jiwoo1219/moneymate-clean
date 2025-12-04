package com.moneymate.repository;

import com.moneymate.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByUser_IdAndYearAndMonth(Long userId, int year, int month);
}

