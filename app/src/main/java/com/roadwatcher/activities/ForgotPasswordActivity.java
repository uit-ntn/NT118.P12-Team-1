package com.roadwatcher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;
import com.roadwatcher.api.ApiClient;
import com.roadwatcher.api.AuthApiService;
import com.roadwatcher.https.ForgotPasswordRequest;
import com.roadwatcher.https.ForgotPasswordResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button forgotButton,loginButton;
    private AuthApiService authApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        forgotButton = findViewById(R.id.forgotButton);
        loginButton = findViewById(R.id.loginButton); // Gán nút "Đăng nhập"


        // Initialize AuthApiService
        authApiService = ApiClient.getClient().create(AuthApiService.class);

        // Set up button click listener
        forgotButton.setOnClickListener(view -> forgotPassword());

        // Set up login button click lister
        loginButton.setOnClickListener(view -> navigateToLogin());
    }

    private void forgotPassword() {
        String email = emailEditText.getText().toString().trim();

        // Validate email
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng điền vào tất cả các trường", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create request object
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        // Make API call
        authApiService.forgotPassword(request).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ForgotPasswordActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi: Không thể cấp lại mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
