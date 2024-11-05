package com.roadwatcher.activities;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText emailEditText;
    private Button registerButton;
    private Button googleSignupButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Ánh xạ các view từ layout
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        emailEditText = findViewById(R.id.emailEditText);
        registerButton = findViewById(R.id.registerButton);

        // Xử lý sự kiện cho nút Đăng ký
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Xử lý sự kiện cho nút Đăng ký với Google
        googleSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpWithGoogle();
            }
        });
    }

    private void registerUser() {
        // Lấy dữ liệu từ các trường nhập liệu
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        // Kiểm tra các trường nhập liệu
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Vui lòng nhập tên đăng nhập");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Vui lòng nhập email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Vui lòng nhập mật khẩu");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Mật khẩu không khớp");
            return;
        }

        // TODO: Thực hiện đăng ký người dùng (Gọi API hoặc lưu trữ dữ liệu)
        Toast.makeText(SignupActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

        // Điều hướng về màn hình đăng nhập sau khi đăng ký thành công
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void signUpWithGoogle() {
        // TODO: Thực hiện logic đăng ký với Google
        Toast.makeText(SignupActivity.this, "Đăng ký với Google đang được phát triển", Toast.LENGTH_SHORT).show();
    }
}