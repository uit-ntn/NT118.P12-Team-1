package com.roadwatcher.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static final String BASE_URL = "https://road-watcher-server.onrender.com/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Tạo interceptor để log các yêu cầu và phản hồi
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Cấu hình OkHttpClient
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor) // Thêm logging
                    .connectTimeout(30, TimeUnit.SECONDS) // Timeout kết nối
                    .readTimeout(30, TimeUnit.SECONDS) // Timeout đọc dữ liệu
                    .writeTimeout(30, TimeUnit.SECONDS) // Timeout ghi dữ liệu
                    .build();

            // Cấu hình Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
