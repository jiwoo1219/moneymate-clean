package com.moneymate.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.moneymate.entity.*;
import com.moneymate.repository.*;

@RestController
@RequestMapping("/test")
public class TestController {

    private final UserRepository userRepo;
    private final ExpenseRepository expenseRepo;
    private final BudgetRepository budgetRepo;
    private final PetRepository petRepo;
    private final AttendanceRepository attendanceRepo;

    public TestController(UserRepository userRepo, ExpenseRepository expenseRepo,
                          BudgetRepository budgetRepo, PetRepository petRepo,
                          AttendanceRepository attendanceRepo) {
        this.userRepo = userRepo;
        this.expenseRepo = expenseRepo;
        this.budgetRepo = budgetRepo;
        this.petRepo = petRepo;
        this.attendanceRepo = attendanceRepo;
    }

    // ✅ DB 연결 테스트: 모든 테이블 비어 있는지 확인
    @GetMapping("/check")
    public String checkRepositories() {
        long userCount = userRepo.count();
        long expenseCount = expenseRepo.count();
        long budgetCount = budgetRepo.count();
        long petCount = petRepo.count();
        long attendanceCount = attendanceRepo.count();

        return String.format(
            "✅ 연결 완료!\nUsers: %d, Expenses: %d, Budgets: %d, Pets: %d, Attendance: %d",
            userCount, expenseCount, budgetCount, petCount, attendanceCount
        );
    }

    // 🧩 임시 유저 추가 테스트
    @PostMapping("/add-user")
    public User addUser(@RequestParam String name) {
        User u = new User();
        u.setUsername(name);
        u.setName(name);
        return userRepo.save(u);
    }

    // 🔍 유저 전체 조회
    @GetMapping("/users")
    public List<User> allUsers() {
        return userRepo.findAll();
    }
}
