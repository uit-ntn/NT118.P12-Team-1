package com.roadwatcher.api;
import com.roadwatcher.models.Pothole;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;

public interface PotholeApiService {
    @GET("/api/potholes")
    Call<List<Pothole>> getAllPotholes();
}

