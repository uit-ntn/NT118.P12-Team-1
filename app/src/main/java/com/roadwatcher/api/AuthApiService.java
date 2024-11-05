package com.roadwatcher.api;

import com.roadwatcher.models.LoginRequest;
import com.roadwatcher.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
}
