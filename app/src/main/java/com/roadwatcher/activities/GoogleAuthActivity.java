package com.roadwatcher.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;

public class GoogleAuthActivity extends AppCompatActivity {

    private ImageView backButton;
    private EditText emailEditText, passwordEditText;
    private TextView forgotPasswordText;
    private CheckBox rememberMeCheckBox;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_auth);

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
        forgotPasswordText.setOnClickListener(v ->
                Toast.makeText(this, "Tính năng Quên mật khẩu đang được phát triển!", Toast.LENGTH_SHORT).show()
        );

        // Login button functionality
        loginButton.setOnClickListener(v -> authenticateUser());
    }

    private void authenticateUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
        } else if (isValidEmail(email)) {
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            // Add your authentication logic here
        } else {
            Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
