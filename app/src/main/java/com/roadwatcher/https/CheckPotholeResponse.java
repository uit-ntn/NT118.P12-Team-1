package com.roadwatcher.https;

import java.util.List;

public class CheckPotholeResponse {
    private boolean found;
    private List<CreatePotholeResponse> potholes;

    // Getters and Setters
    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public List<CreatePotholeResponse> getPotholes() {
        return potholes;
    }

    public void setPotholes(List<CreatePotholeResponse> potholes) {
        this.potholes = potholes;
    }
}
