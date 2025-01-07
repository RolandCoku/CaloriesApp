package org.springboot.caloriesapp.dto;

public class FoodEntryDTO {
    private Long id;
    private String date;
    private String time;
    private int calories;
    private Long userId;
    private String username;

    public FoodEntryDTO() {
    }

    public FoodEntryDTO(Long id, String date, String time, int calories, Long userId, String username) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.calories = calories;
        this.userId = userId;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
