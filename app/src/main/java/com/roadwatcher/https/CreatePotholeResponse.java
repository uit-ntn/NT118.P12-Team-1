package com.roadwatcher.https;

import com.roadwatcher.models.ManualReport;

import java.util.List;

public class CreatePotholeResponse {
    private String id;
    private double latitude;
    private double longitude;
    private String severity;
    private String detectedTime;
    private boolean confirmedByUser;
    private List<ManualReport> manualReports;

    public CreatePotholeResponse(String id, double latitude, double longitude, String severity,
                                 String detectedTime, boolean confirmedByUser,
                                 List<ManualReport> manualReports) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.severity = severity;
        this.detectedTime = detectedTime;
        this.confirmedByUser = confirmedByUser;
        this.manualReports = manualReports;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getDetectedTime() { return detectedTime; }
    public void setDetectedTime(String detectedTime) { this.detectedTime = detectedTime; }

    public boolean isConfirmedByUser() { return confirmedByUser; }
    public void setConfirmedByUser(boolean confirmedByUser) { this.confirmedByUser = confirmedByUser; }

    public List<ManualReport> getManualReports() { return manualReports; }
    public void setManualReports(List<ManualReport> manualReports) { this.manualReports = manualReports; }
}
