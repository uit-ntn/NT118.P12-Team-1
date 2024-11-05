package com.roadwatcher.models;


public class LoginRequest {
    private String name;
    private String email;
    private String phone;
    private String password;

    public LoginRequest(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

}