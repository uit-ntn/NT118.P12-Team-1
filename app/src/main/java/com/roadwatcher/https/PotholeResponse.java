package com.roadwatcher.https;

import com.roadwatcher.models.Pothole;

import java.util.List;

public class PotholeResponse {
    private boolean found;
    private List<Pothole> potholes;

    // Getters and Setters
    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public List<Pothole> getPotholes() {
        return potholes;
    }

    public void setPotholes(List<Pothole> potholes) {
        this.potholes = potholes;
    }
}
