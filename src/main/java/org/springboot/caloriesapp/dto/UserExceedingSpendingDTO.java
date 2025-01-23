package org.springboot.caloriesapp.dto;

public class UserExceedingSpendingDTO {
    private Long id;
    private String name;
    private String username;
    private Double totalSpending;
    private Double monthlyLimit;

    public UserExceedingSpendingDTO(Long id, String name, String username, Double totalSpending, Double monthlyLimit) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.totalSpending = totalSpending;
        this.monthlyLimit = monthlyLimit;
    }

    public String getUsername() {
        return username;
    }

    public Double getTotalSpending() {
        return totalSpending;
    }

    public Double getMonthlyLimit() {
        return monthlyLimit;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
