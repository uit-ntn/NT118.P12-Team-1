package com.roadwatcher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;
import com.roadwatcher.utils.SessionManager;

public class UserActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Initialize session manager
        sessionManager = SessionManager.getInstance(this);

        // Set up avatar click listener
        ImageView avatar = findViewById(R.id.avt);
        avatar.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, AccountInfoActivity.class);
            startActivity(intent);
        });

        // Set up bottom navigation buttons
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

    // Method to handle logout click
    public void onLogOut_Click(View view) {
        sessionManager.logout();
        Toast.makeText(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // Method to handle contact support click
    public void onContact_Click(View view) {
        Intent intent = new Intent(this, SupportContactActivity.class);
        startActivity(intent);
    }

    // Method to handle policy click
    public void onPolicyClick(View view) {
        Intent intent = new Intent(this, PrivacyPolicyActivity.class);
        startActivity(intent);
    }

    // Method to handle settings click
    public void onSettingsClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
