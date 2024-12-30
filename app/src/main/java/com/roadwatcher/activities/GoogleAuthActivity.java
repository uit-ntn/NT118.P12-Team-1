package com.roadwatcher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.roadwatcher.R;
import com.roadwatcher.api.ApiClient;
import com.roadwatcher.api.AuthApiService;
import com.roadwatcher.https.LoginResponse;
import com.roadwatcher.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleAuthActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;
    private AuthApiService apiService;
    private SessionManager sessionManager;

    private ImageView backButton;
    private EditText emailEditText, passwordEditText;
    private TextView forgotPasswordText;
    private CheckBox rememberMeCheckBox;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_auth);

        // Initialize API Service and SessionManager
        apiService = ApiClient.getClient().create(AuthApiService.class);
        sessionManager = SessionManager.getInstance(this);

        // Link views
        backButton = findViewById(R.id.backButton);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        loginButton = findViewById(R.id.loginButton);

        // Back button functionality
        backButton.setOnClickListener(v -> finish());

        // Forgot password functionality
        forgotPasswordText.setOnClickListener(v -> {
            Intent intent = new Intent(GoogleAuthActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Login button functionality
        loginButton.setOnClickListener(v -> authenticateUser());

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id)) // Replace with your server client ID
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Google Login button functionality
        Button googleLoginButton = findViewById(R.id.loginButton);
        googleLoginButton.setOnClickListener(v -> signInWithGoogle());
    }

    private void authenticateUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
        } else if (isValidEmail(email)) {
            // Call API for manual authentication (if needed)
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    String idToken = account.getIdToken();
                    sendIdTokenToBackend(idToken);
                }
            } catch (ApiException e) {
                Log.e("GoogleAuthActivity", "Google Sign-In failed", e);
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendIdTokenToBackend(String idToken) {
        apiService.googleCallback(idToken).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Save session details
                    sessionManager.createLoginSession(
                            response.body().getUserId(),
                            response.body().getToken(),
                            null, // No username available
                            null  // No email available
                    );

                    // Navigate to Dashboard
                    Intent intent = new Intent(GoogleAuthActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(GoogleAuthActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("GoogleAuthActivity", "Error during authentication", t);
                Toast.makeText(GoogleAuthActivity.this, "Error during authentication", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
