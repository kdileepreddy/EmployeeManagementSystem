package com.org.EmployeeManagementSystem.model;

public class LoginResponse {
    private String token;
    private String username;
    private String expiresAt;
    private String role;

    public LoginResponse(String token, String username, String expiresAt, String role) {
        this.token = token;
        this.username = username;
        this.expiresAt = expiresAt;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
