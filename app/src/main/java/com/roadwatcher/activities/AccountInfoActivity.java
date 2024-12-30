package com.roadwatcher.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;
import com.roadwatcher.api.ApiClient;
import com.roadwatcher.api.AuthApiService;
import com.roadwatcher.models.User;
import com.roadwatcher.utils.SessionManager;
import com.roadwatcher.https.ResetPasswordRequest;
import com.roadwatcher.https.ResetPasswordResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountInfoActivity extends AppCompatActivity {

    private ImageView backButton, cameraIcon;
    private TextView accountName, phoneNumber, emailAddress, password;
    private Button changePasswordButton, deleteAccountButton;
    private AuthApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        // Link views
        backButton = findViewById(R.id.backButton);
        cameraIcon = findViewById(R.id.ic_camera);
        accountName = findViewById(R.id.accountName);
        phoneNumber = findViewById(R.id.phoneNumber);
        emailAddress = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);

        // Initialize session manager and API service
        sessionManager = SessionManager.getInstance(this);
        apiService = ApiClient.getClient().create(AuthApiService.class);

        // Fetch user info
        fetchUserInfo();

        // Back button functionality
        backButton.setOnClickListener(v -> finish());

        // Camera icon functionality
        cameraIcon.setOnClickListener(v ->
                Toast.makeText(this, "Chỉnh sửa ảnh đại diện", Toast.LENGTH_SHORT).show()
        );

        // Change password button functionality
        changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());

        // Delete account button functionality
        deleteAccountButton.setOnClickListener(v ->
                Toast.makeText(this, "Xóa tài khoản đang được phát triển!", Toast.LENGTH_SHORT).show()
        );
    }

    private void fetchUserInfo() {
        String userId = sessionManager.getUserId();
        String token = sessionManager.getToken();

        if (userId == null || token == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getUserById(userId, "Bearer " + token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Update UI with user data
                    User user = response.body();
                    accountName.setText(user.getName());
                    phoneNumber.setText(user.getPhone());
                    emailAddress.setText(user.getEmail());
                    password.setText("************"); // Hide the actual password
                } else {
                    Toast.makeText(AccountInfoActivity.this, "Failed to fetch user info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("AccountInfoActivity", "Error fetching user info", t);
                Toast.makeText(AccountInfoActivity.this, "Error fetching user info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        EditText newPasswordInput = dialogView.findViewById(R.id.newPasswordInput);
        Button submitPasswordButton = dialogView.findViewById(R.id.submitPasswordButton);

        submitPasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordInput.getText().toString().trim();

            if (newPassword.isEmpty()) {
                Toast.makeText(this, "Mật khẩu mới không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi API đổi mật khẩu
            changePassword(newPassword, dialog);
        });
    }

    private void changePassword(String newPassword, AlertDialog dialog) {
        String token = sessionManager.getToken();

        if (token == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthApiService apiService = ApiClient.getClient().create(AuthApiService.class);
        ResetPasswordRequest request = new ResetPasswordRequest(token, newPassword);

        apiService.resetPassword(request).enqueue(new Callback<ResetPasswordResponse>() {
            @Override
            public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AccountInfoActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(AccountInfoActivity.this, "Đổi mật khẩu thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                Toast.makeText(AccountInfoActivity.this, "Lỗi kết nối. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
