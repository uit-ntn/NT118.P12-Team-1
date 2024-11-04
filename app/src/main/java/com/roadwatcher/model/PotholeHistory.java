package com.roadwatcher.model;
public class PotholeHistory {
    private String id;
    private String historicalRecordId;
    private String potholeId;
    private String status;
    private String lastUpdated;
    private double longitude;
    private double latitude;
    private String severity;

    public PotholeHistory(String id, String historicalRecordId, String potholeId, String status,
                          String lastUpdated, double longitude, double latitude, String severity) {
        this.id = id;
        this.historicalRecordId = historicalRecordId;
        this.potholeId = potholeId;
        this.status = status;
        this.lastUpdated = lastUpdated;
        this.longitude = longitude;
        this.latitude = latitude;
        this.severity = severity;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getHistoricalRecordId() { return historicalRecordId; }
    public void setHistoricalRecordId(String historicalRecordId) { this.historicalRecordId = historicalRecordId; }
    public String getPotholeId() { return potholeId; }
    public void setPotholeId(String potholeId) { this.potholeId = potholeId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
}
