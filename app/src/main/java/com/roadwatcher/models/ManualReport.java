package com.roadwatcher.models;

public class ManualReport {

        private String userId;
        private String reportTime;
        private String description;

        public ManualReport(String userId, String reportTime, String description) {
            this.userId = userId;
            this.reportTime = reportTime;
            this.description = description;
        }

        // Getters and setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getReportTime() { return reportTime; }
        public void setReportTime(String reportTime) { this.reportTime = reportTime; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

}
