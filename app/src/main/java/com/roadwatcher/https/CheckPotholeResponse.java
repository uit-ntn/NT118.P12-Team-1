package com.roadwatcher.https;

import com.google.gson.annotations.SerializedName;
import com.roadwatcher.https.CreatePotholeResponse;

import java.util.List;

public class CheckPotholeResponse {

    @SerializedName("found")
    private boolean found;

    @SerializedName("potholes")
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
