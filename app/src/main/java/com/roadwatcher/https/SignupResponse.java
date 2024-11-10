package com.roadwatcher.https;

public class SignupResponse {
    private String token;
    private String userId;

    // Constructor
    public SignupResponse(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
