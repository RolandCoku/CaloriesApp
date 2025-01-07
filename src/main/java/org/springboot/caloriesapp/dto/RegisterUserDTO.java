package org.springboot.caloriesapp.dto;

public class RegisterUserDTO {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private Long roleId;

    public RegisterUserDTO() {
    }

    public RegisterUserDTO(String username, String email, String password, Long roleId) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roleId = roleId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Long getRoleId() {
        return roleId;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
