package com.roadwatcher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;
import com.roadwatcher.utils.SessionManager;

public class UserActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView usernameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Initialize SessionManager
        sessionManager = SessionManager.getInstance(this);

        // Liên kết các view
        usernameTextView = findViewById(R.id.username);
        ImageView avatar = findViewById(R.id.avt);
        Button goToMapButton = findViewById(R.id.go_to_map);
        Button goToHomeButton = findViewById(R.id.go_to_home);

        // Lấy thông tin người dùng từ SessionManager
        String username = sessionManager.getUserName();

        // Hiển thị tên người dùng
        if (username != null && !username.isEmpty()) {
            usernameTextView.setText(username);
        } else {
            usernameTextView.setText("Người dùng");
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
