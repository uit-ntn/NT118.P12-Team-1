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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.roadwatcher.MainActivity;
import com.roadwatcher.R;
import com.roadwatcher.api.ApiClient;
import com.roadwatcher.api.PotholeApiService;
import com.roadwatcher.models.Pothole;
import com.roadwatcher.utils.SessionManager;

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

        // Kiểm tra trạng thái đăng nhập
        SessionManager sessionManager = SessionManager.getInstance(this);
        if (!sessionManager.isLoggedIn()) {
            // Nếu chưa đăng nhập, chuyển đến MainActivity
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_dashboard);

        // Phần còn lại của mã khởi tạo
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
        fetchAllPotholes(calendar);

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

    private void fetchAllPotholes(Calendar calendar) {
        Call<List<Pothole>> call = potholeApiService.fetchAllPotholes();
        call.enqueue(new Callback<List<Pothole>>() {
            @Override
            public void onResponse(Call<List<Pothole>> call, Response<List<Pothole>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pothole> potholes = response.body();

                    // Tổng số ổ gà hiện tại
                    totalPotholesText.setText(String.valueOf(potholes.size()));

                    // Lấy tháng và năm hiện tại
                    int currentMonth = calendar.get(Calendar.MONTH) + 1;
                    int currentYear = calendar.get(Calendar.YEAR);

                    // Tính số ổ gà được thêm mới trong tháng hiện tại
                    long potholesThisMonth = potholes.stream()
                            .filter(p -> {
                                String detectedTime = p.getDetectedTime();
                                try {
                                    // Định dạng ISO 8601 từ MongoDB
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
                                    Calendar detectedDate = Calendar.getInstance();
                                    detectedDate.setTime(dateFormat.parse(detectedTime));

                                    // So sánh tháng và năm
                                    return detectedDate.get(Calendar.MONTH) + 1 == currentMonth &&
                                            detectedDate.get(Calendar.YEAR) == currentYear;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return false;
                                }
                            })
                            .count();

                    // Hiển thị số ổ gà được thêm mới
                    potholesAddedThisMonthText.setText(String.valueOf(potholesThisMonth));

                    // Vẽ biểu đồ
                    drawBarChart(potholes);
                } else {
                    Toast.makeText(DashboardActivity.this, "Lỗi khi tải dữ liệu ổ gà", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pothole>> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Không thể kết nối tới server", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void drawBarChart(List<Pothole> potholes) {
        // Tính số lượng ổ gà theo mức độ
        long lowCount = potholes.stream().filter(p -> "low".equalsIgnoreCase(p.getSeverity())).count();
        long mediumCount = potholes.stream().filter(p -> "medium".equalsIgnoreCase(p.getSeverity())).count();
        long highCount = potholes.stream().filter(p -> "high".equalsIgnoreCase(p.getSeverity())).count();

        // Tạo danh sách dữ liệu cho biểu đồ
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, lowCount)); // Nhỏ
        entries.add(new BarEntry(1f, mediumCount)); // Trung bình
        entries.add(new BarEntry(2f, highCount)); // Lớn

        // Ẩn phần Description của biểu đồ
        barChart.getDescription().setEnabled(false);

        // Ẩn Legend của biểu đồ
        barChart.getLegend().setEnabled(false);

        // Cấu hình dữ liệu cho biểu đồ
        BarDataSet dataSet = new BarDataSet(entries, "Mức độ ổ gà");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f); // Kích thước chữ trên cột
        dataSet.setValueTextColor(getResources().getColor(android.R.color.black)); // Màu chữ trên cột

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // Chuyển float thành int và trả về chuỗi
            }
        });

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.5f); // Đặt chiều rộng của cột

        // Cấu hình biểu đồ
        barChart.setData(data);
        barChart.setFitBars(true); // Đảm bảo cột nằm gọn trong khung
        barChart.invalidate();

        // Ẩn trục X
        barChart.getXAxis().setDrawLabels(false); // Tắt nhãn trục X
        barChart.getXAxis().setDrawAxisLine(false); // Tắt đường trục X
        barChart.getXAxis().setDrawGridLines(false); // Tắt các đường lưới ngang trục X
    }

}
