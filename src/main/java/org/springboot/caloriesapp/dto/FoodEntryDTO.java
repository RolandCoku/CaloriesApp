package org.springboot.caloriesapp.dto;

import java.time.LocalDateTime;

public class FoodEntryDTO {
    private Long id;
    private String name;
    private Double calories;
    private Double price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FoodEntryDTO() {
    }

    public FoodEntryDTO(Long id, String name, Double calories, Double price) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.price = price;
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

    public Double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}