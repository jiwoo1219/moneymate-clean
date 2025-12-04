package com.moneymate.dto;

public class BudgetRequest {

    private String yearMonth;

    private int totalBudget;
    private int foodBudget;
    private int cafeBudget;
    private int shoppingBudget;
    private int transportBudget;
    private int cultureBudget;
    private int lifeBudget;
    private int healthBudget;
    private int beautyBudget;

    public String getYearMonth() { return yearMonth; }

    public int getTotalBudget() { return totalBudget; }
    public int getFoodBudget() { return foodBudget; }
    public int getCafeBudget() { return cafeBudget; }
    public int getShoppingBudget() { return shoppingBudget; }
    public int getTransportBudget() { return transportBudget; }
    public int getCultureBudget() { return cultureBudget; }
    public int getLifeBudget() { return lifeBudget; }
    public int getHealthBudget() { return healthBudget; }
    public int getBeautyBudget() { return beautyBudget; }
    
    public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }

    public void setTotalBudget(int totalBudget) { this.totalBudget = totalBudget; }
    public void setFoodBudget(int foodBudget) { this.foodBudget = foodBudget; }
    public void setCafeBudget(int cafeBudget) { this.cafeBudget = cafeBudget; }
    public void setShoppingBudget(int shoppingBudget) { this.shoppingBudget = shoppingBudget; }
    public void setTransportBudget(int transportBudget) { this.transportBudget = transportBudget; }
    public void setCultureBudget(int cultureBudget) { this.cultureBudget = cultureBudget; }
    public void setLifeBudget(int lifeBudget) { this.lifeBudget = lifeBudget; }
    public void setHealthBudget(int healthBudget) { this.healthBudget = healthBudget; }
    public void setBeautyBudget(int beautyBudget) { this.beautyBudget = beautyBudget; }

}
