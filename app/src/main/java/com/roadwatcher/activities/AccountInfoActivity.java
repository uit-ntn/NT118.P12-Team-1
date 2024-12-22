package com.roadwatcher.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;

public class AccountInfoActivity extends AppCompatActivity {

    private ImageView backButton, cameraIcon;
    private TextView accountName, phoneNumber, emailAddress, password;
    private Button changePasswordButton, deleteAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        // Link views
        backButton = findViewById(R.id.backButton);
        cameraIcon = findViewById(R.id.cameraIcon);
        accountName = findViewById(R.id.accountName);
        phoneNumber = findViewById(R.id.phoneNumber);
        emailAddress = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);

        // Back button functionality
        backButton.setOnClickListener(v -> finish());

        // Camera icon functionality
        cameraIcon.setOnClickListener(v ->
                Toast.makeText(this, "Chỉnh sửa ảnh đại diện", Toast.LENGTH_SHORT).show()
        );

        // Change password button functionality
        changePasswordButton.setOnClickListener(v ->
                Toast.makeText(this, "Đổi mật khẩu đang được phát triển!", Toast.LENGTH_SHORT).show()
        );

        // Delete account button functionality
        deleteAccountButton.setOnClickListener(v ->
                Toast.makeText(this, "Xóa tài khoản đang được phát triển!", Toast.LENGTH_SHORT).show()
        );
    }
}
