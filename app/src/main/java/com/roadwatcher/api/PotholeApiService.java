package com.roadwatcher.api;

import com.roadwatcher.https.CheckPotholeRequest;
import com.roadwatcher.https.CheckPotholeResponse;
import com.roadwatcher.https.ConfirmPotholeResponse;
import com.roadwatcher.https.CreatePotholeRequest;
import com.roadwatcher.https.CreatePotholeResponse;

import com.roadwatcher.https.DeletePotholeResponse;
import com.roadwatcher.https.GetAllPotholesResponse;
import com.roadwatcher.https.StatisticsResponse;
import com.roadwatcher.https.UpdatePotholeRequest;
import com.roadwatcher.models.Pothole;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PotholeApiService {
    @GET("/api/potholes")
    Call<List<Pothole>> getAllPotholes();

    @POST("/api/potholes")
    Call<CreatePotholeResponse> createPothole(@Body CreatePotholeRequest request);

    @GET("/api/potholes/{id}")
    Call<CreatePotholeResponse> getPotholeById(@Path("id") String id);

    @PUT("/api/potholes/{id}")
    Call<CreatePotholeResponse> updatePothole(@Path("id") String id, @Body UpdatePotholeRequest request);

    @DELETE("/api/potholes/{id}")
    Call<DeletePotholeResponse> deletePothole(@Path("id") String id);

    @POST("/api/potholes/check")
    Call<CheckPotholeResponse> checkPothole(@Body CheckPotholeRequest request);

    @PUT("/api/potholes/{id}/confirm")
    Call<ConfirmPotholeResponse> confirmPothole(@Path("id") String id);

    @GET("/api/potholes/statistics/day")
    Call<StatisticsResponse> getStatisticsByDay(@Query("year") int year, @Query("month") int month, @Query("day") int day);

    @GET("/api/potholes/statistics/week")
    Call<StatisticsResponse> getStatisticsByWeek(@Query("year") int year, @Query("week") int week);

    @GET("/api/potholes/statistics/month")
    Call<StatisticsResponse> getStatisticsByMonth(@Query("year") int year, @Query("month") int month);

    @GET("/api/potholes")
    Call<List<Pothole>> fetchAllPotholes();


}

