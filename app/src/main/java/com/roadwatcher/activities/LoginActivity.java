package com.roadwatcher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private Button loginButton, signupButton;
    private CheckBox rememberMeCheckBox;
    private TextView forgotPasswordText;
    private SessionManager sessionManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ các view từ file XML
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        signupButton = findViewById(R.id.signupButton);


        sessionManager = new SessionManager(this);

        // Kiểm tra nếu đã đăng nhập
        if (sessionManager.isLoggedIn()) {
            navigateToMainActivity();
        }

        // Xử lý sự kiện nhấn nút Đăng nhập
        loginButton.setOnClickListener(v -> login());


        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        });

        // Xử lý sự kiện nhấn vào Quên mật khẩu
        forgotPasswordText.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "Quên mật khẩu được nhấn", Toast.LENGTH_SHORT).show();
        });
    }

    private void login() {
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền vào tất cả các trường", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email , password);

        AuthApiService apiService = ApiClient.getClient().create(AuthApiService.class);
        Call<LoginResponse> call = apiService.loginUser(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String token = loginResponse.getToken();
                    String userId = loginResponse.getUserId();

                    sessionManager.createLoginSession(userId, token);

                    navigateToMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LoginActivity", "Lỗi: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Lỗi khi gửi yêu cầu đăng nhập, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
