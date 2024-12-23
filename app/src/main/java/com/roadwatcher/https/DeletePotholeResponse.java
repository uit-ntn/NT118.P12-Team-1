package com.roadwatcher.https;

import com.google.gson.annotations.SerializedName;

public class DeletePotholeResponse {
    @SerializedName("massage")
    private String message;

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
