package com.roadwatcher.https;

public class ResetPasswordResponse {
    private String msg;

    public ResetPasswordResponse(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
