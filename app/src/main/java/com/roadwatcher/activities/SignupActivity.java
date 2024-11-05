package com.roadwatcher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText emailEditText;
    private EditText phoneEditText; // Thêm trường nhập phone
    private Button registerButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Ánh xạ các view từ layout
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText); // Ánh xạ trường phone
        registerButton = findViewById(R.id.registerButton);

        // Xử lý sự kiện cho nút Đăng ký
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        // Lấy dữ liệu từ các trường nhập liệu
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

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

        // Gọi API để đăng ký người dùng
        registerUserWithApi(username, email, phone, password);
    }

    private void registerUserWithApi(String name, String email, String phone, String password) {
        // Khởi tạo Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://road-watcher-server.onrender.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        SignupRequest signupRequest = new SignupRequest(name, email, phone, password);

        apiService.signup(signupRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "Lỗi khi đăng ký", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Định nghĩa interface API cho Retrofit
    interface ApiService {
        @POST("/api/auth/signup")
        Call<Void> signup(@Body SignupRequest signupRequest);
    }

    // Định nghĩa lớp SignupRequest để tạo JSON body
    public class SignupRequest {
        private String name;
        private String email;
        private String phone;
        private String password;

        public SignupRequest(String name, String email, String phone, String password) {
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.password = password;
        }
    }
}
