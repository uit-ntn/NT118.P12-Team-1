package com.roadwatcher.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {
    @SerializedName("_id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    private String name;
    private String email;
    private String password;
    private String phone;

    @SerializedName("registered_on")
    private String registeredOn;

    private List<PotholeReport> potholeReports;


    // Constructor
    public User(String id, String userId, String name, String email, String password, String phone, String registeredOn, List<PotholeReport> potholeReports) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.registeredOn = registeredOn;
        this.potholeReports = potholeReports;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRegisteredOn() { return registeredOn; }
    public void setRegisteredOn(String registeredOn) { this.registeredOn = registeredOn; }
    public List<PotholeReport> getPotholeReports() { return potholeReports; }
    public void setPotholeReports(List<PotholeReport> potholeReports) { this.potholeReports = potholeReports; }
}

class PotholeReport {
    private String potholeId;
    private String reportTime;

    public PotholeReport(String potholeId, String reportTime) {
        this.potholeId = potholeId;
        this.reportTime = reportTime;
    }

    // Getters and setters
    public String getPotholeId() { return potholeId; }
    public void setPotholeId(String potholeId) { this.potholeId = potholeId; }
    public String getReportTime() { return reportTime; }
    public void setReportTime(String reportTime) { this.reportTime = reportTime; }
}
