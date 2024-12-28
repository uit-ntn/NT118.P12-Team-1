package com.roadwatcher.https;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private String token;

    @SerializedName("user_id")
    private String userId;

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
