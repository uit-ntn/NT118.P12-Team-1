package com.roadwatcher.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;
import com.roadwatcher.api.ApiClient;
import com.roadwatcher.api.PotholeApiService;
import com.roadwatcher.utils.PieChartView;
import com.roadwatcher.https.StatisticsResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticsBySeverityActivity extends AppCompatActivity {
    private PieChartView pieChartView;
    private TextView level3Count, level5Count, level7Count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics_by_severity);

        pieChartView = findViewById(R.id.pieChartView);
        level3Count = findViewById(R.id.level3Count);
        level5Count = findViewById(R.id.level5Count);
        level7Count = findViewById(R.id.level7Count);

        fetchStatistics();
    }

    private void fetchStatistics() {
        PotholeApiService apiService = ApiClient.getClient().create(PotholeApiService.class);
        Call<StatisticsResponse> call = apiService.getStatisticsByDay(2024, 10, 5); // Example date

        call.enqueue(new Callback<StatisticsResponse>() {
            @Override
            public void onResponse(Call<StatisticsResponse> call, Response<StatisticsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Float> data = new ArrayList<>();
                    List<Integer> colors = new ArrayList<>();

                    for (StatisticsResponse.Statistic stat : response.body().getStatistics()) {
                        switch (stat.getSeverity()) {
                            case "low":
                                data.add((float) stat.getCount());
                                colors.add(getResources().getColor(R.color.green));
                                level3Count.setText(String.valueOf(stat.getCount()));
                                break;
                            case "medium":
                                data.add((float) stat.getCount());
                                colors.add(getResources().getColor(R.color.yellow));
                                level5Count.setText(String.valueOf(stat.getCount()));
                                break;
                            case "high":
                                data.add((float) stat.getCount());
                                colors.add(getResources().getColor(R.color.red));
                                level7Count.setText(String.valueOf(stat.getCount()));
                                break;
                        }
                    }

                    pieChartView.setData(data, colors);
                } else {
                    Toast.makeText(StatisticsBySeverityActivity.this, "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatisticsResponse> call, Throwable t) {
                Toast.makeText(StatisticsBySeverityActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
