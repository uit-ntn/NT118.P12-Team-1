package com.roadwatcher.https;

public class ForgotPasswordResponse {

    private String msg ;

    public ForgotPasswordResponse(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
