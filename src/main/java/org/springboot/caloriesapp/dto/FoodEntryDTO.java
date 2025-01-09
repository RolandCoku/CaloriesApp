package org.springboot.caloriesapp.dto;

public class FoodEntryDTO {
    private Long id;
    private String name;
    private double calories;
    private double price;
    private Long userId;

    public FoodEntryDTO() {
    }

    public FoodEntryDTO(Long id, String name, double calories, double price, Long userId) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.price = price;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
