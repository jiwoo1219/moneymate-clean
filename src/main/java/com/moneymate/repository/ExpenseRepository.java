package com.moneymate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.moneymate.entity.Expense;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUser_Id(Long userId);

    List<Expense> findByUser_IdAndSpendDateBetween(Long userId, LocalDate start, LocalDate end);

    List<Expense> findByUser_IdAndCategory(Long userId, String category);

    // 특정 월 지출 목록
    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId " +
           "AND YEAR(e.spendDate) = :year AND MONTH(e.spendDate) = :month")
    List<Expense> findByUserIdAndYearMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );

    // ⭐ 특정 월 총 지출 금액 (DB 전부 호환)
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
           "WHERE e.user.id = :userId " +
           "AND YEAR(e.spendDate) = :year " +
           "AND MONTH(e.spendDate) = :month")
    Integer sumMonthlyExpense(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );
}
