package com.ipas.ipas.view.dto;

public class UserUpdateRequest {
    private String role;
    private String status;

    public UserUpdateRequest() {}

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
