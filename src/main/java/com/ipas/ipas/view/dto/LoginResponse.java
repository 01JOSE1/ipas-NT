package com.ipas.ipas.view.dto;

import com.ipas.ipas.view.dto.UserSimpleDTO;

public class LoginResponse {
    
    private String token;
    private UserSimpleDTO user;
    
    // Constructors
    public LoginResponse() {}

    public LoginResponse(String token, UserSimpleDTO user) {
        this.token = token;
        this.user = user;
    }
    
    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UserSimpleDTO getUser() { return user; }
    public void setUser(UserSimpleDTO user) { this.user = user; }
}