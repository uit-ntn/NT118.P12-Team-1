package com.roadwatcher.https;

import java.util.List;

public class StatisticsResponse {
    private List<Statistic> statistics;

    public List<Statistic> getStatistics() {
        return statistics;
    }

    public static class Statistic {
        private String severity;
        private int count;

        public String getSeverity() {
            return severity;
        }

        public int getCount() {
            return count;
        }
    }
}
