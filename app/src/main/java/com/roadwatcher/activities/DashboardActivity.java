package com.roadwatcher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.roadwatcher.R;
import com.roadwatcher.api.ApiClient;
import com.roadwatcher.api.PotholeApiService;
import com.roadwatcher.https.GetAllPotholesResponse;
import com.roadwatcher.models.Pothole;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private TextView totalPotholesText, potholesAddedThisMonthText, currentMonthText;
    private BarChart barChart;
    private PotholeApiService potholeApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Liên kết các view
        totalPotholesText = findViewById(R.id.totalPotholes);
        potholesAddedThisMonthText = findViewById(R.id.potholesAddedThisMonth);
        currentMonthText = findViewById(R.id.currentMonth);
        barChart = findViewById(R.id.barChart);

        Button goToMapButton = findViewById(R.id.go_to_map);
        Button goToUserButton = findViewById(R.id.go_to_user);
        Button goToHomeButton = findViewById(R.id.go_to_home);

        // Hiển thị tháng/năm hiện tại
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        currentMonthText.setText(monthYearFormat.format(calendar.getTime()));

        // Khởi tạo API
        potholeApiService = ApiClient.getClient().create(PotholeApiService.class);

        // Lấy dữ liệu ổ gà
        fetchPotholeData(calendar);

        // Chuyển hướng cho các nút footer
        goToMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MapActivity.class);
            startActivity(intent);
        });

        goToUserButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, UserActivity.class);
            startActivity(intent);
        });

        goToHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
    }

    private void fetchPotholeData(Calendar calendar) {
        Call<GetAllPotholesResponse> call = potholeApiService.getAllPotholes();
        call.enqueue(new Callback<GetAllPotholesResponse>() {
            @Override
            public void onResponse(Call<GetAllPotholesResponse> call, Response<GetAllPotholesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pothole> potholes = response.body().getPotholes();

                    // Tổng số ổ gà hiện tại
                    totalPotholesText.setText(String.valueOf(potholes.size()));

                    // Số ổ gà được thêm mới trong tháng hiện tại
                    int currentMonth = calendar.get(Calendar.MONTH) + 1;
                    int currentYear = calendar.get(Calendar.YEAR);
                    long potholesThisMonth = potholes.stream()
                            .filter(p -> {
                                String detectedTime = p.getDetectedTime();
                                try {
                                    Calendar detectedDate = Calendar.getInstance();
                                    detectedDate.setTime(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(detectedTime));
                                    return detectedDate.get(Calendar.MONTH) + 1 == currentMonth &&
                                            detectedDate.get(Calendar.YEAR) == currentYear;
                                } catch (Exception e) {
                                    return false;
                                }
                            })
                            .count();
                    potholesAddedThisMonthText.setText(String.valueOf(potholesThisMonth));

                    // Vẽ biểu đồ
                    drawBarChart(potholes);
                } else {
                    Toast.makeText(DashboardActivity.this, "Lỗi khi tải dữ liệu ổ gà", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetAllPotholesResponse> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Không thể kết nối tới server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawBarChart(List<Pothole> potholes) {
        long lowCount = potholes.stream().filter(p -> "low".equalsIgnoreCase(p.getSeverity())).count();
        long mediumCount = potholes.stream().filter(p -> "medium".equalsIgnoreCase(p.getSeverity())).count();
        long highCount = potholes.stream().filter(p -> "high".equalsIgnoreCase(p.getSeverity())).count();

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, lowCount));
        entries.add(new BarEntry(1, mediumCount));
        entries.add(new BarEntry(2, highCount));

        BarDataSet dataSet = new BarDataSet(entries, "Mức độ ổ gà");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        barChart.setData(data);
        barChart.setFitBars(true);
        barChart.invalidate();

        Description description = new Description();
        description.setText("Thống kê ổ gà theo mức độ");
        barChart.setDescription(description);
    }
}
