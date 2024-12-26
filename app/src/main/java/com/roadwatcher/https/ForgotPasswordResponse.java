package com.roadwatcher.https;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordResponse {

    @SerializedName("msg")
    private String msg;

    public ForgotPasswordResponse(String msg) {
        this.msg = msg;
    }

    // Getter và Setter
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
