public boolean isOverBudget(Long userId, String yearMonth) {

    // "2025-12" → year=2025, month=12
    String[] parts = yearMonth.split("-");
    int year = Integer.parseInt(parts[0]);
    int month = Integer.parseInt(parts[1]);

    // ⭐ 예산 조회 (없으면 0)
    int budget = budgetRepository
            .findByUser_IdAndYearAndMonth(userId, year, month)
            .map(Budget::getTotalBudget)
            .orElse(0);

    // ⭐ 지출 합계 조회 (지출 없으면 null → 0으로 치환)
    Integer spentValue = expenseRepository.sumMonthlyExpense(userId, yearMonth);
    int spent = (spentValue != null) ? spentValue : 0;

    return spent > budget;
}
