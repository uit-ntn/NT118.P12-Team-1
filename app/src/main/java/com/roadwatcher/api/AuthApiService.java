package com.roadwatcher.api;

import com.roadwatcher.https.LoginRequest;
import com.roadwatcher.https.LoginResponse;
import com.roadwatcher.https.SignupRequest;
import com.roadwatcher.https.SignupResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("api/auth/signup")
    Call<SignupResponse> signupUser (@Body SignupRequest signupRequest);
}
