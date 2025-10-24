package com.smartleavemanagement.DTOs;

public class LoginResponse {
    private int userId;
    private String role;
    private String email;
    private String token;

    public LoginResponse(int userId, String role, String email, String token) {
        this.userId = userId;
        this.role = role;
        this.email = email;
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }
}
