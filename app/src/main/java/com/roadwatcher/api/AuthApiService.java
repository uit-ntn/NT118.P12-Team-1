package com.roadwatcher.api;

import com.roadwatcher.https.ForgotPasswordRequest;
import com.roadwatcher.https.ForgotPasswordResponse;
import com.roadwatcher.https.LoginRequest;
import com.roadwatcher.https.LoginResponse;
import com.roadwatcher.https.ResetPasswordRequest;
import com.roadwatcher.https.ResetPasswordResponse;
import com.roadwatcher.https.SignupRequest;
import com.roadwatcher.https.SignupResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApiService {

    @POST("api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("api/auth/signup")
    Call<SignupResponse> signupUser(@Body SignupRequest signupRequest);

    @POST("/api/auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest forgotPasswordRequest);

    @POST("/api/auth/reset-password")
    Call<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequest resetPasswordRequest);

    @GET("/api/auth/google")
    Call<Void> googleLogin();

    @GET("/api/auth/google/callback")
    Call<LoginResponse> googleCallback(@Query("code") String authCode);
}
