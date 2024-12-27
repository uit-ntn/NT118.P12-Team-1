package com.roadwatcher.models;

public class PotholeReport {

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
