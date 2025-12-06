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

    // 기존: 특정 년/월 지출 목록
    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId " +
           "AND YEAR(e.spendDate) = :year AND MONTH(e.spendDate) = :month")
    List<Expense> findByUserIdAndYearMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month
    );

    // ⭐ 추가: 특정 YYYY-MM의 총 지출 금액
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
           "WHERE e.user.id = :userId " +
           "AND FUNCTION('DATE_FORMAT', e.spendDate, '%Y-%m') = :yearMonth")
    Integer sumMonthlyExpense(
            @Param("userId") Long userId,
            @Param("yearMonth") String yearMonth
    );
}
