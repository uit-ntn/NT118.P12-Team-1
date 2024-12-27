package com.roadwatcher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

        // Link views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        googleLoginButton = findViewById(R.id.googleText);

        // Initialize SessionManager
        sessionManager = SessionManager.getInstance(this);

        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToDashboard();
        }

        // Login button click handler
        loginButton.setOnClickListener(v -> login());

        // Forgot password click handler
        forgotPasswordText.setOnClickListener(v -> {
            Toast.makeText(this, "Forgot Password feature is under development!", Toast.LENGTH_SHORT).show();
        });

        // Google login button click handler
        googleLoginButton.setOnClickListener(v -> navigateToGoogleAuth());
    }

    private void login() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password);

        AuthApiService apiService = ApiClient.getClient().create(AuthApiService.class);
        Call<LoginResponse> call = apiService.loginUser(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String token = loginResponse.getToken();
                    String userId = loginResponse.getUserId();

                    // Provide all four arguments to createLoginSession
                    sessionManager.createLoginSession(userId, token, "", email);
                    navigateToDashboard();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed. Please try again!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Server connection error!", Toast.LENGTH_SHORT).show();
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
