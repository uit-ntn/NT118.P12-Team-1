package com.roadwatcher.https;

import com.google.gson.annotations.SerializedName;
import com.roadwatcher.models.Pothole;
import java.util.List;

public class GetAllPotholesResponse {

    @SerializedName("potholes")
    private List<Pothole> potholes;

    // Getters and Setters
    public List<Pothole> getPotholes() {
        return potholes;
    }

    public void setPotholes(List<Pothole> potholes) {
        this.potholes = potholes;
    }
}
