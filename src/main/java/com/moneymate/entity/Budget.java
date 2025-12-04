package com.moneymate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int totalBudget;      // 전체 예산
    private int foodBudget;       // 식비
    private int cafeBudget;       // 카페/간식
    private int shoppingBudget;   // 쇼핑
    private int transportBudget;  // 교통비
    private int cultureBudget;    // 문화/여가
    private int lifeBudget;       // 생활/기타
    private int healthBudget;     // 건강/운동
    private int beautyBudget;     // 뷰티/헤어

    @Column(name = "budget_year")
    private int year;             // 예산 적용 연도
    
    @Column(name = "budget_month")
    private int month;            // 예산 적용 월(1~12)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // ===== 기본 생성자 =====
    public Budget() {}

    // ===== Getter/Setter =====
    public Long getId() { return id; }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public int getTotalBudget() { return totalBudget; }
    public void setTotalBudget(int totalBudget) { this.totalBudget = totalBudget; }

    public int getFoodBudget() { return foodBudget; }
    public void setFoodBudget(int foodBudget) { this.foodBudget = foodBudget; }

    public int getCafeBudget() { return cafeBudget; }
    public void setCafeBudget(int cafeBudget) { this.cafeBudget = cafeBudget; }

    public int getShoppingBudget() { return shoppingBudget; }
    public void setShoppingBudget(int shoppingBudget) { this.shoppingBudget = shoppingBudget; }

    public int getTransportBudget() { return transportBudget; }
    public void setTransportBudget(int transportBudget) { this.transportBudget = transportBudget; }

    public int getCultureBudget() { return cultureBudget; }
    public void setCultureBudget(int cultureBudget) { this.cultureBudget = cultureBudget; }

    public int getLifeBudget() { return lifeBudget; }
    public void setLifeBudget(int lifeBudget) { this.lifeBudget = lifeBudget; }

    public int getHealthBudget() { return healthBudget; }
    public void setHealthBudget(int healthBudget) { this.healthBudget = healthBudget; }

    public int getBeautyBudget() { return beautyBudget; }
    public void setBeautyBudget(int beautyBudget) { this.beautyBudget = beautyBudget; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
