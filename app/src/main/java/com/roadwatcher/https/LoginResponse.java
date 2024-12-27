package com.roadwatcher.https;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    private String token;

    @SerializedName("user_id")
    private String user_id;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }
}