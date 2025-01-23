package org.springboot.caloriesapp.dto;

public class FoodEntryAdminDTO extends FoodEntryDTO {
    private Long userId;
    private String email;

    public FoodEntryAdminDTO() {
    }

    public FoodEntryAdminDTO(Long id, Long userId, String name, Double calories, Double price, String email) {
        super(id, name, calories, price);
        this.userId = userId;
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId =userId;
    }

    public String getUserEmail() {
        return email;
    }

    public void setUserEmail(String email) {
        this.email = email;
    }
}


