package com.roadwatcher.api;

import com.roadwatcher.models.LoginRequest;
import com.roadwatcher.models.LoginResponse;
import com.roadwatcher.models.SignupRequest;
import com.roadwatcher.models.SignupResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("api/auth/signup")
    Call<SignupResponse> signupUser (@Body SignupRequest signupRequest);
}
