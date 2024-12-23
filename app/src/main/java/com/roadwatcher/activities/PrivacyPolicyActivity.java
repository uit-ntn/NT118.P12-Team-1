package com.roadwatcher.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private ImageView backButton;
    private Button learnMorePrivacy, learnMoreSecurity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        // Initialize Views
        backButton = findViewById(R.id.backButton);
        learnMorePrivacy = findViewById(R.id.learnMorePrivacy);
        learnMoreSecurity = findViewById(R.id.learnMoreSecurity);

        // Back Button Click
        backButton.setOnClickListener(v -> finish());

        // Learn More Privacy Button Click
        learnMorePrivacy.setOnClickListener(v ->
                Toast.makeText(this, "Chuyển đến nội dung Chính Sách Quyền Riêng Tư!", Toast.LENGTH_SHORT).show()
        );

        // Learn More Security Button Click
        learnMoreSecurity.setOnClickListener(v ->
                Toast.makeText(this, "Chuyển đến nội dung Chính Sách Bảo Mật!", Toast.LENGTH_SHORT).show()
        );
    }
}
