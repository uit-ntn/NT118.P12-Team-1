package com.roadwatcher.api;
import com.roadwatcher.models.CreatePotholeRequest;
import com.roadwatcher.models.CreatePotholeResponse;
import com.roadwatcher.models.Pothole;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

public interface PotholeApiService {
    @GET("/api/potholes")
    Call<List<Pothole>> getAllPotholes();

    @POST("/api/potholes")
    Call<CreatePotholeResponse> createPothole(@Body CreatePotholeRequest request);
}

