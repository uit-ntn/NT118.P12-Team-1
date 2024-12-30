package com.roadwatcher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;
import com.roadwatcher.api.ApiClient;
import com.roadwatcher.api.AuthApiService;
import com.roadwatcher.models.User;
import com.roadwatcher.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView usernameTextView;
    private AuthApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Initialize SessionManager
        sessionManager = SessionManager.getInstance(this);
        apiService = ApiClient.getClient().create(AuthApiService.class);

        // Liên kết các view
        usernameTextView = findViewById(R.id.username);
        ImageView avatar = findViewById(R.id.avt);
        Button goToMapButton = findViewById(R.id.go_to_map);
        Button goToHomeButton = findViewById(R.id.go_to_home);

        // Lấy thông tin người dùng từ SessionManager
        String username = sessionManager.getUserName();

        // Hiển thị tên người dùng hoặc gọi API nếu username null
        if (username != null && !username.isEmpty()) {
            usernameTextView.setText(username);
        } else {
            fetchUserInfo();
        }

        // Xử lý sự kiện khi nhấn vào avatar
        avatar.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, AccountInfoActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện nút chuyển sang màn hình bản đồ
        goToMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, MapActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện nút chuyển sang màn hình Dashboard
        goToHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
    }

    private void fetchUserInfo() {
        String userId = sessionManager.getUserId();
        String token = sessionManager.getToken();

        if (userId == null || token == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        apiService.getUserById(userId, "Bearer " + token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Update SessionManager and UI
                    User user = response.body();
                    sessionManager.setUserName(user.getName());
                    usernameTextView.setText(user.getName());
                } else {
                    Toast.makeText(UserActivity.this, "Failed to fetch user info.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UserActivity", "Error fetching user info", t);
                Toast.makeText(UserActivity.this, "Error fetching user info.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method xử lý sự kiện đăng xuất
    public void onLogOut_Click(View view) {
        sessionManager.logout();
        Toast.makeText(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // Method xử lý sự kiện cài đặt
    public void onSettingsClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    // Method xử lý sự kiện điều khoản & chính sách
    public void onPolicyClick(View view) {
        Intent intent = new Intent(this, PrivacyPolicyActivity.class);
        startActivity(intent);
    }

    // Method xử lý sự kiện liên hệ hỗ trợ
    public void onContact_Click(View view) {
        Intent intent = new Intent(this, SupportContactActivity.class);
        startActivity(intent);
    }
}
