package com.roadwatcher.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://road-watcher-server.onrender.com/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Cấu hình OkHttpClient với thời gian timeout
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS) // Thời gian timeout kết nối
                    .readTimeout(30, TimeUnit.SECONDS)    // Thời gian timeout đọc dữ liệu
                    .writeTimeout(30, TimeUnit.SECONDS)   // Thời gian timeout ghi dữ liệu
                    .build();

            // Gán OkHttpClient vào Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient) // Sử dụng OkHttpClient
                    .build();
        }
        return retrofit;
    }
}
