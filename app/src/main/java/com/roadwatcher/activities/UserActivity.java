package com.roadwatcher.activities;

import com.roadwatcher.utils.SessionManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;

public class UserActivity extends AppCompatActivity {
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Initialize session manager using singleton
        sessionManager = SessionManager.getInstance(this);

        ImageView avatar = findViewById(R.id.avt);
        avatar.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, AccountInfoActivity.class);
            startActivity(intent);
        });

        Button goToMapButton = findViewById(R.id.go_to_map);
        Button goToHomeButton = findViewById(R.id.go_to_home);

        goToMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, MapActivity.class);
            startActivity(intent);
        });

        goToHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
    }

    public void onLogOut_Click(View view) {
        sessionManager.logout();
        Toast.makeText(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void onContact_Click(View view) {
        Intent intent = new Intent(this, SupportContactActivity.class);
        startActivity(intent);
    }
}
