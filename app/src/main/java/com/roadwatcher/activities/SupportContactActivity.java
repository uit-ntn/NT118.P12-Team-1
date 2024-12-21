package com.roadwatcher.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.roadwatcher.R;

public class SupportContactActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView contactPhone, contactEmail, contactFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_contact);

        // Initialize Views
        backButton = findViewById(R.id.backButton);
        contactPhone = findViewById(R.id.contactPhone);
        contactEmail = findViewById(R.id.contactEmail);
        contactFacebook = findViewById(R.id.contactFacebook);

        // Set Data (Optional - You can update the text dynamically if needed)
        contactPhone.setText("090xxxxxxx");
        contactEmail.setText("RoadWatcher@gmail.com");
        contactFacebook.setText("Road Watcher");

        // Back Button Listener
        backButton.setOnClickListener(v -> finish()); // Return to the previous screen
    }
}
