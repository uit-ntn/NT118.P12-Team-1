package com.roadwatcher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;
import com.roadwatcher.activities.LoginActivity;
import com.roadwatcher.activities.SignupActivity;

public class MainActivity extends AppCompatActivity {

    private Button loginButton, signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Liên kết các view
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);

        // Xử lý sự kiện cho nút "Đăng nhập"
        loginButton.setOnClickListener(v -> navigateToLogin());

        // Xử lý sự kiện cho nút "Đăng ký"
        signUpButton.setOnClickListener(v -> navigateToSignUp());
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(MainActivity.this, SignupActivity.class);
        startActivity(intent);
    }
}
