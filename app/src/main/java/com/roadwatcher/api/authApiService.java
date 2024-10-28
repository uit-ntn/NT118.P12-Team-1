package com.roadwatcher.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface authApiService {

    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("protected-endpoint")
    Call<Protection_endpoint> getProtectedData(@Header("Authorization") String authToken);
}