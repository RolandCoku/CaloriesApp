package org.springboot.caloriesapp.dto;

public class FoodEntryAdminDTO extends FoodEntryDTO {
    private Long userId;

    public FoodEntryAdminDTO() {
    }

    public FoodEntryAdminDTO(Long id, Long userId, String name, Double calories, Double price) {
        super(id, name, calories, price);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId =userId;
    }
}