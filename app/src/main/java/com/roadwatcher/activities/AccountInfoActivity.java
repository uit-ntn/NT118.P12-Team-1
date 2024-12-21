package com.roadwatcher.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;

public class AccountInfoActivity extends AppCompatActivity {

    private ImageView backButton, profilePicture, cameraIcon;
    private TextView accountName, phoneNumber, emailAddress, password;
    private Button changePasswordButton, deleteAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        // Initialize Views
        backButton = findViewById(R.id.backButton);
        profilePicture = findViewById(R.id.profilePicture);
        cameraIcon = findViewById(R.id.cameraIcon);
        accountName = findViewById(R.id.accountName);
        phoneNumber = findViewById(R.id.phoneNumber);
        emailAddress = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);

        // Set Listeners
        backButton.setOnClickListener(v -> finish()); // Back to previous screen

        changePasswordButton.setOnClickListener(v ->
                Toast.makeText(AccountInfoActivity.this, "Change Password Clicked", Toast.LENGTH_SHORT).show()
        );

        deleteAccountButton.setOnClickListener(v ->
                Toast.makeText(AccountInfoActivity.this, "Delete Account Clicked", Toast.LENGTH_SHORT).show()
        );

        cameraIcon.setOnClickListener(v ->
                Toast.makeText(AccountInfoActivity.this, "Edit Profile Picture", Toast.LENGTH_SHORT).show()
        );
    }
}
