package com.roadwatcher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;
import com.roadwatcher.api.ApiClient;
import com.roadwatcher.api.AuthApiService;
import com.roadwatcher.https.LoginRequest;
import com.roadwatcher.https.LoginResponse;
import com.roadwatcher.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordText, googleLoginButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Liên kết các view
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        googleLoginButton = findViewById(R.id.googleText);

        // Khởi tạo SessionManager
        sessionManager = SessionManager.getInstance(this);

        // Kiểm tra trạng thái đăng nhập
        if (sessionManager.isLoggedIn()) {
            navigateToDashboard();
        }

        // Xử lý sự kiện khi nhấn nút login
        loginButton.setOnClickListener(v -> login());

        // Xử lý sự kiện khi nhấn forgot password
        forgotPasswordText.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng Quên mật khẩu đang được phát triển!", Toast.LENGTH_SHORT).show()
        );

        // Xử lý sự kiện khi nhấn login với Google
        googleLoginButton.setOnClickListener(v -> navigateToGoogleAuth());
    }

    private void login() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password);

        // Tạo đối tượng Retrofit
        AuthApiService apiService = ApiClient.getClient().create(AuthApiService.class);
        Call<LoginResponse> call = apiService.loginUser(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String token = loginResponse.getToken();
                    String userId = loginResponse.getUserId();

                    if (token != null && userId != null) {
                        sessionManager.createLoginSession(userId, token, "", email);
                        navigateToDashboard();
                    } else {
                        Toast.makeText(LoginActivity.this, "Phản hồi không hợp lệ từ máy chủ!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorMessage = response.errorBody().string();
                        Log.e("LoginError", errorMessage);
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại, mật khẩu ko đúng", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e("RetrofitError", t.getMessage());
                Toast.makeText(LoginActivity.this, "Lỗi kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToGoogleAuth() {
        Intent intent = new Intent(LoginActivity.this, GoogleAuthActivity.class);
        startActivity(intent);
    }
}
